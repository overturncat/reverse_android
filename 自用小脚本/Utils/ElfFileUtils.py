#   安装 pyelftools 库
from elftools.elf.elffile import ELFFile
import lief

class ElfFileUtils(object):

    @staticmethod
    def modifyEntry(elf_path, out_path):
        ''' 
        循环代码
        intel： EB FE 
        ARM: 
        * **ARM Mode**:  FE FF FF EA
        * **Thumb Mode**: FE E7 
         '''
        binary = lief.parse(elf_path)
        binary.patch_address(binary.header.entrypoint, [0x12, 0x34])
        binary.write(out_path)

    @staticmethod
    def info(elf_path):
        # 打开 elf 文件
        file = open(elf_path, 'rb')
        # 创建 ELFFile 对象 , 该对象是核心对象
        elf_file = ELFFile(file)

        # 打印 elf 文件头
        print(elf_file.header)
        # 打印 程序头入口 个数
        print(elf_file.num_segments())
        # 打印 节区头入口 个数
        print(elf_file.num_sections())

        # 遍历打印 程序头入口
        for segment in elf_file.iter_segments():
            print(segment.header)
            print(segment.header['p_align'])

        # 遍历打印 节区头入口
        for section in elf_file.iter_sections():
            print('name:', section.name)
            print('header', section.header)
        # 关闭文件
        file.close()


if __name__ == '__main__':
    ElfFileUtils.modifyEntry("/home/ayuan/code/back_baidu/项目/android_github/自用小脚本/Utils/ls", "./out/out.bin")
