import java.io.File;

public class Main {
    // 要加壳的文件名， 在程序主目录中
    static String fileName = "demo.apk";
    // 傀儡dex的包名.类名
    static String dexPath = "com.example.myapplication.DummyApplication";

    // main 是static, 要调用的函数有必须是static
    public static void main(String[] args) {
        String dir = FileUtils.getWorkpath(); // 工作目录 /home/ayuan/
        String filePath = dir + File.separator + fileName; // apk绝对路径 /home/ayuan/demo.apk
        String projectPath = dir + File.separator + FileUtils.getPathNoEx(fileName); // /home/ayuan/demo apktool反编译路径
        String xmlPath = projectPath+File.separator+"AndroidManifest.xml";   // /home/ayuan/demo/AndroidManifest.xml
        String assetsPath = projectPath+File.separator+"assets";   // /home/ayuan/demo/assets
        String demoSmaliPath = projectPath+File.separator+"smali";   // /home/ayuan/demo/smali
        String dummydexSmaliPath = dir+File.separator+"dummyDexSmali";   // /home/ayuan/dummyDexSmali
        String outPath = dir + File.separator + "out.apk";  // /home/ayuan/out.apk
        String outSignPath = dir + File.separator + "signed.apk";  // /home/ayuan/signed.apk
        // 1. 反编译apk
        CmdUtils.runCMD("java -jar apktool.jar d "+ filePath);
        // 2. 解析AndroidManifest.xml并获取application 节点, 指向 壳dex: DummyApplication
        XMLUtils.modifyApplication(xmlPath, dexPath);
        // 3. 解压classes.dex到 assets/src.dex
        ZipUtils.copyFileFromZip(filePath, "classes.dex", assetsPath, "src.dex");
        // 4. 拷贝傀儡dex的smali 文件到smali目录中
        // 删除原有的 /home/ayuan/demo/smali 文件夹
        FileUtils.deleteFolder(demoSmaliPath);
        // 拷贝傀儡dex的smali 文件到smali目录中
        FileUtils.copyFolder(dummydexSmaliPath, demoSmaliPath);
        // 5. 重打包
        CmdUtils.runCMD("java -jar apktool.jar b "+projectPath+" -o out.apk");
        // 6. 签名
        CmdUtils.runCMD("java -jar signapk.jar testkey.x509.pem testkey.pk8 "+outPath+" "+outSignPath, "sign");
        // 7. 清理
        FileUtils.deleteFolder(projectPath);
        FileUtils.deleteFile(new File(outPath));
    }
}
