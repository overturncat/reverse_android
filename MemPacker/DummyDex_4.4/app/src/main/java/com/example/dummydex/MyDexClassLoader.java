package com.example.dummydex;

import android.util.Log;

import dalvik.system.DexClassLoader;

// 1. 创建一个DexClassLoader的子类
public class MyDexClassLoader extends DexClassLoader {
    // Used to load the 'memclassloader44' library on application startup.

    static {
        System.loadLibrary("dummydex");
    }

    private static final String TAG = "ayuan-log";
    public native int getCookieFromJNI(byte[] bytes, int length);
    int mCookie;
    ClassLoader mParentClassLoader;
    // 默认继承的构造函数
    public MyDexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }
    // 2. 重写构造函数，反射调用int openDexFile(byte[]), 获取mCookie
    public MyDexClassLoader(byte[] bytes, String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        mParentClassLoader = parent;
        mCookie = initCookieOnAndroid44(bytes);
    }

    // 反射调用openDexFile, 获取mCookie
    private int initCookieOnAndroid41(byte[] bytes) {
        // 类名： dalvik.system.DexFile
        // 方法原型：native private static int openDexFile(byte[] fileContents);
        Class<?> cls = ReflectUtils.getCls("dalvik.system.DexFile");
        String methodName = "openDexFile";
        Class[] paramsType = new Class[]{byte[].class};
        mCookie = (int)ReflectUtils.invokeStaticMethod(cls, methodName, paramsType, bytes);
        return mCookie;
    }
    // 反射调用Native的openDexFile, 获取mCookie
    private int initCookieOnAndroid44(byte[] bytes) {
        int cookie = 0;
        cookie = getCookieFromJNI(bytes, bytes.length);
        return cookie;
    }
    // 3. 重写loadClass, 反射调用Class defineClass(...),
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        // return super.loadClass(name, resolve);
        // 类: dalvik.system.DexFile
        // 方法原型：private native static Class defineClass(String name, ClassLoader loader, int cookie);
        Class<?> cls = ReflectUtils.getCls("dalvik.system.DexFile");
        String methodName = "defineClassNative";
        Class[] paramsType = new Class[]{String.class, ClassLoader.class, int.class};
        Class clazz = null;
        clazz = (Class)ReflectUtils.invokeStaticMethod(cls, methodName, paramsType,name, mParentClassLoader, mCookie);
        Log.d(TAG, "loadClass: 重载方法获取的Class: "+ clazz);
        return clazz;
    }
}
