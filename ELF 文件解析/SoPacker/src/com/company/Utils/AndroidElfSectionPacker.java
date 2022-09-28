package com.company.Utils;



public class AndroidElfSectionPacker {

    public static String encodeSectionName = ".mytext";
    public static String unpackSoPath = "so/libdemo.so";
    public static String packedSoPath = "so/libdemos.so";

//    public static ElfType32 type_32 = new ElfType32();

    public static void main(String[] args){

        byte[] fileByteArys = FileUtils.readFileToBytes(unpackSoPath);
        if(fileByteArys == null){
            System.out.println("read file byte failed...");
            return;
        }

        // 1. 先解析so文件
        AndroidElfParser.parseSo(fileByteArys);
        // 2. 对Section进行加密
        encodeSection(fileByteArys);
        // 检查
//        AndroidElfParser.parseSo(fileByteArys);
        FileUtils.writeFileWithBytes( fileByteArys,packedSoPath);
    }

    private static void encodeSection(byte[] fileByteArys){
        //读取String Section段
        System.out.println();
        // 遍历section 表名
        int string_section_index = VariableUtils.bytes2short(AndroidElfParser.type_32.hdr.e_shstrndx);
        ElfType32.elf32_shdr shdr = AndroidElfParser.type_32.shdrList.get(string_section_index);
        int size = VariableUtils.bytes2int(shdr.sh_size);
        int offset = VariableUtils.bytes2int(shdr.sh_offset);

        int mySectionOffset=0,mySectionSize=0;
        for(ElfType32.elf32_shdr temp : AndroidElfParser.type_32.shdrList){
            int sectionNameOffset = offset+VariableUtils.bytes2int(temp.sh_name);
            // 判断是否为.mytext节
            if(VariableUtils.bytesCompareString(fileByteArys, sectionNameOffset, encodeSectionName)){
                //这里需要读取section段然后进行数据加密
                mySectionOffset = VariableUtils.bytes2int(temp.sh_offset);
                mySectionSize = VariableUtils.bytes2int(temp.sh_size);
                byte[] sectionAry = VariableUtils.bytesSplite(fileByteArys, mySectionOffset, mySectionSize);
                // 加密
                for(int i=0;i<sectionAry.length;i++){
                    sectionAry[i] = (byte)(sectionAry[i] ^ 0xFF);
                }
                // 写入
                VariableUtils.replaceByteAry(fileByteArys, mySectionOffset, sectionAry);
            }
        }

        // 修改Elf Header中的entry和offset值, 用于快速定位
        int nSize = mySectionSize/4096 + (mySectionSize%4096 == 0 ? 0 : 1);
        byte[] entry = new byte[4];
        entry = VariableUtils.int2bytes((mySectionSize<<16) + nSize);
        VariableUtils.replaceByteAry(fileByteArys, 24, entry);
        // 加密section table offset, 这样DIA和readelf就无法正常查询section表
        byte[] offsetAry = new byte[4];
        offsetAry = VariableUtils.int2bytes(mySectionOffset);
        VariableUtils.replaceByteAry(fileByteArys, 32, offsetAry);
    }
}
