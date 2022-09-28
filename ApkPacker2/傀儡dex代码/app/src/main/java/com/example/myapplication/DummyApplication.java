package com.example.myapplication;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import dalvik.system.DexClassLoader;

public class DummyApplication extends Application {
    private static final String TAG = "ayuan-log";
    private static final String src_dex_filename = "src.dex";
    private static Context ApplicationContext;

    // 1. 在attachBaseContext方法中添加加载原dex和替换ClassLoader的代码
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ApplicationContext =base;
        loadDex();
    }

    //2. 在onCreate 方法中加载可能存在的Application节点
    @Override
    public void onCreate() {
        super.onCreate();
        loadApplication();
    }

    private void loadApplication() {
        // 如果源应用配置有Appliction对象，则替换为源应用Applicaiton，以便不影响源程序逻辑。
        String appClassName = null;
        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo(this.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle != null && bundle.containsKey("APPLICATION_CLASS_NAME")) {
                appClassName = bundle.getString("APPLICATION_CLASS_NAME");//className 是配置在xml文件中的。
            } else {
                Log.i("demo", "have no application class name");
                return;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("demo", "error:"+Log.getStackTraceString(e));
            e.printStackTrace();
        }
        //有值的话调用该Applicaiton
        Object currentActivityThread = ReflectUtils.invokeStaticMethod(
                "android.app.ActivityThread", "currentActivityThread",
                new Class[] {}, new Object[] {});
        Object mBoundApplication = ReflectUtils.getFieldOjbect(
                "android.app.ActivityThread", currentActivityThread,
                "mBoundApplication");
        Object loadedApkInfo = ReflectUtils.getFieldOjbect(
                "android.app.ActivityThread$AppBindData",
                mBoundApplication, "info");
        //把当前进程的mApplication 设置成了null
        ReflectUtils.setFieldOjbect("android.app.LoadedApk", "mApplication",
                loadedApkInfo, null);
        Object oldApplication = ReflectUtils.getFieldOjbect(
                "android.app.ActivityThread", currentActivityThread,
                "mInitialApplication");
        //http://www.codeceo.com/article/android-context.html
        ArrayList<Application> mAllApplications = (ArrayList<Application>) ReflectUtils
                .getFieldOjbect("android.app.ActivityThread",
                        currentActivityThread, "mAllApplications");
        mAllApplications.remove(oldApplication);//删除oldApplication

        ApplicationInfo appinfo_In_LoadedApk = (ApplicationInfo) ReflectUtils
                .getFieldOjbect("android.app.LoadedApk", loadedApkInfo,
                        "mApplicationInfo");
        ApplicationInfo appinfo_In_AppBindData = (ApplicationInfo) ReflectUtils
                .getFieldOjbect("android.app.ActivityThread$AppBindData",
                        mBoundApplication, "appInfo");
        appinfo_In_LoadedApk.className = appClassName;
        appinfo_In_AppBindData.className = appClassName;
        Application app = (Application) ReflectUtils.invokeMethod(
                "android.app.LoadedApk", "makeApplication", loadedApkInfo,
                new Class[] { boolean.class, Instrumentation.class },
                new Object[] { false, null });//执行 makeApplication（false,null）
        ReflectUtils.setFieldOjbect("android.app.ActivityThread",
                "mInitialApplication", currentActivityThread, app);


        ArrayMap mProviderMap = (ArrayMap) ReflectUtils.getFieldOjbect(
                "android.app.ActivityThread", currentActivityThread,
                "mProviderMap");
        Iterator it = mProviderMap.values().iterator();
        while (it.hasNext()) {
            Object providerClientRecord = it.next();
            Object localProvider = ReflectUtils.getFieldOjbect(
                    "android.app.ActivityThread$ProviderClientRecord",
                    providerClientRecord, "mLocalProvider");
            ReflectUtils.setFieldOjbect("android.content.ContentProvider",
                    "mContext", localProvider, app);
        }

        Log.i("demo", "app:"+app);

        app.onCreate();
    }

    private void loadDex() {
        Log.d(TAG, "DummyApplication.attachBaseContext: 开始加载");
        //1. 拷贝自定义资源（assets/src.dex）的dex到程序目录下(app/files/)
        String dexPath = copyDex(src_dex_filename);
        //2. 创建一个加载了该Dex文件的Dex类加载器, 可以反射获取其中的类和函数
        DexClassLoader dexClassLoader = getLoader(dexPath);
        //3. 替换PathClassLoader为自定义classLoader, startActivity 底层会从PathClassLoader寻找加载的类
        replaceClassLoader(dexClassLoader);
        Log.d(TAG, "DummyApplication.attachBaseContext: 加载结束");
    }


    private void replaceClassLoader(DexClassLoader dexClassLoader) {
        try {
            //1. private static ActivityThread sCurrentActivityThread = ActivityThread.currentActivityThread()
            // 获取类类型
            Class clzActivityThread = Class.forName("android.app.ActivityThread");
            // 获取静态方法
            Method methodCurrentActivityThread = clzActivityThread.getDeclaredMethod("currentActivityThread");
            // 调用静态方法, 返回静态成员变量
            Object sCurrentActivityThread = methodCurrentActivityThread.invoke(null, new Object[]{});
            //2. private ActivityThread$AppBindData mBoundApplication = sCurrentActivityThread 反射 mBoundApplication
            // 获取类类型
            Class clzAppBinData = Class.forName("android.app.ActivityThread$AppBindData");
            // 获取字段类型
            Field fieldBoundApplication = clzActivityThread.getDeclaredField("mBoundApplication");
            // 获取实例sCurrentActivityThread 对应的字段对象
            fieldBoundApplication.setAccessible(true);
            Object mBoundApplication = fieldBoundApplication.get(sCurrentActivityThread);
            //3. private LoadedApk info = mBoundApplication 反射 info
            // 获取字段类类型
            Class clzLoadedApk = Class.forName("android.app.LoadedApk");
            // 获取字段类型
            Field fieldInfo = clzAppBinData.getDeclaredField("info");
            // 获取实例mBoundApplication 对应的字段对象
            fieldInfo.setAccessible(true);
            Object info = fieldInfo.get(mBoundApplication);
            //4. private ClassLoader mClassLoader = info 反射 mClassLoader
            // 获取类类型
            Class clzClassLoader = Class.forName("java.lang.ClassLoader");
            // 获取字段类型
            Field fieldClassLoader = clzLoadedApk.getDeclaredField("mClassLoader");
            // 获取实例mBoundApplication 对应的字段对象
            fieldClassLoader.setAccessible(true);
            fieldClassLoader.set(info, dexClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 创建一个加载了该Dex文件的Dex类加载器, 可以反射获取其中的类和函数
    private DexClassLoader getLoader(String dexPath) {
        DexClassLoader dexClassLoader = new DexClassLoader(
                dexPath,            // 要加载的Dex文件路径
                getCacheDir().toString(),   // 优化之后的文件路径
                ApplicationContext.getApplicationInfo().nativeLibraryDir,     // native 库路径， 由于没有使用到native，null即可
                getClassLoader()        // 父ClassLoader
        );
        Log.i(TAG, "getLoader: "+dexClassLoader);
        return dexClassLoader;
    }

    // 拷贝assets/文件 到 app/files/ 下
    private String copyDex(String dexName) {
        // 获取assets目录管理器
        AssetManager as = getAssets();
        // 目的地址
        String path = getFilesDir() + File.separator + dexName;
        // /data/user/0/com.example.myapplication/files/Main2Activity.dex
        Log.d(TAG, "copyDex: path: " + path);
        // 将文件拷贝到目的地址
        try {
            // 创建文件流
            FileOutputStream out = new FileOutputStream(path);
            // 打开文件 assets/dexName
            InputStream is = as.open(dexName);
            // 循环读取并写入文件
            byte[] buffer = new byte[1024];
            int len =0;
            while((len = is.read(buffer))!= -1){
                out.write(buffer, 0, len);
            }
            // 关闭文件
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
        return path;
    }
}