package com.example.dummydex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ayuan-log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: MainActivity启动");
        Log.d(TAG, getClassLoader().toString());
    }

    public void onClick(View view) {
        loadDexFromMem();
    }

    // 内存加载Dex
    private void loadDexFromMem() {
        Log.d(TAG, "loadDex: 开始加载");
        //1. 模拟内存加载，从自定义资源（assets）dex中读取字节内容
        Log.d(TAG, "loadDex: 模拟内存加载，从自定义资源（assets）dex中读取字节内容");
        byte[] bytes = FileUtils.readAssetstFileToBytes(this, "Main2Activity.dex");
        //2. 创建一个加载了dex字节的Dex类加载器, 可以反射获取其中的类和函数
        Log.d(TAG, "loadDex: 创建一个加载了dex字节的Dex类加载器, 可以反射获取其中的类和函数");
        MyDexClassLoader dexClassLoader = getLoader(bytes);
        //3. 替换DexPathClassLoader（通过修改mClassLoader变量实现）, 这样在StartActivity的时候就可以所以到从src.dex中加载的类
        Log.d(TAG, "loadDex: 替换ClassLoader, 将src.dex中的类加载进来");
        replaceClassLoader(dexClassLoader);
        //4. 手动启动Activity, 加壳的时候这部分由MainActivity自动完成
        invokeActivity(
                MainActivity.this,
                dexClassLoader,         // classLoader
                "com.example.dummydex.Main2Activity" //类名
        );
    }

    /**
     *
     * @param srcActivityContext 源Activity的Context, 如MainActivity.this
     * @param dexClassLoader  Activity所属的ClassLoader,
     * @param targetActivityName 目标Activity的name ,如com.example.myapplication.Main2Activity
     */
    private void invokeActivity( Context srcActivityContext,
                                 MyDexClassLoader dexClassLoader,
                                 String targetActivityName ) {
        try{
            // 类类型
            Class clazz = dexClassLoader.loadClass(targetActivityName);
            Intent intent  = new Intent(srcActivityContext, clazz);
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    // 通过修改mClassLoader变量来修改ClassLoader,
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
    private MyDexClassLoader getLoader(byte[] bytes) {
        MyDexClassLoader dexClassLoader = new MyDexClassLoader(
                bytes,                      // dex字节内容
                getPackageCodePath(),       // 要加载的Dex文件路径, null会报错,所以填apk地址
                getCacheDir().toString(),   // 优化之后的文件路径
                null,       // native 库路径， 由于没有使用到native，null即可
                getClassLoader()        // 父ClassLoader
        );
        Log.d(TAG, "getLoader: "+dexClassLoader);
        return dexClassLoader;
    }
}