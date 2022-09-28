package com.company.Utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VariableUtils {
    public static void info() {
        // 定义int类型变量x，并赋予初始值100
        System.out.println("* 基本变量类型：int(包装类:Integer)");
        int x = 100;        // 定义
        x = 12345 / 67;     // 两个整数相除只能得到结果的整数部分
        x = 12345 % 67;     // 12345÷67的余数是17
        x += 100;               // 支持简写
        x++;                    // 支持自增自减

        System.out.println("** 整数的位运算");
        int n = 7, a = 0;     // 00000000 00000000 00000000 00000111 = 7
        a = n >> 1;         // 00000000 00000000 00000000 00000011 = 3 支持算术移位, 正数补0负数补1
        a = x >>> 1;        // 不管符号位，右移后高位总是补0
        n = 166 & 1;        // 与运算的规则是，必须两个数同时为1，结果才为1：
        n = 0 | 0;          // 或运算的规则是，只要任意一个为1，结果就为1：
        n = ~0;             // 非运算的规则是，0和1互换：
        n = 0 ^ 1;          // 异或运算的规则是，如果两个数不同，结果为1，否则为0：

        System.out.println("* 基本变量类型: long(包装类:Long)");
        long l = 9000000000000000000L; // long型的结尾需要加L

        System.out.println("* 基本变量类型: float(包装类:Float),double(包装类:Double)");
        float f1 = 3.14f;   // 对于float类型，需要加上f后缀。
        float f2 = 3.14e38f; // 科学计数法表示的3.14x10^38
        double d = 1.79e308;
        double d3 = 4.9e-324; // 科学计数法表示的4.9x10^-324
        System.out.println("** 浮点数的相等比较");
        // 浮点数0.1在计算机中就无法精确表示,所以判断两个浮点数之差的绝对值是否小于一个很小的数
        if (Math.abs(f1 - f2) < 0.00001) {
            // 可以认为相等
        } else {
            // 不相等
        }
        System.out.println("** 浮点数的相等比较");
        // 强制转型,在转型时，浮点数的小数部分会被丢掉。
        int n2 = (int) 12.7; // 12
        int n3 = (int) -12.7; // -12

        System.out.println("* 基本变量类型: boolean(包装类:Boolean)");
        boolean b1 = true;
        boolean isGreater = 5 > 3; // 计算结果为true

        System.out.println("** 常见运算符及其优先级");
        /*
        1.运算符的优先级从高到低依次是：
        ()
        ! ~ ++ --
        * / %
        + -
        << >> >>>
        & (短路运算符&&)
        | (短路运算符||)
        += -= *= /=
        */

        // 字符类型,ava的char可表示一个标准的ASCII或者Unicode字符
        System.out.println("* 基本变量类型: char(包装类:Character)");
        char cha = 'A';
        char zh = '中';
        char c4 = '\u4e2d'; // '中'，因为十六进制4e2d = 十进制20013

        // 字符串类型
        System.out.println("* 基本变量类型: String");
        String s = "hello";        //字符串,引用类型,使用双引号"
        String s1 = "ABC\n\u4e2d\u6587"; // 包含6个字符: A, B, C, 换行符, 中, 文
        // 支持+运算, 并且其他数据类型也会自动转化为字符串
        System.out.println("** 字符串拼接");
        String s2 = "Hello" + "world";
        String s3 = "age is " + 15;
        // 字符串变量存储的是地址(类似python中的不可变变量), 当再次赋值时会直接指向另一个地址的字符串，而不是在源地址上修改
        System.out.println("** 字符串的赋值");
        String s0 = "hello";
        String t = s0;
        s0 = "world";           // 字符串t == "hello"

        // 数组类型
        // 常量,final修饰符，在定义时进行初始化后就不可再次赋值
        final double PI = 3.14; // PI是一个常量
    }

    // 数组变量类型
    public static void infoArray() {
        System.out.println("* 一维数组");
        // 一维数组的初始化, 分配空间，都有默认初始值
        System.out.println("** 一维数组的初始化");
        int[] ns1 = new int[5];
        int[] ns2 = new int[]{68, 79, 91, 85, 62};
        int[] ns3 = {68, 79, 91, 85, 62};
        Integer[] ns4 = {68, 79, 91, 85, 62};
        // 获取数组的长度
        System.out.println("** 一维数组的长度");
        int len = getArrayLength(ns4);
        System.out.println("** 一维数组转换为字符串,快速打印数组内容");
        String ns4_str = getArrayString(ns4); //[68, 79, 91, 85, 62]
        System.out.println("** 遍历数组");
        for (int i=0; i<ns4.length; i++) {
            int n = ns4[i];
            System.out.println(n);
        }
        // 数组的传参， 由于传的是地址，会改变数组中的值
        System.out.println("** 数组传参");

        // 二维数组
        System.out.println("* 二维数组");
        // 二维数组,每个数组元素的长度并不要求相同
        System.out.println("** 二维数组的初始化");
        Integer[][] n2s2 = {
                {1, 2, 3, 4},
                {5, 6},
                {7, 8, 9}
        };
        // 遍历二维数组
        for (Integer[] item : n2s2) {
            for (Integer n : item) {
                System.out.print(n);
                System.out.print(',');
            }
            System.out.println();
        }
        // Arrays.deepToString()二维数组转化为字符串,快速打印数组内容
        System.out.println("** 二维数组转换为字符串,快速打印数组内容");
        String n2s2_str = get2ArrayString(n2s2);//[[1, 2, 3, 4], [5, 6], [7, 8, 9]]
    }
    // 返回一维数组的长度
    public static int getArrayLength(Object[] arr){
        return arr.length;
    }
    // 返回一维数组的字符串
    public static String getArrayString(Object[] arr){
        return Arrays.toString(arr);
    }
    // 返回二维数组的字符串
    public static String get2ArrayString(Object[] arr){
        return Arrays.deepToString(arr);
    }
    // 数组排序
    public static void sortArray(Object[] arr){
        Arrays.sort(arr);
    }

    // 字节操作: 截取指定字节序列
    public static byte[] bytesSplite(byte[] src, int start, int len){
        if(src == null){
            return null;
        }
        if(start > src.length){
            return null;
        }
        if((start+len) > src.length){
            return null;
        }
        if(start<0){
            return null;
        }
        if(len<=0){
            return null;
        }
        byte[] resultByte = new byte[len];
        for(int i=0;i<len;i++){
            resultByte[i] = src[i+start];
        }
        return resultByte;
    }
    /**
     * 在字节数组src的offset偏移处， 替换字节数组copyByte
     * @param src
     * @param offset
     * @param copyByte
     * @return
     */
    public static byte[] replaceByteAry(byte[] src, int offset, byte[] copyByte){
        for(int i=offset;i<offset+copyByte.length;i++){
            src[i] = copyByte[i-offset];
        }
        return src;
    }
    // 字节操作：返回一个翻转后的序列
    public static byte[] bytesReverse(byte[] bytess){
        byte[] bytes = new byte[bytess.length];
        for(int i=0;i<bytess.length;i++){
            bytes[i] = bytess[i];
        }
        if(bytes == null || (bytes.length % 2) != 0){
            return bytes;
        }
        int i = 0, len = bytes.length;
        while(i < (len/2)){
            byte tmp = bytes[i];
            bytes[i] = bytes[len-i-1];
            bytes[len-i-1] = tmp;
            i++;
        }
        return bytes;
    }
    // 字节操作：返回字节的十六进制字符串的翻转, 如 01 02 返回 "02 01"
    public static String bytes2HexStringReverse(byte[] src1){
        byte[] src = bytesReverse(src1);
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv+"");
        }
        return "0x"+stringBuilder.toString();
    }
    // 字节操作：返回字节的十六进制字符串, 如 01 02 返回 "01 02"
    public static String bytes2HexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv+" ");
        }
        return stringBuilder.toString();
    }
    // 字节操作：bytes[] --> int
    public static int bytes2int(byte[] res) {
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00)
                | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }
    public static byte[] int2bytes(final int integer) {
        int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer))/ 8;
        byte[] byteArray = new byte[4];

        for (int n = 0; n < byteNum; n++)
            byteArray[3 - n] = (byte) (integer>>> (n * 8));

        return (byteArray);
    }
    public static byte[] short2bytes(final short integer) {
        byte[] byteArray = new byte[2];

        for (int n = 0; n < 2; n++)
            byteArray[1 - n] = (byte) (integer>>> (n * 8));

        return (byteArray);
    }
    public static short bytes2short(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }
    // 过滤字符串中的\00字节
    public static String filterStringNull(String str){
        if(str == null || str.length() == 0){
            return str;
        }
        byte[] strByte = str.getBytes();
        ArrayList<Byte> newByte = new ArrayList<Byte>();
        for(int i=0;i<strByte.length;i++){
            if(strByte[i] != 0){
                newByte.add(strByte[i]);
            }
        }
        byte[] newByteAry = new byte[newByte.size()];
        for(int i=0;i<newByteAry.length;i++){
            newByteAry[i] = newByte.get(i);
        }
        return new String(newByteAry);
    }


    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName ("UTF-8");
        ByteBuffer bb = ByteBuffer.allocate (bytes.length);
        bb.put (bytes);
        bb.flip ();
        CharBuffer cb = cs.decode (bb);
        return cb.array();
    }

    public static String getStringFromByteAry(byte[] srcByte, int start) {
        if (srcByte == null) {
            return "";
        }
        if (start < 0) {
            return "";
        }
        if (start >= srcByte.length) {
            return "";
        }
        byte val = srcByte[start];
        int i = 1;
        ArrayList<Byte> byteList = new ArrayList<Byte>();
        while (val != 0) {
            byteList.add(srcByte[start + i]);
            val = srcByte[start + i];
            i++;
        }
        byte[] valAry = new byte[byteList.size()];
        for (int j = 0; j < byteList.size(); j++) {
            valAry[j] = byteList.get(j);
        }
        try {
            return new String(valAry, "UTF-8");
        } catch (Exception e) {
            System.out.println("encode error:" + e.toString());
            return "";
        }
    }
    /**
     * 解码leb128数据
     * 每个字节去除最高位，然后进行拼接，重新构造一个int类型数值，从低位开始
     * @param byteAry
     * @return
     */
    public static int decodeUleb128(byte[] byteAry) {
        int index = 0, cur;
        int result = byteAry[index];
        index++;

        if(byteAry.length == 1){
            return result;
        }

        if(byteAry.length == 2){
            cur = byteAry[index];
            index++;
            result = (result & 0x7f) | ((cur & 0x7f) << 7);
            return result;
        }

        if(byteAry.length == 3){
            cur = byteAry[index];
            index++;
            result |= (cur & 0x7f) << 14;
            return result;
        }

        if(byteAry.length == 4){
            cur = byteAry[index];
            index++;
            result |= (cur & 0x7f) << 21;
            return result;
        }

        if(byteAry.length == 5){
            cur = byteAry[index];
            index++;
            result |= cur << 28;
            return result;
        }
        return result;
    }
    /**
     * 读取C语言中的uleb类型
     * 目的是解决整型数值浪费问题
     * 长度不固定，在1~5个字节中浮动
     * @param srcByte
     * @param offset
     * @return
     */
    public static byte[] readUnsignedLeb128(byte[] srcByte, int offset){
        List<Byte> byteAryList = new ArrayList<Byte>();
        byte bytes = bytesSplite(srcByte, offset, 1)[0];
        byte highBit = (byte)(bytes & 0x80);
        byteAryList.add(bytes);
        offset ++;
        while(highBit != 0){
            bytes = bytesSplite(srcByte, offset, 1)[0];
            highBit = (byte)(bytes & 0x80);
            offset ++;
            byteAryList.add(bytes);
        }
        byte[] byteAry = new byte[byteAryList.size()];
        for(int j=0;j<byteAryList.size();j++){
            byteAry[j] = byteAryList.get(j);
        }
        return byteAry;
    }

    /**
     * 判断字节数组是否与给定的字符串相等
     * @param src 字节数组
     * @param start 开始比较的偏移
     * @param destStr 要比较的字符串
     * @return
     */
    public static boolean bytesCompareString(byte[] src, int start, String destStr){
        if(destStr == null){
            return false;
        }
        byte[] dest = destStr.getBytes();
        if(src == null || dest == null){
            return false;
        }
        if(dest.length == 0 || src.length == 0){
            return false;
        }
        if(start >= src.length){
            return false;
        }

        int len = 0;
        byte temp = src[start];
        while(temp != 0){
            len++;
            temp = src[start+len];
        }

        byte[] sonAry = bytesSplite(src, start, len);
        if(sonAry == null || sonAry.length == 0){
            return false;
        }
        if(sonAry.length != dest.length){
            return false;
        }
        String sonStr = new String(sonAry);
        if(destStr.equals(sonStr)){
            return true;
        }
        return false;
    }

    public static String bytes2String(byte[] src, int start){
        if(src == null ){
            return "";
        }
        if(src.length == 0){
            return "";
        }
        if(start >= src.length){
            return "";
        }

        int len = 0;
        byte temp = src[start];
        while(temp != 0){
            len++;
            temp = src[start+len];
        }

        byte[] sonAry = bytesSplite(src, start, len);
        if(sonAry == null || sonAry.length == 0){
            return "";
        }
        String sonStr = new String(sonAry);
        return sonStr;
    }
    public static void main(String[] args) {
        // 基本变量类型的使用
        info();
        // 数组的使用
        infoArray();
    }
}
