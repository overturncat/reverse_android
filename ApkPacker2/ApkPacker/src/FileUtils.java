import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {
    /*
    * 输入： /home/ayuan/demo.apk
    * 输出： /home/ayuan/demo
    */
    public static String getPathNoEx(String filepath){
        if((filepath != null) && (filepath.length() > 0)){
            int dot = filepath.lastIndexOf('.');
            if((dot > -1) && (dot < (filepath.length()))){
                return filepath.substring(0, dot);
            }
        }
        return filepath;
    }
    /*
     * 输入： /home/ayuan/demo.apk
     * 输出： apk
     */
    public static String getPathExtensionName(String filepath){
        if((filepath != null) && (filepath.length() > 0)){
            int dot = filepath.lastIndexOf('.');
            if((dot > -1) && (dot < (filepath.length()))){
                return filepath.substring(dot+1);
            }
        }
        return filepath;
    }
    /* 获取工作路径
    * 输出： 当前工作路径
    */
    public static String getWorkpath(){
        return System.getProperty("user.dir");
    }
    /*
    输入： 文件夹路径=/home/ayuan/demo/smali
    输出： 无， 递归删除文件夹及文件
     */
    public static void deleteFolder(String path){
        File file = new File(path);
        deleteFile(file);
    }

    public static void deleteFile(File file) {
        if(file.exists()){
            if(file.isFile()){
                // 如果是文件，直接删除
                file.delete();
            }else{
                // 文件夹， 先递归删除文件
                File[] files = file.listFiles();
                for(File item: files){
                    deleteFile(item);
                }
                // 删除文件夹
                file.delete();
            }
        }else{
            System.out.println("指定的文件路径不存在");
        }
    }
    /*
    输入： 源文件夹=/home/ayuan/smali， 目标文件夹=/home/ayuan/demo/smali
    输出： 无， 递归复制源文件夹到目标文件夹
     */
    public static void copyFolder(String src, String target){
        // 创建目标文件夹
        (new File(target)).mkdirs();
        // 获取当前目录下的所有文件名
        String[] filesname = (new File(src)).list();
        File file = null;
        for(String filename: filesname){
            // 拼接节点名
            if(src.endsWith(File.separator)){
                file = new File(src + filename);
            }else {
                file = new File(src + File.separator+ filename);
            }
            if(file.isFile()){
                // 如果是文件， 则直接复制
                copyFile(file.getAbsolutePath(), target + File.separator+ file.getName());
            }else{
                // 目录递归
                copyFolder(src + File.separator+filename, target+File.separator + filename);
            }
        }

    }
    private static void copyFile(String src, String target){
        try {
            FileInputStream input = new FileInputStream(src);
            FileOutputStream output = new FileOutputStream(target);
            byte[] bytes = new byte[1024];
            int len;
            while((len= input.read(bytes)) != -1){
                output.write(bytes, 0, len);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
