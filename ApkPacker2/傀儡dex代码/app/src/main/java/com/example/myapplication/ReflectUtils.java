package com.example.myapplication;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射调用工具类
 * public void reflectMethod(){
 *     Context c = null;
 *     try {
 *     	//获取第三方包的上下文
 * 		c = createPackageContext(PACKAGE_NAME, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
 * 		//获取该包中的相应的类
 * 		Class<?> clazz = Class.forName(CLASS_NAME,true, c.getClassLoader());
 * 		//创建单例对象
 * 		Object Obj = clazz.newInstance();
 * 		//获取该类中的方法，传入参数并执行
 * 		ReflectUtils.invokeMethod(Obj,METHOD_NAME,new Class[]{Context.class,int.class, boolean.class},c,0,false);
 * 		//获取该类中的某个变量
 * 		Field debug = clazz.getDeclaredField("DEBUG");
 * 		System.out.println("===========:"+debug.getBoolean("DEBUG"));
 *        } catch (PackageManager.NameNotFoundException e) {
 * 	        e.printStackTrace();
 *    } catch (Exception e) {
 * 	        e.printStackTrace();
 *    }
 * }
 */

public class ReflectUtils {

    /**
     * 根据类名获取cls
     * @param className 类名， 如 java.lang.String
     * @return
     */
    public static Class<?> getCls(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据类名获取cls
     * @param obj 实例， 如String str,getCls(str)
     * @return
     */
    public static Class<?> getCls(Object obj) {
        try {
            return obj.getClass();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 打印类信息
     * @param cls 类类型， 如 java.lang.String
     * @return
     */
    public static void getClsInfo(Class cls) {
        System.out.println("Class name: " + cls.getName());
        System.out.println("Simple name: " + cls.getSimpleName());
        if (cls.getPackage() != null) {
            System.out.println("Package name: " + cls.getPackage().getName());
        }
        System.out.println("is interface: " + cls.isInterface());
        System.out.println("is enum: " + cls.isEnum());
        System.out.println("is array: " + cls.isArray());
        System.out.println("is primitive: " + cls.isPrimitive());
        System.out.println("---------------------------------------------------");
    }
    /**
     * 创建类对象
     *
     * @param className   类名
     * @param params      参数名称
     * @param paramsValue 参数
     * @return 返回值, 可为空
     */
    public static Object newInstance(String className, Class<?>[] params, Object[] paramsValue)
    {
        Object rlt = null;
        try {
            Class<?> cls = getCls(className);
            if (cls != null) {
                Constructor<?> cons = cls.getDeclaredConstructor(params);
                rlt = cons.newInstance(paramsValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rlt;
    }

    /**
     * 反射调用静态方法,会从父类中查找
     *
     * @param methodName     反射方法名
     * @param parameterTypes 方法参数
     * @param args           注入的参数
     * @return 返回结果, 可以null
     */
    public static Object invokeStaticMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes, Object... args)
    {
        Object rlt = null;
        try
        {
            Method method = getMethods(cls, methodName, parameterTypes);

            if (method != null)
            {
                rlt = method.invoke(null, args);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return rlt;
    }


    /** 调用类静态方法 */
    public static Object invokeStaticMethod(String className, String methodName, Object...param)
    {
        Object result = null;
        Method methodObj = null;
        Class<?> classType = null;
        try
        {
            classType = Class.forName(className);
            methodObj = classType.getMethod(methodName); 	// 获取类中的函数方法
            methodObj.setAccessible(true);
            result = methodObj.invoke(null, param);					// 反射调用函数方法classObj.methodName()
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if (classType == null) throw new IllegalStateException("Could not find Class " + className);
        if (methodObj == null) throw new IllegalStateException("Could not find Static method " + methodName + "() in the class " + classType.getName());

        return result;
    }
    /**
     * 反射执行类的静态函数(public)
     * @param class_name	类名
     * @param method_name	函数名
     * @param pareTyple		函数的参数类型
     * @param pareVaules	调用函数时传入的参数
     * @return
     */
    public static  Object invokeStaticMethod(String class_name, String method_name, Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getMethod(method_name,pareTyple);
            return method.invoke(null, pareVaules);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    /**
     * 反射获取方法值,会从父类中查找
     *
     * @param obj            反射对象
     * @param methodName     反射方法名
     * @param parameterTypes 方法参数
     * @param args           注入的参数
     * @return 返回结果, 可以null
     */
    public static Object invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object... args)
    {

        Object rlt = null;
        try
        {
            Method method = getMethods(obj.getClass(), methodName, parameterTypes);
            if (method != null)
            {
                rlt = method.invoke(obj, args);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return rlt;
    }
    /**
     * 反射执行类的函数（public）
     * @param class_name
     * @param method_name
     * @param obj
     * @param pareTyple
     * @param pareVaules
     * @return
     */
    public static  Object invokeMethod(String class_name, String method_name, Object obj ,Class[] pareTyple, Object[] pareVaules){

        try {
            Class obj_class = Class.forName(class_name);
            Method method = obj_class.getMethod(method_name,pareTyple);
            return method.invoke(obj, pareVaules);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 设置类的静态属性（包括私有和保护）
     * @param class_name
     * @param filedName
     * @param filedVaule
     */
    public static void setStaticOjbect(String class_name, String filedName, Object filedVaule){
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(null, filedVaule);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 为反射类变量赋值,包含父类
     *
     * @param obj       反射对象
     * @param filedName 变量名
     * @param value     变量值
     */
    public static void setFieldValue(Object obj, String filedName, Object value)
    {
        try
        {
            List<Field> fieldList = getFields(obj.getClass());

            if (fieldList != null)
            {
                for (Field item : fieldList)
                {
                    if (item.getName().equals(filedName))
                    {
                        item.set(obj, value);
                    }
                }

            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 设置类的属性（包括私有和保护）
     * @param classname
     * @param filedName
     * @param obj
     * @param filedVaule
     */
    public static void setFieldOjbect(String classname, String filedName, Object obj, Object filedVaule){
        try {
            Class obj_class = Class.forName(classname);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            field.set(obj, filedVaule);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 反射得到类的静态属性（包括私有和保护）
     * @param class_name
     * @param filedName
     * @return
     */
    public static Object getStaticFieldOjbect(String class_name, String filedName){

        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(null);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
    /**
     * 为反射类变量赋值,包含父类
     *
     * @param
     * @param filedName 变量名
     * @param value     变量值
     */
    public static void setStaticFieldValue(Class<?> cls, String filedName, Object value)
    {
        try
        {
            List<Field> fieldList = getFields(cls);

            if (fieldList != null)
            {
                for (Field item : fieldList)
                {
                    if (item.getName().equals(filedName))
                    {
                        item.set(null, value);
                    }
                }

            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 反射静态变量的值,包含父类
     *
     * @param clsName   类名
     * @param filedName 变量名
     * @return 变量值, 不存在时为空
     */
    public static Object getStaticFieldValue(String clsName, ClassLoader classLoader, String filedName)
    {
        try
        {
            Class<?> cls = classLoader.loadClass(clsName);
            List<Field> mlist = getFields(cls);
            if (mlist != null)
            {
                for (Field item : mlist)
                {
                    if (item.getName().equals(filedName))
                    {
                        return item.get(null);
                    }

                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 反射得到类的属性（包括私有和保护）
     * @param class_name
     * @param obj
     * @param filedName
     * @return
     */
    public static Object getFieldOjbect(String class_name,Object obj, String filedName){
        try {
            Class obj_class = Class.forName(class_name);
            Field field = obj_class.getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 获取反射对象的变量值,包含父类的变量
     * @param obj
     * @param filedName
     * @return
     */
    public static Object getFieldValue(Object obj, String filedName)
    {

        try
        {
            List<Field> fieldList = getFields(obj.getClass());

            if (fieldList != null)
            {
                for (Field item : fieldList)
                {
                    if (item.getName().equals(filedName))
                    {
                        return item.get(obj);
                    }
                }

            }

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return null;
    }

    /**
     * 获取反射对象的变量值,处理了变量名相同的情况
     *
     * @param obj
     * @param filedName
     * @return
     */
    public static Object getFieldValueForFiledClsNm(Object obj, String filedName, String filedClsNm)
    {


        List<Object> mList = new ArrayList<>();
        try
        {
            List<Field> fieldList = ReflectUtils.getFields(obj.getClass());

            if (fieldList != null)
            {
                for (Field item : fieldList)
                {
                    if (item.getName().equals(filedName))
                    {
                        if(item.get(obj)!=null){
                            mList.add(item.get(obj));
                        }

                    }
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();

        }
        for (Object itemObj : mList)
        {

            if (itemObj.getClass().getName().equals(filedClsNm))
            {
                return itemObj;
            }
        }

        return null;

    }

    /**
     * 获取所有的变量名和变量值
     * @param obj
     * @return
     */
    public static List<String> getAllFieldValue(Object obj)
    {


        try
        {
            List<String> rltList = new ArrayList<>();
            List<Field> fieldList = getFields(obj.getClass());
            if (fieldList != null)
            {
                for (Field item : fieldList)
                {

                    Object rlt = item.get(obj);
                    rltList.add(item.getName() + ":" + rlt);
                }


            }

            return rltList;
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取所有的field 包括父类的
     */
    public static List<Field> getFields(Class<?> cls)
    {
        try
        {

            List<Field> fieldList = new ArrayList<>();
            while (cls != null && !cls.getName().toLowerCase().equals("java.lang.object"))
            {
                Field[] fields = cls.getDeclaredFields();
                if (fields != null)
                {
                    for (Field field : fields)
                    {
                        if (field != null)
                        {
                            field.setAccessible(true);
                            fieldList.add(field);
                        }
                    }
                }

                cls = cls.getSuperclass();
            }

            return fieldList;

        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取符合条件的method,子类不存在会从父类中查找
     */
    public static Method getMethods(Class<?> cls, String methodName, Class<?>[] parameterTypes) {
        while (cls != null && !cls.getName().toLowerCase().equals("java.lang.object"))
        {
            try
            {
                Method method = cls.getDeclaredMethod(methodName, parameterTypes);
                if (method != null)
                {
                    method.setAccessible(true);
                    return method;
                }


            } catch (NoSuchMethodException e)
            {
                //                e.printStackTrace();
                cls = cls.getSuperclass();
            }
        }
        return null;
    }
}
