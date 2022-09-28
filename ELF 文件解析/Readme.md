
# ELF 文件格式分析



论坛中的少有对ELF文件的动态分析，找了一些资料给补上。了解ELF文件的加载和动态执行流程，是学习安全知识和加固的前提。



## 参考

[_周壑](https://space.bilibili.com/37877654):  https://www.bilibili.com/video/BV1no4y1U7C6

ELF文件格式解析 https://paper.seebug.org/papers/Archive/refs/elf/Understanding_ELF.pdf

aosp 源码： https://github.com/aosp-mirror/platform_bionic/tree/donut-release



## 可执行文件格式概述



不同平台的可执行文件是不一样的

* android中的可执行文件是dex文件格式

  * ios是 Mach-O格式
  * windows是PE文件格式
  * linux是ELF文件格式, 相关定义在“/usr/include/elf.h”文件里。

* Linux 文件中的ELF(Executable and Linking Format)文件包括:  

  * 可执行文件(Executable File .exec ): 经过链接的、可执行的目标文件，通常也被称为程序

  * 可重定位文件 / 待重定位文件 (Relocatable Object File)(.o) :由源文件编译而成且尚未链接的目标文件，通常以“.o”作为扩展名

  * 共享位目标文件 / 动态链接库文件 (Shared Object File) : 它在以下两种情况下被使用：第一，在连接过程中与其它动态链接库或可重定位文件一起构建新的目标文件；第二，在可执行文件被加载的过程中，被动态链接到新的进程中，成为运行代码的一部分。

    > 核心转储文件 (Core Dump FIle): 进程意外终止时进程地址空间的转储，也是ELF文件的一种。使用gdb读取这类文件可以辅助调试和查找程序崩溃的原因。



ELF文件 有两种视角可供选择，一种是链接视角，通过节（Section）来进行划分；另一种是运行视角，通过段（Segment）来进行划分。

![image-20220724161650543](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220724161650543.png)

* ELF 文件头 (ELF header)

  位于文件的最开始处，包含有整个文件的结构信息。

* 程序头表 (program header table)

  在运行过程中是必须的，在连接过程中是可选的，因为它的作用是告诉系统如何创建进程的镜像。

* 节头表 (section header table)

  包含有文件中所有“节”的信息。在连接视图中，“节头表”是必须存在的，文件里的每一个“节”都需要在“节头表”中有一个对应的注册项，这个注册项描述了节的名字、大小等等。



## elf 文件静态分析



### ELF文件头

相关定义在“/usr/include/elf.h”

```
typedef struct
{
  unsigned char	e_ident[16];			/* 魔术字符（7f 45(E) 4c(L) 46(F) ...)*/
  Elf64_Half	e_type;					/* 目标文件类型 */
  Elf64_Half	e_machine;		/* Architecture */
  Elf64_Word	e_version;		/* Object file version */
  Elf64_Addr	e_entry;		/* Entry point virtual address */
  Elf64_Off	e_phoff;		/* Program header table file offset */
  Elf64_Off	e_shoff;		/* Section header table file offset */
  Elf64_Word	e_flags;		/* Processor-specific flags */
  Elf64_Half	e_ehsize;		/* ELF header size in bytes */
  Elf64_Half	e_phentsize;		/* Program header table entry size */
  Elf64_Half	e_phnum;		/* Program header table entry count */
  Elf64_Half	e_shentsize;		/* Section header table entry size */
  Elf64_Half	e_simage-20220726214233481hnum;		/* Section header table entry count */
  Elf64_Half	e_shstrndx;		/* Section header string table index */
} Elf64_Ehdr;
```

描述整个文件的一些基本信息，例如ELF文件类型、版本/ABI版本、目标机器、程序入口、段表和节表的位置和长度等。

| 字段            | 值                                                           |
| --------------- | ------------------------------------------------------------ |
| **e_ident[16]** | ELF 标识，16字节数组，它的各个索引位置的字节数据有固定的含义。 |
| * EI_MAG[4]     | 魔数，用于标识这是一个 ELF 文件。这四个字节存放的内容是固定的<br />![image-20220724220532596](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220724220532596.png)<br />值得注意的是文件头部存在魔术字符（7f 45 4c 46)，即字符串“\177ELF”，当文件被映射到内存时，可以通过搜索该字符确定映射地址，这在dump内存时非常有用。 |
| * EI_CLASS      | 指明文件的类型(适应于多种字长大小的系统)<br />![image-20220724220648119](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220724220648119.png) |
| * EI_DATA       | 指明了目标文件中的数据编码格式(一般为小端)<br />![image-20220724220907051](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220724220907051.png) |
| * EI_VERSION    | 指明 ELF 文件头的版本                                        |
| * EI_PAD[9]     | 不使用                                                       |
| **e_type**      | 目标文件类型<br />![image-20220724222506015](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220724222506015.png) |
| e_machine       | 指定该文件适用的处理器体系结构<br />名字			值			意义<br/>EM_NONE		0			未知体系结构<br/>EM_X86_64 	0x3E(62)	X86-64 |
| e_version       | 此字段指明目标文件的版本                                     |
| **e_entry**     | 此字段指明程序入口的虚拟地址。即当文件被加载到进程空间里后，入口程序在进程地址空间里的地址。对于可执行程序文件来说，当 ELF 文件完成加载之后，程序将从这里开始运行；而对于其它文件来说，这个值应该是 0。 |
| e_phoff         | 此字段指明程序头表(program header table)开始处在文件中的偏移量。如果没有程序头表，该值应设为 0。 |
| e_shoff         | 此字段指明节头表(section header table)开始处在文件中的偏移量。如果没有节头表，该值应设为 0。 |
| e_flags         | 处理器特定的标志位                                           |
| e_ehsize        | 此字段表明 ELF 文件头的大小，以字节为单位。                  |
| e_phentsize     | 此字段表明在程序头表中每一个表项的大小，以字节为单位。       |
| e_phnum         | 此字段表明程序头表中总共有多少个表项。                       |
| e_shentsize     | 此字段表明在节头表中每一个表项的大小，以字节为单位。         |
| e_shnum         | 此字段表明节头表中总共有多少个表项。                         |
| e_shstrndx      | 节头表中与节名字表相对应的表项的索引。如果文件没有节名字表，此值应 设置为 SHN_UNDEF。 |



使用代码查看信息

![image-20220924140703282](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220924140703282.png)



### 节头表（Section header table）

#### 节头表结构

节头表（Section header table）: 保存节的信息，表的每一项都是一个Elf64_Shdr结构体（也称为节描述符）,通过每一个表项可以定位到对应的节，记录了节的名字、长度、偏移、读写权限等信息。

* 文件头中， e_shoff字段记录节头表相对于文件开始处的偏移量， e_shnum 成员指明节头表中包含多少个表项；e_shentsize 成员指明了每一个表项的大小
* 节头表对于程序运行并不是必须的，因为它与程序内存布局无关，是程序头表的任务，所以常有程序去除节头表，以增加反编译器的分析难度。
* 目标文件中的每一个节一定对应有一个节头(section header)，节头中有对节的描述信息；但有的节头可以没有对应的节，而只是一个空的头。

每一个节头表项的结构体如下所示： 

```
typedef struct
{
  Elf64_Word	sh_name;		/* Section name (string tbl index) */
  Elf64_Word	sh_type;		/* Section type */
  Elf64_Xword	sh_flags;		/* Section flags */
  Elf64_Addr	sh_addr;		/* Section virtual addr at execution */
  Elf64_Off	sh_offset;		/* Section file offset */
  Elf64_Xword	sh_size;		/* Section size in bytes */
  Elf64_Word	sh_link;		/* Link to another section */
  Elf64_Word	sh_info;		/* Additional section information */
  Elf64_Xword	sh_addralign;		/* Section alignment */
  Elf64_Xword	sh_entsize;		/* Entry size if section holds table */
} Elf64_Shdr;
```



| 字段         | 意义                                                         |
| ------------ | ------------------------------------------------------------ |
| sh_name      | 本节的名字。整个名字的字符串并不存储在这里，它仅是一个索引号，指向“字符串表”节中的某个位置，那里存储了一个以’\0’结尾的字符串。 |
| sh_type      | 本节的类型, 参考 [节头表（Section header table）](#节头表（Section header table）) |
| sh_flags     | 本节的一些属性，由一系列标志比特位组成，各个比特定义了节的不同属 性，当某种属性被设置时，相应的标志位被设为 1，反之则设为 0。未定义的标志 位被全部置 0. 参考[节头表（Section header table）](#节头表（Section header table）) |
| sh_addr      | 如果本节的内容需要映射到进程空间中去，此成员指定映射的起始地址；如果不需要映射，此值为 0。 |
| sh_offset    | 指明了本节所在的位置，该值是节的第一个字节在文件中的位置，即相对于 文件开头的偏移量。单位是字节。如果该节的类型为 SHT_NOBITS 的话，表明这 一节的内容是空的，节并不占用实际的空间，这时 sh_offset 只代表一个逻辑上的 位置概念，并不代表实际的内容。 |
| sh_size      | 指明节的大小，单位是字节。如果该节的类型为 SHT_NOBITS，此值仍然可 能为非零，但没有实际的意义。 |
| sh_link      | 此成员是一个索引值，指向节头表中本节所对应的位置。根据节的类型不 同，本成员的意义也有所不同，具体见下表。 |
| sh_info      | 附加信息  参考[节头表（Section header table）](#节头表（Section header table）) |
| sh_addralign | 对齐字节                                                     |
| sh_entsize   | 有一些节的内容是一张表，其中每一个表项的大小是固定的，比如符号表。 对于这种表来说，本成员指定其每一个表项的大小。如果此值为 0 则表明本节内容 不是这种表格码（.text）： 保存可执行的机器指令<br/><br/>数据（.data）： 保存已初始化的全局变量和局部静态变量<br/><br/>BSS（.bss）： BSS节则用于保存未初始化的全局变量和局部静态变量 |



* sh_type 本节的类型。

  | 名字         | 值         | 意义                                                         |
  | ------------ | ---------- | ------------------------------------------------------------ |
  | SHT_NULL     | 0          | 没有对应的节                                                 |
  | SHT_PROGBITS | 1          | 程序定义节                                                   |
  | SHT_SYMTAB   | 2          | 动态连接完整的符号表                                         |
  | SHT_DYNSYM   | 11         | 较小的符号表，专门用于动态连接                               |
  | SHT_STRTAB   | 3          | 字符串表                                                     |
  | SHT_RELA     | 4          | 重定位节                                                     |
  | SHT_HASH     | 5          | 哈希表，所有参与动态连接的目标文件都必须要包含<br/>一个符号哈希表。 |
  | SHT_DYNAMIC  | 6          | 动态连接信息                                                 |
  | SHT_NOTE     | 7          | 标记本文件                                                   |
  | SHT_NOBITS   | 8          | 此值表明这一节的内容是空的，节并不占用实际的空间             |
  | SHT_REL      | 9          | 重定位节，含有带明确加数的重定位项                           |
  | SHT_SHLIB    | 10         | 保留值                                                       |
  | SHT_LOPROC   | 0x70000000 | 为特殊处理器保留的节类型索引值的下边界                       |
  | SHT_HIPROC   | 0x7fffffff | 为特殊处理器保留的节类型索引值的上边界                       |
  | SHT_LOUSER   | 0x80000000 | 为应用程序保留节类型索引值的下边界                           |
  | SHT_HIUSER   | 0xffffffff | 为应用程序保留节类型索引值的上边界                           |

* sh_flags 本节的一些属性，由一系列标志比特位组成，各个比特定义了节的不同属 性，当某种属性被设置时，相应的标志位被设为 1，反之则设为 0。未定义的标志 位被全部置 0。

  | 名字          | 值         | 意义                                                         |
  | ------------- | ---------- | ------------------------------------------------------------ |
  | SHF_WRITE     | 0x1        | 本节所包含的内容在进程运行过程中是可写的                     |
  | SHF_ALLOC     | 0x2        | 本节内容在进程运行过程中要占用内存单元。并不是所有节都会占用实际的内存，有一些起控制作用的节，在目标文件映射到进程空间时，并不需要占用内存。 |
  | SHF_EXECINSTR | 0x4        | 此节内容是指令代码                                           |
  | SHF_MASHPROC  | 0xf0000000 | 保留                                                         |


* sh_info 附加信息

  | sh_type                | sh_link                                        | sh_info                                                      |
  | ---------------------- | ---------------------------------------------- | ------------------------------------------------------------ |
  | SHT_DYNAMIC            | 用于本节中项目的字符串表在节头表中相应的索引值 | 0本节包含动态连接信息，并且可能有 SHF_ALLOC 和 SHF_WRITE 等属性。 是否具有 SHF_WRITE 属性取决于操作系统和处理器。 |
  | SHT_HASH               | 用于本节中哈希表的符号表在节头表中相应的索引值 | 0                                                            |
  | SHT_REL /SHT_RELA      | 相应符号表在节头表中的索引值                   | 本重定位节所应用到目标节在节头表中的索引值                   |
  | SHT_SYMTAB /SHT_DYNSYM | 相关字符串表的节头索引                         | 符号表中最后一个本地符号的索引值加 1                         |
  | 其它                   | SHN_UNDEF                                      | 0                                                            |




代码解析如下：

![image-20220924141447338](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220924141447338.png)





#### 常见的表的属性

| 名字               | 类型             | 属性                      | 意义                                                         |
| ------------------ | ---------------- | ------------------------- | ------------------------------------------------------------ |
| .init              | SHT_PROGBITS     | SHF_ALLOC + SHF_EXECINSTR | 此节包含进程初始化时要执行的程序指令。当程序开始运行时，系统会在进 入主函数之前执行这一节中的代码。 |
| .fini              | SHT_PROGBITS     | SHF_ALLOC + SHF_EXECINSTR | 此节包含进程终止时要执行的程序指令。当程序正常退出时，系统会执行这 一节中的代码。 |
| .bss               | SHT_NOBITS       | SHF_ALLOC+SHF_WRITE       | 本节中包含目标文件中未初始化的全局变量。一般情况下，可执行程序在开 始运行的时候，系统会把这一段内容清零。但是，在运行期间的 bss 段是由系统初 始化而成的，在目标文件中.bss 节并不包含任何内容，其长度为 0，所以它的节类 型为 SHT_NOBITS。 |
| .comment           | SHT_PROGBITS     | 无                        | 本节包含版本控制信息                                         |
| .data/.data1       | SHT_PROGBITS     | SHF_ALLOC+SHF_WRITE       | 这两个节用于存放程序中被初始化过的全局变量。在目标文件中，它们是占 用实际的存储空间的，与.bss 节不同。 |
| .debug             | SHT_PROGBITS     | 无                        | 调试信息，内容格式没有统一规定。所有以”.debug”为前缀的节名 字都是保留 |
| .line              | SHT_PROGBITS     | 无                        | 本节也是一个用于调试的节，它包含那些调试符号的行号，为程序指令码与 源文件的行号建立起联系。其内容格式没有统一规定。 |
| .dynamic           | SHT_DYNAMIC      | 见下文                    | 本节包含动态连接信息，并且可能有 SHF_ALLOC 和 SHF_WRITE 等属性。 是否具有 SHF_WRITE 属性取决于操作系统和处理器。 |
| .dynstr            | SHT_STRTAB       | SHF_ALLOC                 | 此节含有用于动态连接的字符串，一般是那些与符号表相关的名字   |
| .dynsym            | SHT_DYNSYM       | SHF_ALLOC                 | 此节含有动态连接符号表                                       |
| .got               | SHT_PROGBITS     | SHF_ALLOC + SHF_WRITE     | 此节包含全局偏移量表                                         |
| .hash              | SHT_HASH         | SHF_ALLOC                 | 本节包含一张符号哈希表                                       |
| .interp            | SHT_PROGBITS     | 见下文                    | 此节含有 ELF 程序解析器的路径名。如果此节被包含在某个可装载的段中， 那么本节的属性中应置 SHF_ALLOC 标志位，否则不置此标志。 |
| .note              | SHT_NOTE         | 无                        | 注释节                                                       |
| .plt               | SHT_PROGBITS     | SHF_ALLOC + SHF_EXECINSTR | 此节包含函数连接表                                           |
| .relname/.relaname | SHT_REL/SHT_RELA | 见下文                    | 这两个节含有重定位信息。如果此节被包含在某个可装载的段中，那么本节 的属性中应置 SHF_ALLOC 标志位，否则不置此标志。注意，这两个节的名字 中”name”是可替换的部分，执照惯例，对哪一节做重定位就把”name”换成哪一节 的名字。比如，.text 节的重定位节的名字将是.rel.text 或.rela.text。 |
| .rodata/.rodata1   | SHT_PROGBITS     | SHF_ALLOC                 | 本节包含程序中的只读数据，在程序装载时，它们一般会被装入进程空间中 那些只读的段中去 |
| .shstrtab          | SHT_STRTAB       | 无                        | 本节是“节名字表”，含有所有其它节的名字                       |
| .strtab            | SHT_STRTAB       | 见下文                    | 本节用于存放字符串，主要是那些符号表项的名字。如果一个目标文件有一 个可装载的段，并且其中含有符号表，那么本节的属性中应该有 SHF_ALLOC |
| .symtab            | SHT_SYMTAB       | 见下文                    | 本节用于存放符号表。如果一个目标文件有一个可载入的段，并且其中含有 符号表，那么本节的属性中应该有 SHF_ALLOC。 |
| .text              | SHT_PROGBITS     | SHF_ALLOC + SHF_EXECINSTR | 本节包含程序指令代码                                         |





#### .dynsym & .symtab 符号表

* 符号表记录了目标文件中所用到的所有符号信息，通常分为.dynsym和.symtab，前者是后者的子集。

* .dynsym保存了引用自外部文件的符号，只能在运行时被解析

* .symtab还保存了本地符号，用于调试和链接。

* 目标文件通过一个符号在表中的索引值来使用该符号。索引值从0开始计数，但值为0的表项不具有实际的意义，它表示未定义的符号STN_UNDEF。每个符号都有一个符号值（symbol value），对于变量和函数，该值就是符号的地址。


符号表项的格式定义（Elf64_Sym结构体）， 如下所示。

```
typedef struct
{
  Elf64_Word	st_name;		/* Symbol name (string tbl index) */
  unsigned char	st_info;		/* Symbol type and binding */
  unsigned char st_other;		/* Symbol visibility */
  Elf64_Section	st_shndx;		/* Section index */
  Elf64_Addr	st_value;		/* Symbol value */
  Elf64_Xword	st_size;		/* Symbol size */
} Elf64_Sym;
```



| 字段     | 意义                                                         |
| -------- | ------------------------------------------------------------ |
| st_name  | 符号的名字。但它并不是一个字符串，而是一个指向字符串表的索引值。如果此值为 0，那么此符号无名字。 |
| st_value | 符号的值。这个值其实没有固定的类型，它可能代表一个数值，也可以是一 个地址，具体是什么要看上下文。具体参考 [.dynsym & .symtab 符号表](#.dynsym & .symtab 符号表) |
| st_size  | 符号的大小。各种符号的大小各不相同，比如一个对象的大小就是它实际占 用的字节数。如果一个符号的大小为 0 或者大小未知，则这个值为 0。 |
| st_info  | 符号的类型和属性。st_info 由一系列的比特位构成，标识了“符号绑定 (symbol binding)”、“符号类型(symbol type)”和“符号信息(symbol infomation)” 三种属性。下面几个宏分别用于读取这三种属性值。具体参考 [.dynsym & .symtab 符号表](#.dynsym & .symtab 符号表) |
| st_other | 本数据成员目前暂未使用，在目标文件中一律赋值为 0。           |
| st_shndx | 任何一个符号表项的定义都与某一个“节”相联系，因为符号是为节而定 义，在节中被引用。本数据成员即指明了相关联的节。本数据成员是一个索引值， 它指向相关联的节在节头表中的索引。在重定位过程中，节的位置会改变，本数据 成员的值也随之改变，继续指向节的新位置。具体参考 [.dynsym & .symtab 符号表](#.dynsym & .symtab 符号表) |



> 符号表的第一项，即索引值为 STN_UNDEF(0)的这项，其内容与其它项不同
>
> | 名字     | 值        | 意义     |
> | -------- | --------- | -------- |
> | st_name  | 0         | 无效名字 |
> | st_value | 0         | 0值      |
> | st_size  | 0         | 无效大小 |
> | st_info  | 0         | 无效类型 |
> | st_other | 0         |          |
> | st_shndx | SHN_UNDEF | 无对应节 |
>
> 

- st_value 符号的值。这个值其实没有固定的类型，它可能代表一个数值，也可以是一 个地址，具体是什么要看上下文。

  | 文件类型               | st_name          | st_value的意义                                               |
  | ---------------------- | ---------------- | ------------------------------------------------------------ |
  | 重定位文件             | SHN_COMMON       | 是这个节内容的字节对齐数                                     |
  | 重定位文件             | 普通的字符串索引 | 该符号的起始地址在其所在节中的偏移量，而其所在的节的索引由st_shndx 给出 |
  | 可执行文件和共享库文件 | 普通的字符串索引 | st_value 不再是一个节内的偏移量，而是一个虚拟地址，直接指向符号所在的内存位置 |

  如果一个可执行文件中含有一个共享库文件函数的引用，那么那个共享目标文件的符号表应该含有这个 函数的符号。符号表的 st_shndx 成员值为 SHN_UNDEF，这就告诉了动态连接 器，这个函数的符号定义并不在可执行文件中。如果已经在可执行文件中给这个符 号申请了一个函数连接表项，而且符号表项的 st_value 成员不是 0，那么 st_value 值就将是函数连接表项中第一条指令的地址。否则，st_value 成员是 0。这个函数 连接表项地址被动态连接器用来解析函数地址。

- st_info 符号的类型和属性。st_info 由一系列的比特位构成，标识了“符号绑定 (symbol binding)”、“符号类型(symbol type)”和“符号信息(symbol infomation)” 三种属性。下面几个宏分别用于读取这三种属性值。

  ```
  #define ELF32_ST_BIND(i) ((i)>>4)
  #define ELF32_ST_TYPE(i) ((i)&0xf)
  #define ELF32_ST_INFO(b,t) (((b)<<4)+((t)&0xf))
  ```

  - 符号绑定（ELF32_ST_BIND） 

    ```
    名字 | 值
    STB_LOCAL | 0
    STB_GLOBAL  | 1
    STB_WEAK | 2
    STB_LOPROC | 13
    STB_HIPROC | 15
    ```

    | 名字       | 值   | 意义                                                         |
    | ---------- | ---- | ------------------------------------------------------------ |
    | STB_LOCAL  | 0    | 表明本符号是一个本地符号。它只出现在本文件中，在本文件外该符号 无效。所以在不同的文件中可以定义相同的符号名，它们之间不会互相影 响。 |
    | STB_GLOBAL | 1    | 表明本符号是一个全局符号。当有多个文件被连接在一起时，在所有文 件中该符号都是可见的。正常情况下，在一个文件中定义的全局符号，一 定是在其它文件中需要被引用，否则无须定义为全局。 |
    | STB_WEAK   | 2    | 类似于全局符号，但是相对于 STB_GLOBAL，它们的优先级更低。 全局符号(global symbol)和弱符号(weak symbol)在以下两方面有区别：<br />- 当连接编辑器把若干个可重定位目标文件连接起来时，同名的 STB_GLOBAL 符号不允许出现多次。而如果在一个目标文件中已经定义 了一个全局的符号(global symbol)，当一个同名的弱符号(weak symbol)出 现时，并不会发生错误。连接编辑器会以全局符号为准，忽略弱符号。与 全局符号相似，如果已经存在的是一个公用符号，即 st_shndx 域为 SHN_COMMON 值的符号，当一个同名的弱符号(weak symbol)出现时， 也不会发生错误。连接编辑器会以公用符号为准，忽略弱符号。<br />- 在查找符号定义时，连接编辑器可能会搜索存档的库文件。如 果是查找全局符号，连接编辑器会提取包含该未定义的全局符号的存档成 员，存档成员可能是一个全局的符号，也可能是弱符号；而如果是查找弱 符号，连接编辑器不会去提取存档成员。未解析的弱符号值为 0。 |
    | STB_LOPROC | 13   |                                                              |
    | STB_HIPROC | 15   |                                                              |

  - 符号类型(ELF32_ST_TYPE) 

    | 名字        | 值   | 意义                                                         |
    | ----------- | ---- | ------------------------------------------------------------ |
    | STT_NOTYPE  | 0    | 本符号类型未指定                                             |
    | STT_OBJECT  | 1    | 本符号是一个数据对象，比如变量、数组等                       |
    | STT_FUNC    | 2    | 本符号是一个函数，或者其它的可执行代码。函数符号在共享目标文件 中有特殊的意义。当另外一个目标文件引用一个共享目标文件中的函数符 号时，连接编辑器为被引用符号自动创建一个连接表项。非 STT_FUNC 类型的共享目标符号不会通过这种连接表项被自动引用 |
    | STT_SECTION | 3    | 本符号与一个节相关联，用于重定位，通常具有 STB_LOCAL 属性    |
    | STT_FILE    | 4    | 本符号是一个文件符号，它具有 STB_LOCAL 属性，它的节索引值是 SHN_ABS。在符号表中如果存在本类符号的话，它会出现在所有 STB_LOCAL 类符号的前部。 |
    | STT_LOPROC  | 13   | 这一区间的符号类型为特殊处理器保留                           |
    | STT_HIPROC  | 15   | 这一区间的符号类型为特殊处理器保留                           |

- st_shndx 任何一个符号表项的定义都与某一个“节”相联系，因为符号是为节而定 义，在节中被引用。本数据成员即指明了相关联的节。本数据成员是一个索引值， 它指向相关联的节在节头表中的索引。在重定位过程中，节的位置会改变，本数据 成员的值也随之改变，继续指向节的新位置。当本数据成员指向下面三种特殊的节 索引值时，本符号具有如下特别的意义：

  | 值             | 意义                                                         |
  | -------------- | ------------------------------------------------------------ |
  | 普通的节的索引 | 指明了相关联的节                                             |
  | SHN_ABS        | 符号的值是绝对的，具有常量性，在重定位过程中，此值不需要改变。 |
  | SHN_COMMON     | 本符号所关联的是一个还没有分配的公共节，本符号的值规定了其内容的 字节对齐规则，与 sh_addralign 相似。也就是说，连接器会为本符号分配存储 空间，而且其起始地址是向 st_value 对齐的。本符号的值指明了要分配的字 节数。 |
  | SHN_UNDEF      | 当一个符号指向第 1 节(SHN_UNDEF)时，表明本符号在当前目标文件中 未定义，在连接过程中，连接器会找到此符号被定义的文件，并把这些文件 连接在一起。本文件中对该符号的引用会被连接到实际的定义上去。 |

  

代码解析结果如下：

![image-20220924141948525](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220924141948525.png)







### 程序头表(program header table)

可执行文件和共享目标文 件（动态连接库）是程序的静态存储形式。要执行一个程序，系统要先把相应的可 执行文件和动态连接库装载到进程空间中，这样形成一个可运行的进程的内存空间 布局，也可以称它为“进程镜像”。一个已装载完成的进程空间会包含多个不同的 “段(segment)”，比如代码段(text segment)，数据段(data segment)，堆栈段(stack segment)等等。



#### 程序头结构

描述一下与运行程序相关的目标文件结构, 它指明了文件中各个段的位置，还包含一些用于创建内存镜像的必要内容。

* 程序头表(program header table)是一个数组， 数组中的每一个元素称为“程序头(program header)”，每一个程序头描述了一个 “段(segment)”或者一块用于准备执行程序的信息。
* 一个目标文件中的“段 (segment)”包含一个或者多个“节(section)”。
* 程序头只对可执行文件或共享目标 文件有意义，对于其它类型的目标文件，该信息可以忽略。
* 在目标文件的文件头 (elf header)中，e_phentsize 和 e_phnum 成员指定了程序头的大小 。

程序头的结构

```
typedef struct {
    Elf32_Word p_type;
    Elf32_Off p_offset;
    Elf32_Addr p_vaddr;
    Elf32_Addr p_paddr;
    Elf32_Word p_filesz;
    Elf32_Word p_memsz;
    Elf32_Word p_flags;
    Elf32_Word p_align;
} Elf32_Phdr;
```



| 字段     | 含义                                                         |
| -------- | ------------------------------------------------------------ |
| p_type   | 此数据成员说明了本程序头所描述的段的类型，或者如何解析本程序头的信息 参考： [程序头结构](#程序头结构) |
| p_offset | 段内容的开始位置相对于文件 开头的偏移量。                    |
| p_vaddr  | 此数据成员给出本段内容的开始位置在进程空间中的虚拟地址。     |
| p_paddr  | 描述了物理地址相关，在应用层无作用。                         |
| p_filesz | 数据成员给出本段内容在文件中的大小，单位是字节，可以是 0。p_offset描述了段在文件中的偏移。 |
| p_memsz  | 此数据成员给出本段内容在内容镜像中的大小，单位是字节，可以是 0 |
| p_flags  | 此成员描述了段的标志<br />![image-20220727135220543](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220727135220543.png) |
| p_align  | 描述了对齐。对于可加载的段 p_vaddr和p_offset取值必须是合适的。此成员给出了段在文件中和内存中如何对齐。数值 0 1 标识不需要对齐。否则就必须是2的倍数。 p_vaddr和p_offset在取模后应该相等。 |

* p_type 此数据成员说明了本程序头所描述的段的类型，或者如何解析本程序头的信息。

  | 名字        | 值         | 意义                                                         |
  | ----------- | ---------- | ------------------------------------------------------------ |
  | PT_NULL     | 0          | 此类型表明本程序头是未使用的，本程序头内的其它成员值均无意义。具 有此种类型的程序头应该被忽略。 |
  | **PT_LOAD** | 1          | 此类型表明本程序头指向一个可装载的段。段的内容会被从文件中拷贝到 内存中。一般来说，一个动态链接的ELF可执行文件通常包含两个可装载的段。 段类型都为PT_LOAD<br />-  一个是存放程序代码的text段<br />- 另一个是存放全局变量和动态链接信息的data段。 |
  | PT_DYNAMIC  | 2          | 此类型表明本段指明了动态连接的信息。                         |
  | PT_INTERP   | 3          | 本段指向了一个以”null”结尾的字符串，这个字符串是一个 ELF 解析器的 路径。这种段类型只对可执行程序有意义，当它出现在共享目标文件中时， 是一个无意义的多余项。在一个 ELF 文件中它最多只能出现一次，而且必须 出现在其它可装载段的表项之前。 |
  | PT_NOTE     | 4          | 本段指向了一个以”null”结尾的字符串，这个字符串包含一些附加的信息。 |
  | PT_SHLIB    | 5          | 该段类型是保留的，而且未定义语法。UNIX System V 系统上的应用程序 不会包含这种表项。 |
  | PT_PHDR     | 6          | 此类型的程序头如果存在的话，它表明的是其自身所在的程序头表在文件 或内存中的位置和大小。这样的段在文件中可以不存在，只有当所在程序头 表所覆盖的段只是整个程序的一部分时，才会出现一次这种表项，而且这种 表项一定出现在其它可装载段的表项之前。 |
  | PT_LOPROC   | 0x70000000 | 类型值在这个区间的程序头是为特定处理器保留的                 |
  | PT_HIPROC   | 0x7fffffff | 类型值在这个区间的程序头是为特定处理器保留的                 |

  所有程序头的段类型域 p_type 都是可选项，不是必须存在的。

  

代码分析结果如下

![image-20220924142312327](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220924142312327.png)



#### 常见的段的结构



##### 代码段text segment (.text) 

代码段(.text)或直译为“文本段”，包含的是只读的指令和数据，一般情况下会 包含以下这些节。不过这里给出的只是一个典型的例子，一个实际的更复杂的代码 段可能包含更多的节。

| .text    |
| -------- |
| .rodata  |
| .hash    |
| .dynsym  |
| .dynstr  |
| .plt     |
| .rel.got |

##### 数据段(data segment)

数据段(data segment)包含可写的数据和指令，典型的数据段包含以下节。

| 数据段(Data Segment) |                                                              |
| -------------------- | ------------------------------------------------------------ |
| .data                |                                                              |
| .dynamic             |                                                              |
| .got                 | .got 节和.plt 节也含 有与与地址无关的代码和动态连接相关的信息。在上面的例子中，虽然.plt 节只出 现在代码段中，但实际上，它也可以出现在数据段中。 |
| .bss                 | .bss 节的类型为 SHT_NOBITS，即它在目标文件中不占空间， 但它在段中，即在进程空间中却会占有一席之地。一般地，未初始化的全局变量会存放在.bss 节中，而整个.bss 节会出现在段的最末尾，也正是因为这样，段的内存 空间大小(p_memsz)可能会比它在文件中的大小(p_filesz)大一些。 |





## ELF 加载流程动态分析

### ELF 由谁加载

ELF 文件的动态加载由 /system/bin/linker(旧版) ld.so(新版) 加载， 该加载器由段表[1]指定

![image-20220922170122204](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220922170122204.png)

/system/bin/linker 主要功能为

1. 映射要执行的ELF到内存中
2. 为ELF文件导入需要的模块
3. 重定位

源码分析整个加载过程： https://github.com/aosp-mirror/platform_bionic/tree/donut-release



### 映射内存

加载ELF第一步： 映射内存

linker 将类型为PT_LOAD的programe通过map映射到内存中, 如下所示

![image-20220924163228896](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220924163228896.png)

很明显这两个段， 一个是可执行的代码段， 一个是可写的数据段

以可执行文件(ls)为例， 映射到内存中，如下所示, 两个段被映射到了4个部分

![image-20220924213522116](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220924213522116.png)

其中 `acec9000-acf0b000` 是代码段， `acf0c000-acf13000` 整个部分是数据段

```bash
# 代码段
acec9000-acf0b000 r-xp 00000000 fe:20 14205      /data/user/ls	 大小: 4 20 00
# 数据段的只读部分，由programe[GUN read only After Relocation] 决定大小
acf0c000-acf0e000 r--p 00042000 fe:20 14205      /data/user/ls   大小： 20 00
# 数据段的可写部分
acf0e000-acf0f000 rw-p 00044000 fe:20 14205      /data/user/ls   大小： 10 00
# 可能会出现额外的填充， 并且没有指定文件名，这是因为数据段 内存大小>文件大小 决定的
acf0f000-acf13000 rw-p 00000000 00:00 0 						 大小： 40 00
```

> 如果想要详细了解映射， 参考：https://www.bilibili.com/video/BV1SM4y157WE/?spm_id_from=333.999.0.0&vd_source=ca069728de78926f6ac8fc77093830f8

关键代码解析如下：

```java
        // 分析内存映射情况
        System.out.println();
        System.out.println("+++++++++++++++++++ 内存映射分析 ++++++++++++++++++");
        for(ElfType32.elf32_phdr phdr : type_32.phdrList){
            if(VariableUtils.bytes2int(phdr.p_type) == ElfType32.PT_LOAD){
                int mem_vaddr_align = (VariableUtils.bytes2int(phdr.p_vaddr)/0x1000)*0x1000;
                int mem_end_vaddr = VariableUtils.bytes2int(phdr.p_vaddr) + VariableUtils.bytes2int(phdr.p_memsz);
                int mem_end_vaddr_align = mem_end_vaddr/0x1000*0x1000 + (mem_end_vaddr % 0x1000 != 0 ? 1 : 0)*0x1000;
                int file_offset_align = (VariableUtils.bytes2int(phdr.p_offset)/0x1000)*0x1000;
                int file_end_vaddr = VariableUtils.bytes2int(phdr.p_vaddr) + VariableUtils.bytes2int(phdr.p_filesz);
                int file_end_vaddr_align = file_end_vaddr/0x1000*0x1000 + (file_end_vaddr % 0x1000 != 0 ? 1 : 0)*0x1000;
                ElfType32.Elf32_MemMap memMap = new ElfType32.Elf32_MemMap();
                memMap.mem_vaddr_align = mem_vaddr_align;
                memMap.mem_end_vaddr_align = mem_end_vaddr_align;
                memMap.file_offset = file_offset_align;
                memMap.file_end_vaddr_align = file_end_vaddr_align;
                System.out.print(" 内存起始地址: "+ VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_vaddr_align)) + " - " +VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr_align))  );
                System.out.print(" 内存大小: "+ VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr_align - mem_vaddr_align )));
                System.out.print(" 文件偏移起始地址: "+ VariableUtils.bytes2HexString(VariableUtils.int2bytes((VariableUtils.bytes2int(phdr.p_offset)/0x1000)*0x1000)));
                System.out.println();
                if( mem_end_vaddr_align- file_end_vaddr_align > 0){
                    System.out.print(" * (未映射到文件部分)内存起始地址: "+ VariableUtils.bytes2HexString(VariableUtils.int2bytes(file_end_vaddr_align)) + " - " +VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr_align))  );
                    System.out.print(" 内存大小: "+ VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr_align - file_end_vaddr_align)));
                    System.out.println();
                }
            }
            if(VariableUtils.bytes2int(phdr.p_type) == ElfType32.PT_GNU_RELRO){
                int mem_vaddr = (VariableUtils.bytes2int(phdr.p_vaddr)/0x1000)*0x1000;
                int memsz_align = VariableUtils.bytes2int(phdr.p_memsz)/0x1000 + (VariableUtils.bytes2int(phdr.p_memsz) % 0x1000 != 0 ? 1 : 0);
                int mem_end_vaddr = mem_vaddr + memsz_align*0x1000;
                System.out.print(" * (只读的数据段部分)内存起始地址: "+ VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_vaddr)) + " - " +VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr))  );
                System.out.print("  内存起始地址: "+ VariableUtils.bytes2HexString(VariableUtils.int2bytes(memsz_align*0x1000)));
                System.out.println();
            }
        }
```



![image-20220926105402111](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926105402111.png)







### Dynamic Segment 动态段解析

解析 programe[Dynamic Segment] 动态段， 指定了动态链接过程中所需要的各种信息。 

![image-20220926102625271](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926102625271.png)

每8个字节为一项 { 类型， value}

```
typedef struct {
    Elf32_Sword d_tag;			// d_tag 控制着对 d_un 的解析
    union {
        Elf32_Word d_val;		// d_val 类型为 Elf32_Word 的目标项代表的是整型数。
        Elf32_Addr d_ptr;		// d_ptr 类型为 Elf32_Addr 的目标项代表的是进程空间里的地址。
    } d_un;
} Elf32_Dyn;
extern Elf32_Dyn _DYNAMIC[];
```



下表列出了可执行文件或共享目标文件中所要求的标记(d_tag)。如果一个标记 被置为“必需”，那么在 ELF 文件的动态连接数组中就必须包含一个此类型的 项；如果被置为“可选”，那就不是必需的，可以有也可以没有。

| 名称        | 值         | d_un   | 可执行文件 | 共享目标文件 | 意义                                                         |
| ----------- | ---------- | ------ | ---------- | ------------ | ------------------------------------------------------------ |
| DT_NULL     | 0          | 忽略   | 必需       | 必需         | 用于标记_DYNAMIC 数组的结束                                  |
| DT_NEEDED   | 1          | d_val  | 可选       | 可选         | 此元素指明了一个所需的库的名字。不过此元素本身并不是一个字符串，它 是一个指向由”DT_STRTAB”所标记的字符串表中的索引，在表中，此索引处是一 个以’null’结尾的字符串，这个字符串就是库的名字。在动态数组中可以包含若干 个此类型的项，这些项出现的相对顺序是不能随意调换的。 |
| DT_PLTRELSZ | 2          | d_val  | 可选       | 可选         | 此元素含有与函数连接表相关的所有重定位项的总大小，以字节为单位。如 果数组中有 DT_JMPREL 项的话，DT_PLTRELSZ 也必须要有。 |
| DT_PLTGOT   | 3          | d_ptr  | 可选       | 可选         | 此元素包含与函数连接表或全局偏移量表相应的地址。在 Intel 架构中，这一 项的 d_ptr 成员给出全局偏移量表中第一项的地址。如下文所述，全局偏移量表中 前三项都是保留的，其中两项用于持有函数连接表信息。 |
| DT_HASH     | 4          | d_ptr  | 必需       | 必需         | 此元素含有符号哈希表的地址。这里所指的哈希表与 DT_SYMTAB 所指的哈 希表是同一个。 |
| DT_STRTAB   | 5          | d_ptr  | 必需       | 必需         | 此元素包含字符串表的地址，此表中包含符号名、库名等等。       |
| DT_SYMTAB   | 6          | d_ptr  | 必需       | 必需         | 此元素包含符号表的地址                                       |
| DT_RELA     | 7          | d_ptr  | 必需       | 可选         | 此元素包含一个重定位表的地址，在重定位表中存储的是显式的“加数”， 比如对于 32 位文件来说，这种加数就是 Elf32_Rela。在一个目标文件中可以存在 多个重定位节，当为可执行文件或共享目标文件创建重定位表的时候，连接编辑器 会把这些重定位节连接在一起，最后形成一张大的重定位表。当连接编辑器为一个 可执行文件创建进程空间，或者把一个共享目标添加到进程空间中去的时候，它会 去读重定位表并执行相应的操作。如果在动态结构中包含有 DT_RELA 元素的话， 就必须同时还包含 DT_RELASZ 和 DT_RELEANT 元素。如果一个文件需要重定位 的话，DT_RELA 或 DT_REL 至少要出现一个。 |
| DT_RELASZ   | 8          | d_val  | 必需       | 可选         | 此元素持有 DT_RELA 相应的重定位表的大小，以字节为单位。      |
| DT_RELAENT  | 9          | d_val  | 必需       | 可选         | 此元素持有 DT_RELA 相应的重定位表项的大小，以字节为单位。    |
| DT_STRSZ    | 10         | d_val  | 必需       | 必需         | 此元素持有字符串表的大小，以字节为单位。                     |
| DT_SYMENT   | 11         | d_val  | 必需       | 必需         | 此元素持有符号表项的大小，以字节为单位。                     |
| DT_INIT     | 12         | d_ptr  | 可选       | 可选         | 此元素持有初始化函数的地址。参见下文”初始化和终止函数”内容。 |
| DT_FINI     | 13         | d_ptr  | 可选       | 可选         | 此元素持有终止函数的地址。参见下文”初始化和终止函数”内容。   |
| DT_SONAME   | 14         | d_val  | 忽略       | 可选         | 此元素持有一个字符串表中的偏移量，该位置存储了一个以’null’结尾的字符 串，是一个共享目标的名字。相应的字符串表由 DT_STRTAB 指定。 |
| DT_RPATH    | 15         | d_val  | 可选       | 忽略         | 此元素持有一个字符串表中的偏移量，该位置存储了一个以’null’结尾的字符 串，是一个用于搜索库文件的路径名。相应的字符串表由 DT_STRTAB 指定。 |
| DT_SYMBOLIC | 16         | 忽略   | 忽略       | 可选         | 在共享目标文件中，此元素的出现与否决定了动态连接器解析符号时所用的 算法。如果此元素不出现的话，动态连接器先搜索可执行文件再搜索库文件；如果此元素出现的话，顺序刚好相反，动态连接器会先从本共享目标文件开始，后搜索 可执行文件。 |
| DT_REL      | 17         | d_ptr  | 必需       | 可选         | 此元素与 DT_RELA 相似，只是它所指向的重定位表中，“加数”是隐含的 而不是显式的。 |
| DT_RELSZ    | 18         | d_val  | 必需       | 可选         | 此元素持有 DT_REL 相应的重定位表的大小，以字节为单位。       |
| DT_RELENT   | 19         | d_val  | 必需       | 可选         | 此元素持有 DT_REL 相应的重定位表项的大小，以字节为单位。     |
| DT_PLTREL   | 20         | d_val  | 可选       | 可选         | 本成员指明了函数连接表所引用的重定位项的类型。d_val 成员含有 DT_REL 或 DT_RELA。函数连接表中的所有重定位类型都是相同的。 |
| DT_DEBUG    | 21         | d_ptr  | 可选       | 忽略         | 本成员用于调试，格式未明确定义。                             |
| DT_TEXTREL  | 22         | 忽略   | 可选       | 可选         | 如果此元素出现的话，在重定位过程中如果需要修改的是只读段的话，连接 编辑器可以做相应的修改；而如果此元素不出现的话，在重定位过程中，即使需 要，也不能修改只读段。 |
| DT_JMPREL   | 23         | d_ptr  | 可选       | 可选         | 此类型元素如果存在的话，其 d_ptr 成员含有与函数连接表单独关联的重定位 项地址。把多个重定位项分开可以让动态连接器在初始化的时候忽略它们，当然前 提条件是“后期绑定”是激活的。如果此元素存在的话，DT_PLTRELSZ 和 DT_PLTREL 也应该出现。 |
| DT_BIND_NOW | 24         | 忽略   | 可选       | 可选         | 如果此元素存在的话，动态连接器必须在程序开始执行以前，完成所有包含 此项的目标的重定位工作。如果此元素存在，即使程序应用了“后期绑定”，它对 于此项所指定的目标也不适用，动态连接器仍需事先做好重定位。 |
| DT_LOPROC   | 0x70000000 | 未定义 | 未定义     | 未定义       | 这一区间的值是为处理器保留的。                               |
| DT_HIPROC   | 0x7fffffff | 未定义 | 未定义     | 未定义       | 这一区间的值是为处理器保留的。                               |

代码解析如下： 

![image-20220926102933408](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926102933408.png)



#### DT_STRTAB 字符串表

解析 字符串表(d_tag=5 DT_STRTAB, d_value 为 内存偏移 )

![image-20220926104900747](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926104900747.png)

内存偏移1A20, 在第一个段内，所以文件偏移也是1A20

![image-20220926105402111](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926105402111.png)

![image-20220926110113858](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926110113858.png)

关键代码解析如下：

```java
// 读取字符串表的内容
System.out.println();
System.out.println("+++++++++++++++++++DT_STRTAB of dynamic segment++++++++++++++++++");
int offset = 1;
while (true){
    String str = DT_STRTAB_getString(fileByteArys, offset);
    System.out.println("偏移： 0x" + Integer.toHexString(offset) + " 字符串: " + str);
    if(str.equals("")){
        break;
    }
    offset += str.length()+1;
}


    /**  解析字符串表 DT_STRTAB
     *
     * @param fileByteArys 文件首地址
     * @param offset_strtab 字符串表偏移
     * @return
     */
    public static String DT_STRTAB_getString(byte[] fileByteArys, int offset_strtab){
        // 遍历字符串表
        int offset = 0;
        for (int i = 0; i < type_32.dynamicList.size(); ++i) {
            if( VariableUtils.bytes2int(type_32.dynamicList.get(i).d_tag) == ElfType32.DT_STRTAB){
                offset = VariableUtils.bytes2int(type_32.dynamicList.get(i).d_val_ptr );
            }
        }
        // 内存偏移转文件偏移
        offset = vaddr2offset(offset);
        return VariableUtils.bytes2String(fileByteArys, offset + offset_strtab);
    }
    
	// 内存偏移转文件偏移
    public static int vaddr2offset(int vaddr){
        for (int i = 0; i < type_32.memList.size(); ++i) {
            if( type_32.memList.get(i).isInSegment(vaddr)){
                return type_32.memList.get(i).vaddr2offset(vaddr);
            }
        }
        return 0;
    }
```

![image-20220926150103786](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926150103786.png)







#### DT_NEEDED 导入库表

解析 导入库表(d_tag=1 DT_NEEDED,  d_value 为相对字符串表DT_STRAB的offset) 

![image-20220926150542559](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926150542559.png)

d_value是相对字符串表DT_STRAB的内存偏移， 也都在第一个段内，所以文件偏移与内存偏移一致

关键代码解析如下：

```java
        // 读取导入库的内容
        System.out.println();
        System.out.println("+++++++++++++++++++导入库 of dynamic segment++++++++++++++++++");
        // 遍历字符串表
        for (int i = 0; i < type_32.dynamicList.size(); ++i) {
            if( VariableUtils.bytes2int(type_32.dynamicList.get(i).d_tag) == ElfType32.DT_NEEDED){
                vaddr = VariableUtils.bytes2int(type_32.dynamicList.get(i).d_val_ptr);
                String str = DT_STRTAB_getString(fileByteArys, vaddr);
                System.out.println("导入库： "+ str);
            }
        }
```

![image-20220926160655084](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926160655084.png)



#### DT_SYMTAB 符号表

解析符号表(类型=6 DT_SYMTAB, d_value 为符号表的offset)，符号表的每一项有16 字节{名字偏移，地址，大小,属性}, 尺寸没有明确指定（在节表中有指明符号表的大小，但不应该依赖于节表）

>  可以通过hash表来分析符号表的大小
>
>  https://stackoverflow.com/questions/59411449/what-is-the-entry-count-of-elf-dynamic-symbol-table



符号表表项的意义具体参考： [.dynsym & .symtab 符号表](#.dynsym & .symtab 符号表)

![image-20220926205322174](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926205322174.png)

d_value是内存偏移， 也都在第一个段内，所以文件偏移与内存偏移一致

关键解析代码如下：

```java
//读取符号表信息(Symbol Table)
System.out.println();
System.out.println("+++++++++++++++++++符号表 of dynamic segment++++++++++++++++++");
// 通过表的类型 DT_SYMTAB 定位符号表
for (ElfType32.Elf32_Dynamic phdr : type_32.dynamicList) {
if( VariableUtils.bytes2int(phdr.d_tag) == ElfType32.DT_SYMTAB){
offset_sym = VariableUtils.bytes2int(phdr.d_val_ptr);
}
}
// 内存偏移转换为文件偏移
type_32.symList_d = vaddr2offset(offset_sym);
System.out.println("DT_SYMTAB符号表 基址： 0x"+ Integer.toHexString(offset_sym) );
// 确定符号表的总项数, 通过最后一项为0来确定
parseSymbolTableListD(fileByteArys, type_32.symList_d, 1);


    /**
     * 解析Symbol Table内容
     * @param header 文件字节
     * @param offset_symtab 符号表偏移
     * @param offset 要查询的符号的索引
     */
    public static ElfType32.Elf32_Sym parseSymbolTableListD(byte[] header, int offset_symtab, int offset){
        int header_size = 16;//16个字节
        byte[] des = new byte[header_size];
        des = VariableUtils.bytesSplite(header, offset*header_size + offset_symtab, header_size);
        ElfType32.Elf32_Sym obj =parseSymbolTable(des);
        System.out.println("正在解析的符号： "+ obj.st_name_toString(header));
        System.out.println(obj.toString());
        return obj;
    }
```





#### DT_JMPREL 导入表

解析 导入表(类型=23 DT_JMPREL), 导入表的每一项有8 字节{重定位地址， 属性(1)，符号表索引(3)}, 尺寸为 (类型=2 DT_PLTRELSZ ) 字节决定

```
typedef struct elf32_rel {
  Elf32_Addr	r_offset;			// 要重定位地址的地址
  Elf32_Word	r_info;				// 属性(1)，符号表索引(3)
} Elf32_Rel;
```

* r_info[0] 三种类型

  * R_ARM_JUMP_SLOT  22 (**导入表中我只看到这种类型**)

    直接替换地址

  * R_ARM_GLOB_DAT  21        

    直接替换地址

  * R_ARM_ABS32 2

    不是替换地址， 而是在找到符号地址的基础上， 加上原本地址存有的数值


> 实际上导入表DT_JMPREL 导入的是函数地址， 对于其他模块变量的导入在后面的重定位表中实现

首先在动态段中定位导入表的内存偏移

![image-20220927154151417](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220927154151417.png)

在之前的内存映射关系中， 0x51d4 < 0x 00 04 20 00, 所以在第一个段中， 内存偏移等价于文件偏移

![image-20220926105402111](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220926105402111.png)

可以发现ida的静态分析可以解析出这些内容

![image-20220927154925277](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220927154925277.png)

以`__libc_int`符号为例， linker首先遍历之前解析的所有导入库的导出符号，获取同名的导出符号的地址，然后替换到模块中的内存偏移地址0x44B78 的位置

> 这种寻找符号的方法导致了一种与windows 的PE文件格式不同的地方， ELF 文件的导入库与导入函数之间并不是强相关。
>
> 比如在linux的hook中，通过LD_PRELOAD环境变量的hook函数，并不需要指定模块名， 只需要函数原型相同即可完成hook。参考 https://www.anquanke.com/post/id/254388

* 未导入符号地址， 该处的 0x9cF0并不能指向函数

  ![image-20220927220935094](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220927220935094.png)

  该地址被如下代码引用， 代码结合起来类似于 jmp [0x44B78]  , **这就是R_ARM_JUMP_SLOT  22类型的导入地址使用方式**

  ![image-20220927221104701](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220927221104701.png)

* 导入符号地址

  ![image-20220927225447360](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220927225447360.png)



关键代码解析：

```java
    public static class Elf32_Jmp {
        public byte[] r_offset = new byte[4];
        public byte[] r_info = new byte[4];

        public Elf32_Jmp() {
        }

        public String toString() {
            byte[] info = {r_info[1], r_info[2],r_info[3],0};
            return "r_offset(存放导入符号地址的地址(类似与windows的IAT（导入地址表）)， 相对于 .dynstr 的 sh_offset):" + VariableUtils.bytes2HexString(this.r_offset) +
                    "\n属性(虚拟地址，指向符号所在的内存地址):" + Byte.toString(r_info[0]) + e_type_toString()+
                    "\n符号(虚拟地址，指向符号所在的内存地址):" +  VariableUtils.bytes2HexStringReverse(info);
        }
        public String r_sym_toString(byte[] fileByteArys){
            byte[] info = {r_info[1], r_info[2],r_info[3],0};
            ElfType32.Elf32_Sym sym = AndroidElfParser.parseSymbolTableListD(fileByteArys, AndroidElfParser.type_32.symList_d,  VariableUtils.bytes2int(info));
            return sym.st_name_toString(fileByteArys);
        }
        public String e_type_toString(){
            final int R_ARM_JUMP_SLOT = 22;
            final int R_ARM_GLOB_DAT = 21;
            final int R_ARM_ABS32 = 2;

            int value = Byte.toUnsignedInt(r_info[0]);
            switch (value){
                case R_ARM_JUMP_SLOT:
                    return "R_ARM_JUMP_SLOT 直接替换地址";
                case R_ARM_GLOB_DAT:
                    return "R_ARM_GLOB_DAT 直接替换地址";
                case R_ARM_ABS32:
                    return "R_ARM_ABS32 不是替换地址， 而是在找到符号地址的基础上， 加上原本地址存有的数值";
                default:
                    return "其他类型";
            }
        }
    }
```

 ![image-20220928003449811](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220928003449811.png)



#### DT_REL 重定位表

解析 重定位表(类型=17 DT_REL), 尺寸为 (类型=18 DT_RELSZ ) 字节决定, 重定位表的每一项有16 字节{重定位地址， 属性(1)，符号表索引(3)}

> 重定位表的解析与导入表完全相同，导入符号的处理时，会将这两个表一起用于搜索符号,  重定位表有一部分是其他模块变量的导入表

表项结构如下

```
typedef struct elf32_rel {
  Elf32_Addr	r_offset;			// 要重定位地址的地址
  Elf32_Word	r_info;				// 属性(1)，符号表索引(3)
} Elf32_Rel;
```

* r_info[0] 三种类型

  * R_ARM_RELATIVE  23 **只有该项与R_ARM_JUMP_SLOT  22 数值上的不同**

    不是替换地址， 而是在找到符号地址的基础上， 加上原本地址存有的数值

  * R_ARM_GLOB_DAT  21 

    直接替换地址

  * R_ARM_ABS32 2

    不是替换地址， 而是在找到符号地址的基础上， 加上原本地址存有的数值

先使用代码解析重定位表

![image-20220928013818474](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220928013818474.png)

首先定位第一个重定位的符号， 没有符号名， 重定位类型为 **R_ARM_RELATIVE  23**

![image-20220928014639644](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220928014639644.png)

这要重定位的是全局字符串， 大多数也都是字符串需要重定位

![image-20220928014809051](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220928014809051.png)

而重定位类型 **R_ARM_GLOB_DAT  21** 的使用通常作为函数指针直接使用

![image-20220928015816767](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220928015816767.png)





#### DT_GUN_HASH 导出/hash表

解析 导出/hash表表(类型=0x6ffffef5 DT_GUN_HASH)

```c
struct{
    uint32_t nbucket;		// bucket数组的个数, 每个bucket 4个字节
    uint32_t symoffset;		// chain 表实际开始的符号表索引位置， 所以理论位置为chain-4*symoffset
    /* Bloom Filter用于提前停止查找丢失的符号, 本节不研究*/
    uint32_t bloom_size; 	// 指定bloom数组的个数, 每个bloom 4个字节
    uint32_t bloom_shift;
    uint32_t bloom[bloom_size]; /* uint64_t for 64-bit binaries */
    /*bucket 
    每个bucket 4个字节,是符号表中的索引, 索引*0x10 + 符号表偏移 = 函数地址
    */
    uint32_t bucket[nbucket]; 	
    uint32_t chain[];			// chain 是符号名的hash
};
```

关于这个表的具体解析，参考： https://flapenguin.me/elf-dt-gnu-hash 

下面这一套系统是用于快速索引导出表的一套结构， 理论上chain[]存储的是符号表的hash中的前31位， 所以项数nchain必须等于符号表的个数，不过由于符号表开始的一部分是导入表和重定位表的内容，为了减少空间的浪费所以chain表是从sym_tab的symoffset项开始的。

![image-20220923154015011](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220923154015011.png)

如下所示的一个哈希函数输入一个符号名，输出一个值用于计算 bucket 索引。如果给出一个符号名，经哈希函数计算得到值 x，那么 x%nbucket 是 bucket 表内的索引，bucket[x%nbucket] 给出一个符号表的索引值 y，y 同时也是 chain 表内的索引值。如果chain[y]前31位与hash值不匹配，chain[y]第32位是0则继续向下匹配，否则结束匹配， 说明这个导出表中并不含有此符号。继续遍历下一个模块的导出表. 

哈希函数(hashing function)

```
unsigned long elf_hash(const unsigned char *name)
{
    unsigned long h = 0, g;
    while (*name)
    {
        h = (h << 4) + *name++;
        if (g = h & 0xf0000000)
        h ^= g >> 24;
        h &= ~g;
    }
    return h;
}
```

关键解析代码: 

![image-20220928155758216](https://raw.githubusercontent.com/overturncat/picbook/master/)

```java
    public static class Elf32_hash {
        // bucket数组的个数, 每个bucket 4个字节
        public byte[] nbucket = new byte[4];
        // chain 表实际开始的符号表索引位置， 所以理论位置为chain-4*symoffset
        public byte[] symoffset = new byte[4];
        /* Bloom Filter用于提前停止查找丢失的符号, 本节不研究*/
        public byte[] bloom_size = new byte[4]; // 指定bloom数组的个数, 每个bloom 4个字节
        public byte[] bloom_shift = new byte[4]; /* uint64_t for 64-bit binaries */
        public byte[] bloom;
        /*bucket
        共有 nbucket 个bucket
        每个bucket 4个字节,是符号表中的索引, 索引*0x10 + 符号表偏移 = 函数地址
        */
        public int[] bucket;
        // chain 是符号名的hash
        public int[] chain;


        public void getBucketArray(byte[] fileByteArys, int offset){
            int num = VariableUtils.bytes2int(nbucket);
            bucket = new int[num];
            for( int i =0; i<num;i++){
                bucket[i] =  VariableUtils.bytes2int(VariableUtils.bytesSplite(fileByteArys, offset+VariableUtils.bytes2int(bloom_size)*4 + 0x10 + i*4, 4));
            }
        }
        public byte[] getChainArray(byte[] fileByteArys, int offset, int index){
            int num = VariableUtils.bytes2int(nbucket);
            int chain_offset = offset+VariableUtils.bytes2int(bloom_size)*4 + 0x10 + num*4;
            return VariableUtils.bytesSplite(fileByteArys,index*4 + chain_offset, 4);
        }
        public String toString() {
            return "nbucket(bucket数组的个数, 每个bucket 4个字节):" + VariableUtils.bytes2HexString(this.nbucket) +
                    "\nsymoffset(chain 表实际开始的符号表索引位置， 所以理论位置为chain-4*symoffset):" + VariableUtils.bytes2HexString(this.symoffset) +
                    "\nbloom_size(指定bloom数组的个数, 每个bloom 4个字节):" + VariableUtils.bytes2HexString(this.bloom_size) +
                    "\nbloom_shift:" + VariableUtils.bytes2HexString(this.bloom_shift);
        }
    }
```

![image-20220928170431316](https://raw.githubusercontent.com/overturncat/picbook/master/image-20220928170431316.png)
