package com.company.Utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class ElfType64 {
    public elf64_rel rel = new elf64_rel();
    public elf64_rela rela = new elf64_rela();
    public elf64_sym sym = new elf64_sym();
    public elf64_hdr hdr = new elf64_hdr();
    public elf64_phdr phdr = new elf64_phdr();
    public elf64_shdr shdr = new elf64_shdr();

    public ElfType64() {
    }

    public class elf64_hdr {
        public byte[] e_ident = new byte[16];
        public short e_type;
        public short e_machine;
        public int e_version;
        public long e_entry;
        public long e_phoff;
        public long e_shoff;
        public int e_flags;
        public short e_ehsize;
        public short e_phentsize;
        public short e_phnum;
        public short e_shentsize;
        public short e_shnum;
        public short e_shstrndx;

        public elf64_hdr() {
        }
    }

    public class elf64_phdr {
        public int p_type;
        public int p_flags;
        public long p_offset;
        public long p_vaddr;
        public long p_paddr;
        public long p_filesz;
        public long p_memsz;
        public long p_align;

        public elf64_phdr() {
        }
    }

    public class elf64_rel {
        public long r_offset;
        public long r_info;

        public elf64_rel() {
        }
    }

    public class elf64_rela {
        public long r_offset;
        public long r_info;
        public long r_addend;

        public elf64_rela() {
        }
    }

    public class elf64_shdr {
        public int sh_name;
        public int sh_type;
        public long sh_flags;
        public int sh_addr;
        public int sh_offset;
        public long sh_size;
        public int sh_link;
        public int sh_info;
        public long sh_addralign;
        public long sh_entsize;

        public elf64_shdr() {
        }
    }

    public class elf64_sym {
        public int st_name;
        public char st_info;
        public char st_other;
        public short st_shndx;
        public long st_value;
        public long st_st_size;

        public elf64_sym() {
        }
    }
}
class ElfType32 {
    public elf32_rel rel = new elf32_rel();
    public elf32_rela rela = new elf32_rela();
    public elf32_hdr hdr = new elf32_hdr();
    // 段表 数组
    public ArrayList<elf32_phdr> phdrList = new ArrayList();
    // 节表 数组
    public ArrayList<elf32_shdr> shdrList = new ArrayList();
    // 符号表 数组
    public ArrayList<Elf32_Sym> symList = new ArrayList();
    // 内存分配映射
    public ArrayList<Elf32_MemMap> memList = new ArrayList();
    // 动态段 数组
    public ArrayList<Elf32_Dynamic> dynamicList = new ArrayList();
    // 符号表(*动态*) 基址
    public int symList_d = 0;
    // 导入表
    public ArrayList<Elf32_Jmp> jmpList = new ArrayList();
    // 重定位表
    public ArrayList<Elf32_Rel> relList = new ArrayList();
    // hash 表
    public Elf32_hash hash = new Elf32_hash();

    // 符号表的一些属性
    public static final int STB_LOCAL = 0;
    public static final int STB_GLOBAL = 1;
    public static final int STB_WEAK = 2;
    public static final int STB_LOPROC = 13;
    public static final int STB_HIPROC = 15;
    public static final int STT_NOTYPE = 0;
    public static final int STT_OBJECT = 1;
    public static final int STT_FUNC = 2;
    public static final int STT_SECTION = 3;
    public static final int STT_FILE = 4;
    public static final int STT_LOPROC = 13;
    public static final int STT_HIPROC = 15;
    public static final int SHN_UNDEF = 0;
    public static final int SHN_LORESERVE = 0xff00;
    public static final int SHN_LOPROC = 0xff00;
    public static final int SHN_HIPROC = 0xff1f;
    public static final int SHN_ABS = 0xfff1;
    public static final int SHN_COMMON = 0xfff2;
    public static final int SHN_HIRESERVE = 0xffff;
    public static final int SHN_MIPS_ACCOMON = 0xff00;
    public static final int SHT_NULL = 0;
    public static final int SHT_PROGBITS = 1;
    public static final int SHT_SYMTAB = 2;
    public static final int SHT_STRTAB = 3;
    public static final int SHT_RELA = 4;
    public static final int SHT_HASH = 5;
    public static final int SHT_DYNAMIC = 6;
    public static final int SHT_NOTE = 7;
    public static final int SHT_NOBITS = 8;
    public static final int SHT_REL = 9;
    public static final int SHT_SHLIB = 10;
    public static final int SHT_DYNSYM = 11;
    public static final int SHT_NUM = 12;
    public static final int SHT_LOPROC = 1879048192;
    public static final int SHT_HIPROC = 2147483647;
    public static final int SHT_LOUSER = -2147483648;
    public static final int SHT_HIUSER = -1;
    public static final int SHT_MIPS_LIST = 1879048192;
    public static final int SHT_MIPS_CONFLICT = 1879048194;
    public static final int SHT_MIPS_GPTAB = 1879048195;
    public static final int SHT_MIPS_UCODE = 1879048196;
    public static final int SHF_WRITE = 1;
    public static final int SHF_ALLOC = 2;
    public static final int SHF_EXECINSTR = 4;
    public static final int SHF_MASKPROC = -268435456;
    public static final int SHF_MIPS_GPREL = 268435456;
    public static final int PT_NULL = 0;
    public static final int PT_LOAD = 1;
    public static final int PT_DYNAMIC = 2;
    public static final int PT_INTERP = 3;
    public static final int PT_NOTE = 4;
    public static final int PT_SHLIB = 5;
    public static final int PT_PHDR = 6;
    public static final int PT_GNU_RELRO = 0x6474E552;
    public static final int PT_LOPROC = 0x70000000;
    public static final int PT_HIPROC = 0x7fffffff;

    // 动态段表 d_tag的属性
    public static final int DT_NULL = 0;
    public static final int DT_NEEDED = 1;
    public static final int DT_PLTRELSZ = 2;
    public static final int DT_PLTGOT = 3;
    public static final int DT_HASH = 4;
    public static final int DT_STRTAB = 5;
    public static final int DT_SYMTAB = 6;
    public static final int DT_RELA = 7;
    public static final int DT_RELASZ = 8;
    public static final int DT_RELAENT = 9;
    public static final int DT_STRSZ = 10;
    public static final int DT_SYMENT = 11;
    public static final int DT_INIT = 12;
    public static final int DT_FINI = 13;
    public static final int DT_SONAME = 14;
    public static final int DT_RPATH = 15;
    public static final int DT_SYMBOLIC = 16;
    public static final int DT_REL = 17;
    public static final int DT_RELSZ = 18;
    public static final int DT_RELENT = 19;
    public static final int DT_PLTREL = 20;
    public static final int DT_DEBUG = 21;
    public static final int DT_TEXTREL = 22;
    public static final int DT_JMPREL = 23;
    public static final int DT_BIND_NOW = 24;

    public static final int DT_INIT_ARRAY = 25;
    public static final int DT_FINI_ARRAY = 26;
    public static final int DT_INIT_ARRAYSZ = 27;
    public static final int DT_FINI_ARRAYSZ = 28;
    public static final int DT_PREINIT_ARRAY = 32;
    public static final int DT_PREINIT_ARRAYSZ = 33;

    public static final int DT_GUN_HASH = 0x6ffffef5;
    public static final int ET_LOPROC = 0x70000000;
    public static final int ET_HIPROC = 0x7fffffff;

    public ElfType32() {
    }

    public void printDynamicList(byte[] fileByteArys) {
        for (int i = 0; i < this.dynamicList.size(); ++i) {
            System.out.println();
            System.out.println("The " + (i + 1) + " Dynamic Segment:");
            System.out.println(( this.dynamicList.get(i)).toString());
        }

    }


    public void printJmpList(byte[] fileByteArys) {
        for (int i = 0; i < this.jmpList.size(); ++i) {
            System.out.println();
            System.out.println("The " + (i + 1) + " Jmp Segment:" + this.jmpList.get(i).r_sym_toString(fileByteArys));
            System.out.println(( this.jmpList.get(i)).toString());
        }
    }

    public void printRelList(byte[] fileByteArys) {
        for (int i = 0; i < this.relList.size(); ++i) {
            System.out.println();
            System.out.println("The " + (i + 1) + " Rel Segment:" + this.relList.get(i).r_sym_toString(fileByteArys));
            System.out.println(( this.relList.get(i)).toString());
        }
    }

    public void printSymList(byte[] fileByteArys) {
        for (int i = 0; i < this.symList.size(); ++i) {
            System.out.println();
            System.out.println("The " + (i + 1) + " Symbol Table:" + this.symList.get(i).st_name_toString(fileByteArys));
            System.out.println(((Elf32_Sym) this.symList.get(i)).toString());
        }
    }


    public void printPhdrList() {
        for (int i = 0; i < this.phdrList.size(); ++i) {
            System.out.println();
            System.out.println("The " + (i + 1) + " Program Header:");
            System.out.println(((elf32_phdr) this.phdrList.get(i)).toString());
        }

    }

    public void printShdrList(byte[] fileByteArys) {
        for (int i = 0; i < this.shdrList.size(); ++i) {
            System.out.println();
            System.out.println("The " + (i + 1) + " Section Header:"+ this.shdrList.get(i).sh_name_toString(fileByteArys));
            System.out.println(this.shdrList.get(i));
        }

    }

    public static class Elf32_MemMap{
        public int mem_vaddr_align = 0;
        public int mem_end_vaddr_align = 0;
        public int file_offset = 0;
        public int file_end_vaddr_align = 0;

        boolean isInSegment(int vaddr){
            if(vaddr > mem_vaddr_align && vaddr < mem_end_vaddr_align)
                return true;
            return false;
        }

        int vaddr2offset(int vaddr){
            return vaddr - (mem_vaddr_align - file_offset);
        }
    }

    public static class Elf32_Sym {
        public byte[] st_name = new byte[4];
        public byte[] st_value = new byte[4];
        public byte[] st_size = new byte[4];
        public byte st_info;
        public byte st_other;
        public byte[] st_shndx = new byte[2];

        public Elf32_Sym() {
        }

        public String toString() {
            return "st_name(符号名偏移， 相对于 .dynstr 的 sh_offset):" + VariableUtils.bytes2HexString(this.st_name) +
                    "\nst_value(虚拟地址，指向符号所在的内存地址):" + VariableUtils.bytes2HexString(this.st_value) +
                    "\nst_size(符号的大小，如一个对象的大小就是它实际占用的字节数):" + VariableUtils.bytes2HexString(this.st_size) +
                    "\nst_info(符号的类型和属性):" + this.st_info  + st_info_toString() +
                    "\nst_other(本数据成员目前暂未使用):" + ((short) this.st_other & 15) +
                    "\nst_shndx(本数据成员指明了相关联的节头表的索引。重定位时节的位置会改变，本数据成员的值也随之改变):" + VariableUtils.bytes2HexString(this.st_shndx) + st_shndx_toString();
        }
        public String st_name_toString(byte[] fileByteArys){
            int offset_sym = 0;
            for(ElfType32.elf32_shdr shdr : AndroidElfParser.type_32.shdrList){
                if(RegexUtils.isSearch(".dynstr",shdr.sh_name_toString(fileByteArys) )){
                    offset_sym = VariableUtils.bytes2int(shdr.sh_offset);
                    break;
                }
            }
            if(offset_sym == 0)
                return "";
            // 首先定位section .dynstr
            int symNameOffset = offset_sym+VariableUtils.bytes2int(this.st_name);
            String name = VariableUtils.bytes2String(fileByteArys, symNameOffset);
            return name;
        }
        public String st_info_toString(){
            int ELF32_ST_BIND = Byte.toUnsignedInt(this.st_info)>>>4;
            int ELF32_ST_TYPE = Byte.toUnsignedInt(this.st_info) & 0xf;
            String info_string = "";
            if (ELF32_ST_BIND == STB_LOCAL){
                info_string += " | STB_LOCAL: 本地符号,只出现在本文件中";
            }else if(ELF32_ST_BIND == STB_GLOBAL){
                info_string += " | STB_GLOBAL: 全局符号,可以被所有文件引用";
            }else if(ELF32_ST_BIND == STB_WEAK){
                info_string += " | STB_WEAK: 全局符号,优先级更低";
            }else if(ELF32_ST_BIND > STB_LOPROC && ELF32_ST_BIND < STB_HIPROC){
                info_string += " | STB_LOPROC ~ STB_HIPROC: 为特殊处理器保留的属性区间";
            }

            if (ELF32_ST_TYPE == STT_NOTYPE){
                info_string += " | STT_NOTYPE: 本符号类型未指定";
            }else if(ELF32_ST_TYPE == STT_OBJECT){
                info_string += " | STT_OBJECT: 全局符号,本符号是一个数据对象，比如变量、数组等";
            }else if(ELF32_ST_TYPE == STT_FUNC){
                info_string += " | STT_FUNC: 本符号是一个函数，当函数符号被其他模块引用时会自动创建连接表项";
            }else if(ELF32_ST_TYPE == STT_SECTION){
                info_string += " | STT_SECTION: 本符号与一个节相关联，用于重定位，通常具有 STB_LOCAL属性";
            }else if(ELF32_ST_TYPE == STT_FILE){
                info_string += " | STT_FILE: 文件符号，它具有 STB_LOCAL属性，它的节索引值是SHN_ABS。";
            }else if(ELF32_ST_TYPE > STT_LOPROC && ELF32_ST_TYPE < STT_HIPROC){
                info_string += " | STT_LOPROC ~ STT_HIPROC: 为特殊处理器保留的属性区间";
            }

            return info_string;
        }

        public String st_shndx_toString(){
            int shndx = Short.toUnsignedInt(VariableUtils.bytes2short(this.st_shndx));
            String info_string = "";
            if (shndx == SHN_ABS){
                info_string += "SHN_ABS: 符号的值是绝对的，具有常量性，在重定位过程中，此值不需要改变";
            }else if(shndx == SHN_COMMON){
                info_string += "SHN_COMMON: 本符号所关联的是一个还没有分配的公共节，连接器会为本符号分配存储空间，而且其起始地址是向 st_value对齐";
            }else if(shndx == SHN_UNDEF){
                info_string += "SHN_UNDEF: 本符号在当前目标文件中未定义，在连接过程中，连接器会找到此符号被定义的文件";
            }else if(shndx > 0 && shndx < AndroidElfParser.type_32.shdrList.size() ){
                info_string += AndroidElfParser.type_32.shdrList.get(shndx).sh_name_string;
            }
            return info_string;
        }
    }
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
            ElfType32.Elf32_Sym sym = AndroidElfParser.parseSymbolTableListD(fileByteArys,  VariableUtils.bytes2int(info));
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

    public static class Elf32_Rel {
        public byte[] r_offset = new byte[4];
        public byte[] r_info = new byte[4];

        public Elf32_Rel() {
        }

        public String toString() {
            byte[] info = {r_info[1], r_info[2],r_info[3],0};
            return "r_offset(存放导入符号地址的地址(类似与windows的IAT（导入地址表）)， 相对于 .dynstr 的 sh_offset):" + VariableUtils.bytes2HexString(this.r_offset) +
                    "\n属性(虚拟地址，指向符号所在的内存地址):" + Byte.toString(r_info[0]) + e_type_toString()+
                    "\n符号(虚拟地址，指向符号所在的内存地址):" +  VariableUtils.bytes2HexStringReverse(info);
        }
        public String r_sym_toString(byte[] fileByteArys){
            byte[] info = {r_info[1], r_info[2],r_info[3],0};
            ElfType32.Elf32_Sym sym = AndroidElfParser.parseSymbolTableListD(fileByteArys,  VariableUtils.bytes2int(info));
            return sym.st_name_toString(fileByteArys);
        }
        public String e_type_toString(){
            final int R_ARM_RELATIVE = 23;
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
                case R_ARM_RELATIVE:
                    return "R_ARM_RELATIVE 不是替换地址， 而是在找到符号地址的基础上， 加上原本地址存有的数值";
                default:
                    return "其他类型";
            }
        }
    }

    public static class Elf32_Dynamic {
        public byte[] d_tag = new byte[4];
        public byte[] d_val_ptr = new byte[4];

        public Elf32_Dynamic() {
        }

        public String toString() {
            return "d_tag(符号名偏移， 相对于 .dynstr 的 sh_offset):" + VariableUtils.bytes2HexString(this.d_tag) + d_tag_toString()+
                    "\nd_val/d_ptr(虚拟地址，指向符号所在的内存地址):" + VariableUtils.bytes2HexString(this.d_val_ptr);
        }

        public String d_tag_toString(){
            int value = VariableUtils.bytes2int(this.d_tag);
            switch (value){
                case DT_NULL:
                    return "DT_NULL: 用于标记_DYNAMIC数组的结束";
                case DT_NEEDED:
                    return "DT_NEEDED: 此元素指明了一个所需的库的名字的索引。指向由”DT_STRTAB”所标记的字符串表中的偏移: DT_STRTAB+OFFSET";
                case DT_PLTRELSZ:
                    return "DT_PLTRELSZ: 此元素含有DT_JMPREL项目的总大小，单位为字节";
                case DT_PLTGOT:
                    return "DT_PLTGOT: 此元素包含与函数连接表或全局偏移量表相应的地址。";
                case DT_HASH:
                    return "DT_HASH: 此元素含有符号哈希表的地址。这里所指的哈希表与 DT_SYMTAB所指的哈希表是同一个";
                case DT_STRTAB:
                    return "DT_STRTAB: 此元素包含字符串表的地址，此表中包含符号名、库名等等。";
                case DT_SYMTAB:
                    return "DT_SYMTAB: 此元素包含符号表的地址。";
                case DT_RELA:
                    return "DT_RELA: 此元素包含一个重定位表的地址，在重定位表中存储的是显式的“加数”";
                case DT_RELASZ:
                    return "DT_RELASZ: 此元素持有 DT_RELA相应的重定位表的大小";
                case DT_RELAENT:
                    return "DT_RELAENT: 此元素持有 DT_RELA相应的重定位表项的大小";
                case DT_STRSZ:
                    return "DT_STRSZ: 此元素持有字符串表的大小";
                case DT_SYMENT:
                    return "DT_SYMENT: 此元素持有符号表项的大小";
                case DT_INIT:
                    return "DT_INIT: 此元素持有初始化函数的地址";
                case DT_FINI:
                    return "DT_FINI: 此元素持有终止函数的地址";
                case DT_SONAME:
                    return "DT_SONAME: 此元素持有一个字符串表中的偏移量，该位置存储了一个以’null’结尾的字符串，是一个共享目标的名字。";
                case DT_RPATH:
                    return "DT_RPATH: 此元素持有一个字符串表中的偏移量，该位置存储了一个以’null’结尾的字符串，是一个用于搜索库文件的路径名。";
                case DT_SYMBOLIC:
                    return "DT_SYMBOLIC: 在共享目标文件中，此元素的出现与否决定了动态连接器解析符号时所用的算法。";
                case DT_REL:
                    return "DT_REL: 此元素包含一个重定位表的地址";
                case DT_RELSZ:
                    return "DT_RELSZ: ";
                case DT_RELENT:
                    return "DT_RELENT: 此元素持有 DT_REL相应的重定位表项的大小，";
                case DT_PLTREL:
                    return "DT_PLTREL: 本成员指明了函数连接表所引用的重定位项的类型。d_val成员含有 DT_REL或 DT_RELA。";
                case DT_DEBUG:
                    return "DT_DEBUG: 本成员用于调试";
                case DT_TEXTREL:
                    return "DT_TEXTREL: 如果此元素出现的话，在重定位过程中如果需要修改的是只读段的话，连接编辑器可以做相应的修改";
                case DT_JMPREL:
                    return "DT_JMPREL: 导入表，其 d_ptr成员含有与函数连接表单独关联的重定位项地址。";
                case DT_BIND_NOW:
                    return "DT_BIND_NOW: 如果此元素存在的话，动态连接器必须在程序开始执行以前，完成所有包含此项的目标的重定位工作。如果此元素存在，即使程序应用了“后期绑定”，它对于此项所指定的目标也不适用，动态连接器仍需事先做好重定位。";
                case DT_GUN_HASH:
                    return "DT_GUN_HASH: hash表。";
                default:
                    return  "其他";
            }
        }
    }
    public class elf32_hdr {
        public byte[] e_ident = new byte[16];
        public byte[] e_type = new byte[2];
        public byte[] e_machine = new byte[2];
        public byte[] e_version = new byte[4];
        public byte[] e_entry = new byte[4];
        public byte[] e_phoff = new byte[4];
        public byte[] e_shoff = new byte[4];
        public byte[] e_flags = new byte[4];
        public byte[] e_ehsize = new byte[2];
        public byte[] e_phentsize = new byte[2];
        public byte[] e_phnum = new byte[2];
        public byte[] e_shentsize = new byte[2];
        public byte[] e_shnum = new byte[2];
        public byte[] e_shstrndx = new byte[2];

        public elf32_hdr() {
        }

        public String toString() {
            return "\nmagic(魔数):" + VariableUtils.bytes2HexString(this.e_ident) +
                    "\ne_type(文件类型):" + VariableUtils.bytes2HexString(this.e_ident)+" " +e_type_toString() +
                    "\ne_machine(处理器体系结构):" + VariableUtils.bytes2HexString(this.e_machine) +
                    "\ne_version(目标文件的版本):" + VariableUtils.bytes2HexString(this.e_version) +
                    "\ne_entry(入口的虚拟地址，仅可执行文件有效):" + VariableUtils.bytes2HexString(this.e_entry) +
                    "\ne_phoff(程序头表文件偏移):" + VariableUtils.bytes2HexString(this.e_phoff) +
                    "\ne_shoff(节头表文件偏移):" + VariableUtils.bytes2HexString(this.e_shoff) +
                    "\ne_flags:" + VariableUtils.bytes2HexString(this.e_flags) +
                    "\ne_ehsize(ELF 文件头的大小):" + VariableUtils.bytes2HexString(this.e_ehsize) +
                    "\ne_phentsize(程序头表中每一个表项的大小):" + VariableUtils.bytes2HexString(this.e_phentsize) +
                    "\ne_phnum(程序头表中总共有多少个表项):" + VariableUtils.bytes2HexString(this.e_phnum) +
                    "\ne_shentsize(节头表中每一个表项的大小):" + VariableUtils.bytes2HexString(this.e_shentsize) +
                    "\ne_shnum(节头表中总共有多少个表项):" + VariableUtils.bytes2HexString(this.e_shnum) +
                    "\ne_shstrndx(节名字表):" + VariableUtils.bytes2HexString(this.e_shstrndx);
        }
        public String e_type_toString(){
            final int ET_NONE = 0;        // 未知文件类型
            final int ET_REL = 1;        // 可重定位文件
            final int ET_EXEC = 2;        // 可执行文件
            final int ET_DYN = 3;        // 动态链接库文件
            final int ET_CORE = 4;        // Core文件
            final int ET_LOPROC = 0xff00;        // 特定处理器文件扩展下边界
            final int ET_HIPROC = 0xffff;        // 特定处理器文件扩展上边界

            short value = VariableUtils.bytes2short(this.e_type);
            switch (value){
                case ET_NONE:
                    return "未知文件类型";
                case ET_REL:
                    return "可重定位文件";
                case ET_EXEC:
                    return "可执行文件";
                case ET_DYN:
                    return "动态链接库文件";
                case ET_CORE:
                    return "Core文件";
                default:
                    if(Short.toUnsignedInt(value) > ET_LOPROC && Short.toUnsignedInt(value) < ET_HIPROC){
                        return "特定处理器文件";
                    }
                    return  "错误文件类型";
            }
        }
    }

    public static class elf32_phdr {
        public byte[] p_type = new byte[4];
        public byte[] p_offset = new byte[4];
        public byte[] p_vaddr = new byte[4];
        public byte[] p_paddr = new byte[4];
        public byte[] p_filesz = new byte[4];
        public byte[] p_memsz = new byte[4];
        public byte[] p_flags = new byte[4];
        public byte[] p_align = new byte[4];

        public elf32_phdr() {
        }

        public String toString() {
            return "p_type(段的类型):" + VariableUtils.bytes2HexString(this.p_type) + p_type_toString() +
                    "\np_offset(段内容的开始位置相对于文件开头的偏移量):" + VariableUtils.bytes2HexString(this.p_offset) +
                    "\np_vaddr(本段内容的开始位置在进程空间中的虚拟地址):" + VariableUtils.bytes2HexString(this.p_vaddr) +
                    "\np_paddr(保留不用):" + VariableUtils.bytes2HexString(this.p_paddr) +
                    "\np_filesz(本段内容在文件中的大小):" + VariableUtils.bytes2HexString(this.p_filesz) +
                    "\np_memsz(本段内容在内容镜像中的大小):" + VariableUtils.bytes2HexString(this.p_memsz) +
                    "\np_flags(本段内容的属性):" + VariableUtils.bytes2HexString(this.p_flags) + p_flags_toString() +
                    "\np_align(对齐):" + VariableUtils.bytes2HexString(this.p_align);
        }

        public String p_type_toString() {


            int value = VariableUtils.bytes2int(this.p_type);
            switch (value) {
                case PT_NULL:
                    return "PT_NULL 本程序头是未使用的，本程序头内的其它成员值均无意义";
                case PT_LOAD:
                    return "PT_LOAD 此类型表明本程序头指向一个可装载的段。段在文件中大小是 p_filesz，在内存中大小是p_memsz";
                case PT_DYNAMIC:
                    return "PT_DYNAMIC 此类型表明本段指明了动态连接的信息，比如依赖于哪些共享目标文件、动态连接符号表的位置、动态连接重定位表的位置";
                case PT_INTERP:
                    return "PT_INTERP 本段指向了一个以”null”结尾的字符串，这个字符串是一个 ELF解析器的路径。";
                case PT_NOTE:
                    return "PT_NOTE 本段指向了一个以”null”结尾的字符串，这个字符串包含一些附加的信息。";
                case PT_SHLIB:
                    return "PT_SHLIB 保留";
                case PT_PHDR:
                    return "PT_PHDR 此类型的程序头如果存在的话，它表明的是其自身所在的程序头表在文件或内存中的位置和大小。";
                default:
                    if (Integer.toUnsignedLong(value) > Integer.toUnsignedLong(PT_LOPROC)
                            && Integer.toUnsignedLong(value) < Integer.toUnsignedLong(PT_HIPROC)) {
                        return "特殊处理器保留的节类型";
                    }
                    return "其他节类型";
            }
        }
        public String p_flags_toString(){
            final int PF_X = 1;
            final int PF_W = 2;
            final int PF_R = 4;
            final int PF_MASKPROC = 0xf0000000;

            int value = VariableUtils.bytes2int(this.p_flags);
            String flag = "";
            if((value & PF_X)>0){
                flag +="| PF_X:可执行(同时有可读) ";
            }
            if((value & PF_W)>0){
                flag +="| PF_W:只写(同时有可执行，可写) ";
            }
            if((value & PF_R)>0){
                flag +="| PF_R 只读 ";
            }
            if((value & PF_MASKPROC)>0){
                flag +="| PF_MASKPROC 未指定";
            }
            return flag;
        }
    }

    public class elf32_rel {
        public byte[] r_offset = new byte[4];
        public byte[] r_info = new byte[4];

        public elf32_rel() {
        }

        public String toString() {
            return "r_offset(此值是受重定位作用的存储单元的虚拟地址):" + VariableUtils.bytes2HexString(this.r_offset) +
                    "\nr_info(重定位所作用的符号表索引 & 类型):" + VariableUtils.bytes2HexString(this.r_info);
        }

        public String st_info_toString(){
            int ELF32_R_SYM = VariableUtils.bytes2int(this.r_info)>>>8;
            int ELF32_R_TYPE = VariableUtils.bytes2int(this.r_info);
            String info_string = "";
//            if (ELF32_ST_BIND == STB_LOCAL){
//                info_string += " | STB_LOCAL: 本地符号,只出现在本文件中";
//            }else if(ELF32_ST_BIND == STB_GLOBAL){
//                info_string += " | STB_GLOBAL: 全局符号,可以被所有文件引用";
//            }else if(ELF32_ST_BIND == STB_WEAK){
//                info_string += " | STB_WEAK: 全局符号,优先级更低";
//            }else if(ELF32_ST_BIND > STB_LOPROC && ELF32_ST_BIND < STB_HIPROC){
//                info_string += " | STB_LOPROC ~ STB_HIPROC: 为特殊处理器保留的属性区间";
//            }
//
//            if (ELF32_ST_TYPE == STT_NOTYPE){
//                info_string += " | STT_NOTYPE: 本符号类型未指定";
//            }else if(ELF32_ST_TYPE == STT_OBJECT){
//                info_string += " | STT_OBJECT: 全局符号,本符号是一个数据对象，比如变量、数组等";
//            }else if(ELF32_ST_TYPE == STT_FUNC){
//                info_string += " | STT_FUNC: 本符号是一个函数，当函数符号被其他模块引用时会自动创建连接表项";
//            }else if(ELF32_ST_TYPE == STT_SECTION){
//                info_string += " | STT_SECTION: 本符号与一个节相关联，用于重定位，通常具有 STB_LOCAL属性";
//            }else if(ELF32_ST_TYPE == STT_FILE){
//                info_string += " | STT_FILE: 文件符号，它具有 STB_LOCAL属性，它的节索引值是SHN_ABS。";
//            }else if(ELF32_ST_TYPE > STT_LOPROC && ELF32_ST_TYPE < STT_HIPROC){
//                info_string += " | STT_LOPROC ~ STT_HIPROC: 为特殊处理器保留的属性区间";
//            }

            return info_string;
        }
    }

    public class elf32_rela {
        public byte[] r_offset = new byte[4];
        public byte[] r_info = new byte[4];
        public byte[] r_addend = new byte[4];

        public elf32_rela() {
        }

        public String toString() {
            return "r_offset(此值是受重定位作用的存储单元的虚拟地址):" + VariableUtils.bytes2HexString(this.r_offset) +
                    "\nr_info(重定位所作用的符号表索引 & 类型):" + VariableUtils.bytes2HexString(this.r_info) +
                    "\nr_addend(这个加数用于计算需要重定位的域的值):" + VariableUtils.bytes2HexString(this.r_info);
        }
    }
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
    public static class elf32_shdr {
        public byte[] sh_name = new byte[4];
        public byte[] sh_type = new byte[4];
        public byte[] sh_flags = new byte[4];
        public byte[] sh_addr = new byte[4];
        public byte[] sh_offset = new byte[4];
        public byte[] sh_size = new byte[4];
        public byte[] sh_link = new byte[4];
        public byte[] sh_info = new byte[4];
        public byte[] sh_addralign = new byte[4];
        public byte[] sh_entsize = new byte[4];

        // 额外信息(不在对应的数据结构中)： 记录sh_name 对应的表名
        public String sh_name_string = "";

        public elf32_shdr() {
        }

        public String toString() {
            return "sh_name(section 表名索引，根据.shstrtab):" + VariableUtils.bytes2HexString(this.sh_name) +
                    "\nsh_type(section 表类型):" + VariableUtils.bytes2HexString(this.sh_type) +" "+ sh_type_toString()+
                    "\nsh_flags(section 表属性):" + VariableUtils.bytes2HexString(this.sh_flags) + " "+ sh_flags_toString()+
                    "\nsh_add(映射到进程空间的起始地址):" + VariableUtils.bytes2HexString(this.sh_addr) +
                    "\nsh_offset(本节所在的文件偏移):" + VariableUtils.bytes2HexString(this.sh_offset) +
                    "\nsh_size(节的大小):" + VariableUtils.bytes2HexString(this.sh_size) +
                    "\nsh_link(节头表中本节所对应的位置):" + VariableUtils.bytes2HexString(this.sh_link) +
                    "\nsh_info(此节的附加信息):" + VariableUtils.bytes2HexString(this.sh_info) +
                    "\nsh_addralign(如何对齐字节):" + VariableUtils.bytes2HexString(this.sh_addralign) +
                    "\nsh_entsize(每一个表项的大小):" + VariableUtils.bytes2HexString(this.sh_entsize);
        }
        public String sh_type_toString(){
            final int SHT_NULL = 0;         // 无效的（非活动的）节头
            final int SHT_PROGBITS = 1;         // 本节所含有的信息是由程序定义
            final int SHT_SYMTAB = 2;         // 较小的符号表,专门用于动态连接
            final int SHT_STRTAB = 3;         // 字符串表节
            final int SHT_RELA = 4;         // 重定位节，含有带明确加数(addend)的重定位项
            final int SHT_HASH = 5;         // 本节包含一张哈希表
            final int SHT_DYNAMIC = 6;         // 完整的符号表, 会包含很多在运行期间(动态连接)用不到的符号。
            final int SHT_NOTE = 7;         // 本节包含的信息用于以某种方式来标记本文件
            final int SHT_NOBITS = 8;         // 这一节的内容是空的，节并不占用实际的空间。
            final int SHT_REL = 9;         // 重定位节，含有带明确加数的重定位项
            final int SHT_SHLIB = 10;         // 保留值
            final int SHT_DYNSYM = 11;         // 本节是符号表。与 SHT_SYMTAB 同义
            final int SHT_LOPROC = 0x70000000;         // 为特殊处理器保留的节类型索引值的下边界
            final int SHT_HIPROC = 0x7fffffff;         // 为特殊处理器保留的节类型索引值的上边界
            final int SHT_LOUSER = 0x80000000;         // 为应用程序保留节类型索引值的下边界
            final int SHT_HIUSER = 0xffffffff;         // 为应用程序保留节类型索引值的上边界

            short value = VariableUtils.bytes2short(this.sh_type);
            switch (value){
                case SHT_NULL:
                    return "SHT_NULL 无效的（非活动的）节头";
                case SHT_PROGBITS:
                    return "SHT_PROGBITS 本节所含有的信息是由程序定义";
                case SHT_SYMTAB:
                    return "SHT_SYMTAB 较小的符号表,专门用于动态连接";
                case SHT_STRTAB:
                    return "SHT_STRTAB 字符串表节";
                case SHT_RELA:
                    return "SHT_RELA 重定位节，含有带明确加数(addend)的重定位项";
                case SHT_HASH:
                    return "SHT_HASH 本节包含一张哈希表";
                case SHT_DYNAMIC:
                    return "SHT_DYNAMIC 完整的符号表, 会包含很多在运行期间(动态连接)用不到的符号";
                case SHT_NOTE:
                    return "SHT_NOTE 本节包含的信息用于以某种方式来标记本文件";
                case SHT_NOBITS:
                    return "SHT_NOBITS 这一节的内容是空的，节并不占用实际的空间。";
                case SHT_REL:
                    return "SHT_REL 重定位节，含有带明确加数的重定位项";
                case SHT_SHLIB:
                    return "SHT_SHLIB 保留值";
                case SHT_DYNSYM:
                    return "SHT_DYNSYM 本节是符号表。与 SHT_SYMTAB 同义";
                default:
                    if(Integer.toUnsignedLong(value) > Integer.toUnsignedLong(SHT_LOPROC)
                            && Integer.toUnsignedLong(value) < Integer.toUnsignedLong(SHT_HIPROC) ){
                        return "特殊处理器保留的节类型";
                    }
                    if(Integer.toUnsignedLong(value) > Integer.toUnsignedLong(SHT_LOUSER)
                            && Integer.toUnsignedLong(value) < Integer.toUnsignedLong(SHT_HIUSER) ){
                        return "应用程序保留节类型";
                    }
                    return  "错误节类型";
            }
        }
        public String sh_name_toString(byte[] fileByteArys){
            int string_section_index = VariableUtils.bytes2short(AndroidElfParser.type_32.hdr.e_shstrndx);
            ElfType32.elf32_shdr shdr = AndroidElfParser.type_32.shdrList.get(string_section_index);
            int offset = VariableUtils.bytes2int(shdr.sh_offset);

            int sectionNameOffset = offset+VariableUtils.bytes2int(this.sh_name);
            this.sh_name_string = VariableUtils.bytes2String(fileByteArys, sectionNameOffset);

            // 表名与意义
            HashMap<String, String> map = new HashMap<>();
            map.put(".bss", "本节中包含目标文件中未初始化的全局变量。可执行程序在开始运行的时候，系统会把这一段内容清零。在非运行状态下不包含任何内容");
            map.put(".comment", "本节包含版本控制信息");
            map.put(".data", "被初始化过的全局变量");
            map.put(".data1", "被初始化过的全局变量");
            map.put(".debug", "本节中含有调试信息, 所有以”.debug”为前缀的节名字都是保留的。");
            map.put(".dynamic", "本节包含动态连接信息");
            map.put(".dynstr", "此节含有用于动态连接的字符串，一般是那些与符号表相关的名字。");
            map.put(".dynsym", "此节含有完整的动态连接符号表");
            map.put(".fini", "此节包含进程终止时要执行的程序指令。");
            map.put(".got", "此节包含全局偏移量表。");
            map.put(".hash", "本节包含一张符号哈希表。");
            map.put(".init", "此节包含进程初始化时要执行的程序指令。");
            map.put(".interp", "此节含有 ELF 程序解析器的路径名。。");
            map.put(".line", "本节也是一个用于调试的节，它包含那些调试符号的行号。");
            map.put(".note", "注释节");
            map.put(".plt", "此节包含函数连接表");
            map.put(".rel(name)?", "重定位信息。注意”name”是可替换的部分,比如.text 节的重定位节的名字将是.rel.text 或.rela.text。");
            map.put(".rela(name)?", "重定位信息。注意”name”是可替换的部分,比如.text 节的重定位节的名字将是.rel.text 或.rela.text。。");
            map.put(".rodata", "只读数据，在程序装载时，它们一般会被装入进程空间中那些只读的段中去。");
            map.put(".rodata1", "只读数据，在程序装载时，它们一般会被装入进程空间中那些只读的段中去。");
            map.put(".shstrtab", "本节是“节名字表”，含有所有其它节的名字。");
            map.put(".strtab", "本节用于存放字符串，主要是那些符号表项的名字。");
            map.put(".symtab", "本节用于存放符号表。");
            map.put(".text", "本节包含程序指令代码。");

            String note = "";
            for ( Map.Entry<String, String> entry: map.entrySet()){
                if(RegexUtils.isSearch(entry.getKey(), sh_name_string)){
                    return sh_name_string + " " +entry.getValue();
                }
            }
            return sh_name_string;
        }
        public String sh_flags_toString(){
            final int SHF_WRITE = 1;         // 本节所包含的内容在进程运行过程中是可写的
            final int SHF_ALLOC = 2;         // 本节内容在进程运行过程中要占用内存单元
            final int SHF_EXECINSTR = 4;         // 此节内容是指令代码。
            final int SHF_MASHPROC = 0xf0000000;         // 所有被此值所覆盖的位都是保留做特殊处理器扩展用的

            int value = VariableUtils.bytes2int(this.sh_flags);
            String flag = "";
            if((value & SHF_WRITE)>0){
                flag +="SHF_WRITE:本节所包含的内容在进程运行过程中是可写的 ";
            }
            if((value & SHF_ALLOC)>0){
                flag +="SHF_ALLOC:本节内容在进程运行过程中要占用内存单元 ";
            }
            if((value & SHF_EXECINSTR)>0){
                flag +="SHF_EXECINSTR 此节内容是指令代码 ";
            }
            if((value & SHF_MASHPROC)>0){
                flag +="SHF_MASHPROC 所有被此值所覆盖的位都是保留做特殊处理器扩展用的 ";
            }
            return flag;

        }
    }
    public static class Elf32_strtab {
        public byte[] d_value = new byte[4];

        public Elf32_strtab() {
        }

    }
//    public static class Elf32_strtb {
//
//        public byte[] str_name;
//        public int len;
//
//        public Elf32_strtb() {
//        }
//
//        public String toString() {
//            return "str_name:" + this.str_name + "len:" + this.len;
//        }
//    }
}


public class AndroidElfParser {
     public static ElfType32 type_32 = new ElfType32();

    /**
     * 解析Elf的头部信息
     * @param header
     */
    private static void  parseHeader(byte[] header, int offset){
        if(header == null){
            System.out.println("header is null");
            return;
        }
        /**
         *  public byte[] e_ident = new byte[16];
         public short e_type;
         public short e_machine;
         public int e_version;
         public int e_entry;
         public int e_phoff;
         public int e_shoff;
         public int e_flags;
         public short e_ehsize;
         public short e_phentsize;
         public short e_phnum;
         public short e_shentsize;
         public short e_shnum;
         public short e_shstrndx;
         */
        type_32.hdr.e_ident = VariableUtils.bytesSplite(header, 0, 16);//魔数
        type_32.hdr.e_type = VariableUtils.bytesSplite(header, 16, 2);
        type_32.hdr.e_machine = VariableUtils.bytesSplite(header, 18, 2);
        type_32.hdr.e_version = VariableUtils.bytesSplite(header, 20, 4);
        type_32.hdr.e_entry = VariableUtils.bytesSplite(header, 24, 4);
        type_32.hdr.e_phoff = VariableUtils.bytesSplite(header, 28, 4);
        type_32.hdr.e_shoff = VariableUtils.bytesSplite(header, 32, 4);
        type_32.hdr.e_flags = VariableUtils.bytesSplite(header, 36, 4);
        type_32.hdr.e_ehsize = VariableUtils.bytesSplite(header, 40, 2);
        type_32.hdr.e_phentsize = VariableUtils.bytesSplite(header, 42, 2);
        type_32.hdr.e_phnum = VariableUtils.bytesSplite(header, 44,2);
        type_32.hdr.e_shentsize = VariableUtils.bytesSplite(header, 46,2);
        type_32.hdr.e_shnum = VariableUtils.bytesSplite(header, 48, 2);
        type_32.hdr.e_shstrndx = VariableUtils.bytesSplite(header, 50, 2);
    }
    /**
     * 解析程序头信息
     * @param header
     */
    public static void parseProgramHeaderList(byte[] header, int offset){
        int header_size = 32;//32个字节
        int header_count = VariableUtils.bytes2short(type_32.hdr.e_phnum);//头部的个数
        byte[] des = new byte[header_size];
        for(int i=0;i<header_count;i++){
            System.arraycopy(header, i*header_size + offset, des, 0, header_size);
            type_32.phdrList.add(parseProgramHeader(des));
        }
    }

    private static ElfType32.elf32_phdr parseProgramHeader(byte[] header){
        /**
         *  public int p_type;
         public int p_offset;
         public int p_vaddr;
         public int p_paddr;
         public int p_filesz;
         public int p_memsz;
         public int p_flags;
         public int p_align;
         */
        ElfType32.elf32_phdr phdr = new ElfType32.elf32_phdr();
        phdr.p_type = VariableUtils.bytesSplite(header, 0, 4);
        phdr.p_offset = VariableUtils.bytesSplite(header, 4, 4);
        phdr.p_vaddr = VariableUtils.bytesSplite(header, 8, 4);
        phdr.p_paddr = VariableUtils.bytesSplite(header, 12, 4);
        phdr.p_filesz = VariableUtils.bytesSplite(header, 16, 4);
        phdr.p_memsz = VariableUtils.bytesSplite(header, 20, 4);
        phdr.p_flags = VariableUtils.bytesSplite(header, 24, 4);
        phdr.p_align = VariableUtils.bytesSplite(header, 28, 4);
        return phdr;
    }

    /**
     * 解析段头信息内容
     */
    public static void parseSectionHeaderList(byte[] header, int offset){
        int header_size = 40;//40个字节
        int header_count = VariableUtils.bytes2short(type_32.hdr.e_shnum);//头部的个数
        byte[] des = new byte[header_size];
        for(int i=0;i<header_count;i++){
            System.arraycopy(header, i*header_size + offset, des, 0, header_size);
            type_32.shdrList.add(parseSectionHeader(des));
        }
    }

    private static ElfType32.elf32_shdr parseSectionHeader(byte[] header){
        ElfType32.elf32_shdr shdr = new ElfType32.elf32_shdr();
        /**
         *  public byte[] sh_name = new byte[4];
         public byte[] sh_type = new byte[4];
         public byte[] sh_flags = new byte[4];
         public byte[] sh_addr = new byte[4];
         public byte[] sh_offset = new byte[4];
         public byte[] sh_size = new byte[4];
         public byte[] sh_link = new byte[4];
         public byte[] sh_info = new byte[4];
         public byte[] sh_addralign = new byte[4];
         public byte[] sh_entsize = new byte[4];
         */
        shdr.sh_name = VariableUtils.bytesSplite(header, 0, 4);
        shdr.sh_type = VariableUtils.bytesSplite(header, 4, 4);
        shdr.sh_flags = VariableUtils.bytesSplite(header, 8, 4);
        shdr.sh_addr = VariableUtils.bytesSplite(header, 12, 4);
        shdr.sh_offset = VariableUtils.bytesSplite(header, 16, 4);
        shdr.sh_size = VariableUtils.bytesSplite(header, 20, 4);
        shdr.sh_link = VariableUtils.bytesSplite(header, 24, 4);
        shdr.sh_info = VariableUtils.bytesSplite(header, 28, 4);
        shdr.sh_addralign = VariableUtils.bytesSplite(header, 32, 4);
        shdr.sh_entsize = VariableUtils.bytesSplite(header, 36, 4);
        return shdr;
    }

    /**
     * 解析Symbol Table内容
     */
    public static void parseSymbolTableList(byte[] header, int header_count, int offset){
        int header_size = 16;//16个字节
        byte[] des = new byte[header_size];
        for(int i=0;i<header_count;i++){
            System.arraycopy(header, i*header_size + offset, des, 0, header_size);
            type_32.symList.add(parseSymbolTable(des));
        }
    }
    /**
     * 解析Rel Table内容
     */
public static void parseJmpTableList(byte[] header, int header_count, int offset){
        int header_size = 8;//16个字节
        byte[] des = new byte[header_size];
        for(int i=0;i<header_count;i++){
            System.arraycopy(header, i*header_size + offset, des, 0, header_size);
            type_32.jmpList.add(parseJmpTable(des));
        }
    }
    public static void parseRelTableList(byte[] header, int header_count, int offset){
        int header_size = 8;//16个字节
        byte[] des = new byte[header_size];
        for(int i=0;i<header_count;i++){
            System.arraycopy(header, i*header_size + offset, des, 0, header_size);
            type_32.relList.add(parseRelTable(des));
        }
    }
    public static void parseHashTable(byte[] header, int offset){
        type_32.hash = new ElfType32.Elf32_hash();
        type_32.hash.nbucket =VariableUtils.bytesSplite(header, offset, 4);
        type_32.hash.symoffset =VariableUtils.bytesSplite(header, offset+4, 4);
        type_32.hash.bloom_size =VariableUtils.bytesSplite(header, offset+8, 4);
        type_32.hash.bloom_shift =VariableUtils.bytesSplite(header, offset+12, 4);
        type_32.hash.getBucketArray(header, offset);
        System.out.println(type_32.hash.toString());

        // 遍历导出
        int symoffset = VariableUtils.bytes2int(type_32.hash.symoffset);
        for(int i =0; i< type_32.hash.bucket.length ; i++){
            System.out.print("第 "+ i+" 个bucket");
            System.out.print("符号: "+ Integer.toHexString(type_32.hash.bucket[i]));
            System.out.println();
            if(type_32.hash.bucket[i] ==0){
                continue;
            }
            while (true){
                byte[] chain = type_32.hash.getChainArray(header, offset,symoffset-VariableUtils.bytes2int(type_32.hash.symoffset));
                System.out.print(" * "+ parseSymbolTableListD(header, symoffset).st_name_toString(header));
                System.out.print(" "+ VariableUtils.bytes2HexStringReverse(chain));
                System.out.println();
                symoffset+=1;
                if((chain[0]&1) ==1){
                    break;
                }
            }

        }
    }

    /**
     * 解析Symbol Table内容
     * @param header 文件字节
     * @param offset 要查询的符号的索引
     */
    public static ElfType32.Elf32_Sym parseSymbolTableListD(byte[] header, int offset){
        int header_size = 16;//16个字节
        byte[] des = new byte[header_size];
        des = VariableUtils.bytesSplite(header, offset*header_size + type_32.symList_d, header_size);
        ElfType32.Elf32_Sym obj =parseSymbolTable(des);
//        System.out.println("正在解析的符号： "+ obj.st_name_toString(header));
//        System.out.println(obj.toString());
        return obj;
    }

    private static void parseDynamicSegment(byte[] fileByteArys, int offset_dynamic) {
        int header_size = 8;//8个字节
        byte[] des = new byte[header_size];

        for(int i=0;;i++){
            System.arraycopy(fileByteArys, i*header_size + offset_dynamic, des, 0, header_size);
            type_32.dynamicList.add(parseDynamicSegment(des));
            if(VariableUtils.bytes2int(type_32.dynamicList.get(type_32.dynamicList.size()-1).d_tag) == 0)
                return;
        }
    }

    private static ElfType32.Elf32_Sym parseSymbolTable(byte[] header){
        /**
         *  public byte[] st_name = new byte[4];
         public byte[] st_value = new byte[4];
         public byte[] st_size = new byte[4];
         public byte st_info;
         public byte st_other;
         public byte[] st_shndx = new byte[2];
         */
        ElfType32.Elf32_Sym sym = new ElfType32.Elf32_Sym();
        sym.st_name = VariableUtils.bytesSplite(header, 0, 4);
        sym.st_value = VariableUtils.bytesSplite(header, 4, 4);
        sym.st_size = VariableUtils.bytesSplite(header, 8, 4);
        sym.st_info = header[12];
        //FIXME 这里有一个问题，就是这个字段读出来的值始终是0
        sym.st_other = header[13];
        sym.st_shndx = VariableUtils.bytesSplite(header, 14, 2);
        return sym;
    }
    private static ElfType32.Elf32_Jmp parseJmpTable(byte[] header){
        /**
         *  public byte[] st_name = new byte[4];
         public byte[] st_value = new byte[4];
         public byte[] st_size = new byte[4];
         public byte st_info;
         public byte st_other;
         public byte[] st_shndx = new byte[2];
         */
        ElfType32.Elf32_Jmp rel = new ElfType32.Elf32_Jmp();
        rel.r_offset = VariableUtils.bytesSplite(header, 0, 4);
        rel.r_info = VariableUtils.bytesSplite(header, 4, 4);
        return rel;
    }

    private static ElfType32.Elf32_Rel parseRelTable(byte[] header){
        /**
         *  public byte[] st_name = new byte[4];
         public byte[] st_value = new byte[4];
         public byte[] st_size = new byte[4];
         public byte st_info;
         public byte st_other;
         public byte[] st_shndx = new byte[2];
         */
        ElfType32.Elf32_Rel rel = new ElfType32.Elf32_Rel();
        rel.r_offset = VariableUtils.bytesSplite(header, 0, 4);
        rel.r_info = VariableUtils.bytesSplite(header, 4, 4);
        return rel;
    }


    private static ElfType32.Elf32_Dynamic parseDynamicSegment(byte[] header){
        /**
         *  public byte[] st_name = new byte[4];
         public byte[] st_value = new byte[4];
         public byte[] st_size = new byte[4];
         public byte st_info;
         public byte st_other;
         public byte[] st_shndx = new byte[2];
         */
        ElfType32.Elf32_Dynamic sym = new ElfType32.Elf32_Dynamic();
        sym.d_tag = VariableUtils.bytesSplite(header, 0, 4);
        sym.d_val_ptr = VariableUtils.bytesSplite(header, 4, 4);
        return sym;
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


    public static int vaddr2offset(int vaddr){
        for (int i = 0; i < type_32.memList.size(); ++i) {
            if( type_32.memList.get(i).isInSegment(vaddr)){
                return type_32.memList.get(i).vaddr2offset(vaddr);
            }
        }
        return 0;
    }

    public static void parseSo(byte[] fileByteArys) {
        //读取头部内容
        System.out.println("+++++++++++++++++++Elf Header+++++++++++++++++");
        parseHeader(fileByteArys, 0);
        System.out.println("header:\n" + type_32.hdr);

        //读取节表信息
        System.out.println();
        System.out.println("+++++++++++++++++++Section Header++++++++++++++++++");
        int s_header_offset = VariableUtils.bytes2int(type_32.hdr.e_shoff);
        System.out.println("offset:" + s_header_offset);
        parseSectionHeaderList(fileByteArys, s_header_offset);
        type_32.printShdrList(fileByteArys);

        //读取符号表信息(Symbol Table)
        System.out.println();
        System.out.println("+++++++++++++++++++Symbol Table++++++++++++++++++");
        // 通过表的类型 SHT_DYNSYM定位符号表,
        int offset_sym = 0;
        int total_sym = 0;
        for (ElfType32.elf32_shdr shdr : type_32.shdrList) {
            if (VariableUtils.bytes2int(shdr.sh_type) == ElfType32.SHT_DYNSYM) {
                total_sym = VariableUtils.bytes2int(shdr.sh_size);
                offset_sym = VariableUtils.bytes2int(shdr.sh_offset);
                break;
            }
        }
        // 每个符号表项固定16个字节, 所以符号项数目=大小/16
        int num_sym = total_sym / 16;
        System.out.println("sym num=" + num_sym);
        parseSymbolTableList(fileByteArys, num_sym, offset_sym);
        type_32.printSymList(fileByteArys);

        //读取程序头信息
        System.out.println();
        System.out.println("+++++++++++++++++++Program Header+++++++++++++++++");
        int p_header_offset = VariableUtils.bytes2int(type_32.hdr.e_phoff);
        parseProgramHeaderList(fileByteArys, p_header_offset);
        type_32.printPhdrList();

        // 分析内存映射情况
        System.out.println();
        System.out.println("+++++++++++++++++++ 内存映射分析 ++++++++++++++++++");
        for (ElfType32.elf32_phdr phdr : type_32.phdrList) {
            if (VariableUtils.bytes2int(phdr.p_type) == ElfType32.PT_LOAD) {
                int mem_vaddr_align = (VariableUtils.bytes2int(phdr.p_vaddr) / 0x1000) * 0x1000;
                int mem_end_vaddr = VariableUtils.bytes2int(phdr.p_vaddr) + VariableUtils.bytes2int(phdr.p_memsz);
                int mem_end_vaddr_align = mem_end_vaddr / 0x1000 * 0x1000 + (mem_end_vaddr % 0x1000 != 0 ? 1 : 0) * 0x1000;
                int file_offset_align = (VariableUtils.bytes2int(phdr.p_offset) / 0x1000) * 0x1000;
                int file_end_vaddr = VariableUtils.bytes2int(phdr.p_vaddr) + VariableUtils.bytes2int(phdr.p_filesz);
                int file_end_vaddr_align = file_end_vaddr / 0x1000 * 0x1000 + (file_end_vaddr % 0x1000 != 0 ? 1 : 0) * 0x1000;
                ElfType32.Elf32_MemMap memMap = new ElfType32.Elf32_MemMap();
                memMap.mem_vaddr_align = mem_vaddr_align;
                memMap.mem_end_vaddr_align = mem_end_vaddr_align;
                memMap.file_offset = file_offset_align;
                memMap.file_end_vaddr_align = file_end_vaddr_align;
                type_32.memList.add(memMap);
                System.out.print(" 内存起始地址: " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_vaddr_align)) + " - " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr_align)));
                System.out.print(" 内存大小: " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr_align - mem_vaddr_align)));
                System.out.print(" 文件偏移起始地址: " + VariableUtils.bytes2HexString(VariableUtils.int2bytes((VariableUtils.bytes2int(phdr.p_offset) / 0x1000) * 0x1000)));
                System.out.println();
                if (mem_end_vaddr_align - file_end_vaddr_align > 0) {
                    System.out.print(" * (未映射到文件部分)内存起始地址: " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(file_end_vaddr_align)) + " - " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr_align)));
                    System.out.print(" 内存大小: " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr_align - file_end_vaddr_align)));
                    System.out.println();
                }
            }
            if (VariableUtils.bytes2int(phdr.p_type) == ElfType32.PT_GNU_RELRO) {
                int mem_vaddr = (VariableUtils.bytes2int(phdr.p_vaddr) / 0x1000) * 0x1000;
                int memsz_align = VariableUtils.bytes2int(phdr.p_memsz) / 0x1000 + (VariableUtils.bytes2int(phdr.p_memsz) % 0x1000 != 0 ? 1 : 0);
                int mem_end_vaddr = mem_vaddr + memsz_align * 0x1000;
                System.out.print(" * (只读的数据段部分)内存起始地址: " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_vaddr)) + " - " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(mem_end_vaddr)));
                System.out.print("  内存起始地址: " + VariableUtils.bytes2HexString(VariableUtils.int2bytes(memsz_align * 0x1000)));
                System.out.println();
            }
        }
        //读取动态链接段信息(dynamic segment))
        System.out.println();
        System.out.println("+++++++++++++++++++dynamic segment++++++++++++++++++");
        int offset_dynamic = 0;
        for (ElfType32.elf32_phdr phdr : type_32.phdrList) {
            if (VariableUtils.bytes2int(phdr.p_type) == ElfType32.PT_DYNAMIC) {
                offset_dynamic = VariableUtils.bytes2int(phdr.p_offset);
                break;
            }
        }

        parseDynamicSegment(fileByteArys, offset_dynamic);
        type_32.printDynamicList(fileByteArys);

        // 读取字符串表的内容
        System.out.println();
        System.out.println("+++++++++++++++++++字符串表 of dynamic segment++++++++++++++++++");
        int vaddr = 1;
        while (true) {
            String str = DT_STRTAB_getString(fileByteArys, vaddr);
            System.out.println("偏移： 0x" + Integer.toHexString(vaddr) + " 字符串: " + str);
            if (str.equals("")) {
                break;
            }
            vaddr += str.length() + 1;
        }

        // 读取导入库的内容
        System.out.println();
        System.out.println("+++++++++++++++++++导入库 of dynamic segment++++++++++++++++++");
        // 遍历字符串表
        for (int i = 0; i < type_32.dynamicList.size(); ++i) {
            if (VariableUtils.bytes2int(type_32.dynamicList.get(i).d_tag) == ElfType32.DT_NEEDED) {
                vaddr = VariableUtils.bytes2int(type_32.dynamicList.get(i).d_val_ptr);
                String str = DT_STRTAB_getString(fileByteArys, vaddr);
                System.out.println("导入库： " + str);
            }
        }
        //读取符号表信息(Symbol Table)
        System.out.println();
        System.out.println("+++++++++++++++++++符号表 of dynamic segment++++++++++++++++++");
        // 通过表的类型 DT_SYMTAB 定位符号表
        for (ElfType32.Elf32_Dynamic phdr : type_32.dynamicList) {
            if (VariableUtils.bytes2int(phdr.d_tag) == ElfType32.DT_SYMTAB) {
                offset_sym = VariableUtils.bytes2int(phdr.d_val_ptr);
            }
        }
        // 内存偏移转换为文件偏移
        type_32.symList_d = vaddr2offset(offset_sym);
        System.out.println("DT_SYMTAB符号表 基址： 0x" + Integer.toHexString(offset_sym));
        // 确定符号表的总项数, 通过最后一项为0来确定
        System.out.println("测试偏移为1的符号: ");
        parseSymbolTableListD(fileByteArys, 1);

        //读取导入表信息(Symbol Table)
        System.out.println();
        System.out.println("+++++++++++++++++++导入表 of dynamic segment++++++++++++++++++");
        // 通过表的类型 DT_JMPREL 定位导入表
        int offset_jmp = 0;
        int size_jmp = 0;
        for (ElfType32.Elf32_Dynamic phdr : type_32.dynamicList) {
            if (VariableUtils.bytes2int(phdr.d_tag) == ElfType32.DT_JMPREL) {
                offset_jmp = VariableUtils.bytes2int(phdr.d_val_ptr);
            }
            if (VariableUtils.bytes2int(phdr.d_tag) == ElfType32.DT_PLTRELSZ) {
                size_jmp = VariableUtils.bytes2int(phdr.d_val_ptr)/8;
            }
        }
        // 内存偏移转换为文件偏移
        offset_jmp = vaddr2offset(offset_jmp);
        System.out.println("DT_JMPREL 导入表 基址： 0x" + Integer.toHexString(offset_jmp) + " 导入表项目个数: " + size_jmp);
        parseJmpTableList(fileByteArys, size_jmp, offset_jmp);
        type_32.printJmpList(fileByteArys);

        //读取重定位表信息(DT_REL)
        System.out.println();
        System.out.println("+++++++++++++++++++重定位表 of dynamic segment++++++++++++++++++");
        // 通过表的类型 DT_REL 重定位表
        int offset_rel = 0;
        int size_rel = 0;
        for (ElfType32.Elf32_Dynamic phdr : type_32.dynamicList) {
            if (VariableUtils.bytes2int(phdr.d_tag) == ElfType32.DT_REL) {
                offset_rel = VariableUtils.bytes2int(phdr.d_val_ptr);
            }
            if (VariableUtils.bytes2int(phdr.d_tag) == ElfType32.DT_RELSZ) {
                size_rel = VariableUtils.bytes2int(phdr.d_val_ptr)/8;
            }
        }
        System.out.println("DT_REL 重定位表 文件偏移： 0x" + Integer.toHexString(offset_rel) + " 重定位表项目个数: " + size_rel);
        // 内存偏移转换为文件偏移
        offset_rel = vaddr2offset(offset_rel);
        parseRelTableList(fileByteArys, size_rel, offset_rel);
        type_32.printRelList(fileByteArys);

        //读取hash表信息(DT_REL)
        System.out.println();
        System.out.println("+++++++++++++++++++hash表 of dynamic segment++++++++++++++++++");
        // 通过表的类型 DT_REL 重定位表
        int offset_hash = 0;
        for (ElfType32.Elf32_Dynamic phdr : type_32.dynamicList) {
            if (VariableUtils.bytes2int(phdr.d_tag) == ElfType32.DT_GUN_HASH) {
                offset_hash = VariableUtils.bytes2int(phdr.d_val_ptr);
            }
        }
        System.out.println("DT_GUN_HASH hash表 文件偏移： 0x" + Integer.toHexString(offset_hash));
        // 内存偏移转换为文件偏移
        offset_hash = vaddr2offset(offset_hash);
        parseHashTable(fileByteArys,  offset_hash);

//        // 内存偏移转换为文件偏移
//        offset_rel = vaddr2offset(offset_rel);
//        parseRelTableList(fileByteArys, size_rel, offset_rel);
//        type_32.printRelList(fileByteArys);
    }

    public static void main(String[] args){
        // 对ELF的修改与解析
//        CmdUtils.runCMD("adb push ./so/ls /data/user/ls");
//        CmdUtils.runCMD("adb shell 'chmod +x /data/user/ls'");
//        CmdUtils.runCMD("adb shell '/data/user/ls'");

        byte[] fileByteArys = FileUtils.readFileToBytes("./so/ls");
        if(fileByteArys == null){
            System.out.println("read file byte failed...");
            return;
        }
        /**
         * 先解析so文件
         * 然后初始化AddSection中的一些信息
         * 最后在AddSection
         */
         parseSo(fileByteArys);

         // 模拟内存映射
//
//        //初始化AddSection中的变量
//        AddSection.sectionHeaderOffset = VariableUtils.byte2Int(type_32.hdr.e_shoff);
//        AddSection.stringSectionInSectionTableIndex = VariableUtils.byte2Short(type_32.hdr.e_shstrndx);
//        AddSection.stringSectionOffset = VariableUtils.byte2Int(type_32.shdrList.get(AddSection.stringSectionInSectionTableIndex).sh_offset);
//        //找到第一个和最后一个类型为Load的Program header的index
//        boolean flag = true;
//        for(int i=0;i<type_32.phdrList.size();i++){
//            if(VariableUtils.byte2Int(type_32.phdrList.get(i).p_type) == 1){//LOAD的type==1,可在elf格式文档中找到
//                if(flag){
//                    AddSection.firstLoadInPHIndex = i;
//                    flag = false;
//                }else{
//                    AddSection.lastLoadInPHIndex = i;
//                }
//            }
//        }
//        int lastLoadVaddr = VariableUtils.byte2Int(type_32.phdrList.get(AddSection.lastLoadInPHIndex).p_vaddr);
//        int lastLoadMem = VariableUtils.byte2Int(type_32.phdrList.get(AddSection.lastLoadInPHIndex).p_memsz);
//        int lastLoadAlign = VariableUtils.byte2Int(type_32.phdrList.get(AddSection.lastLoadInPHIndex).p_align);
//        AddSection.addSectionStartAddr = VariableUtils.align(lastLoadVaddr + lastLoadMem, lastLoadAlign);
//        System.out.println("start addr hex:"+VariableUtils.bytes2HexString(VariableUtils.int2Byte(AddSection.addSectionStartAddr)));
//
//        /**
//         * 添加一个Section
//         * 以下的顺序不可乱，不然会出错的
//         * 1、添加一个Section Header
//         * 2、直接在文件的末尾追加一个section
//         * 3、修改String段的长度
//         * 4、修改Elf Header中的section count
//         */
//        fileByteArys = AddSection.addSectionHeader(fileByteArys);
//        fileByteArys = AddSection.addNewSectionForFileEnd(fileByteArys);
//        fileByteArys = AddSection.changeStrtabLen(fileByteArys);
//        fileByteArys = AddSection.changeElfHeaderSectionCount(fileByteArys);
//        fileByteArys = AddSection.changeProgramHeaderLoadInfo(fileByteArys);
//
//        VariableUtils.saveFile("so/libhello-jnis.so", fileByteArys);

    }
}
