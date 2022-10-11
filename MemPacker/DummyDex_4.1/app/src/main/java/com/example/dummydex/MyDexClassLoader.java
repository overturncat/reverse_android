package com.example.dummydex;

import Utils.ReflectUtils;
import dalvik.system.DexClassLoader;

// 1. 创建一个DexClassLoader的子类
public class MyDexClassLoader extends DexClassLoader {
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
        mCookie = initCookie(bytes);
    }

    // 反射调用openDexFile, 获取mCookie
    private int initCookie(byte[] bytes) {
        /*
        类名： dalvik.system.DexFile
        函数名： openDexFile
        参数列表: byte[]
         */
        Class<?> cls = ReflectUtils.getCls("dalvik.system.DexFile");
        String methodName = "openDexFile";
        Class[] paramsType = new Class[]{byte[].class};
        mCookie = (int)ReflectUtils.invokeStaticMethod(cls, methodName, paramsType, bytes);
        return mCookie;
    }

    // 2. 重写loadClass, 反射调用Class defineClass(...),
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        /*
        类名： dalvik.system.DexFile
        函数名： defineClass
        参数列表: String , java.lang.ClassLoader, int
         */
        Class<?> cls = ReflectUtils.getCls("dalvik.system.DexFile");
        String methodName = "defineClass";
        Class[] paramsType = new Class[]{String.class, ClassLoader.class, int.class};
        Class clazz = null;
        clazz = (Class)ReflectUtils.invokeStaticMethod(cls, methodName, paramsType,name, mParentClassLoader, mCookie);
        return clazz;
    }
}