import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    /*输入： apk路径=/home/ayuan/demo.apk， 要解压的文件名=classes.dex， 目的文件夹=/home/ayuan/demo/assets, 解压后的文件名=src.dex
     * 输出： 无，解压特定文件到指定文件夹*/
    public static void copyFileFromZip(String zipPath, String filename, String newPath, String newFilename){
        try {
            // 创建Zip输入流
            ZipFile zf = new ZipFile(zipPath);
            InputStream in = new BufferedInputStream(new FileInputStream(zipPath));
            ZipInputStream zin = new ZipInputStream(in);
            ZipEntry ze;
            // 遍历Zip文件
            while((ze = zin.getNextEntry())!=null){
                if(ze.isDirectory()){
                    // 只检测根目录下的文件，直接跳过
                    continue;
                }else{
                    // 检测文件名
                    if(!ze.getName().equals(filename)){
                        continue;
                    }
                    // 检测目标文件夹是否存在，不存在就创建
                    File file = new File(newPath);
                    if(!file.exists()) file.mkdir();
                    // 循环读取写入
                    FileOutputStream outputStream = new FileOutputStream(newPath + File.separator + newFilename);
                    InputStream inputStream = zf.getInputStream(ze);
                    int len = 0;
                    byte[] bytes = new byte[1024];
                    while((len = inputStream.read(bytes))!=-1){
                        outputStream.write(bytes, 0, len);
                    }
                    outputStream.close();
                    break;
                }
            }
            zin.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
