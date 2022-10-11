//
// Created by ayuan on 2022/8/7.
//

#ifndef NATIVETEST_JNIUTILS_H
#define NATIVETEST_JNIUTILS_H

#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>
#include <stdarg.h>
#include <unordered_map>
#include <unistd.h>

#ifndef JNI_LOGD
#define JNI_LOGD(...) (__android_log_print(ANDROID_LOG_DEBUG, "ayuan-log", __VA_ARGS__))
#endif

std::unordered_map<std::string, jmethodID> jni_methods;

namespace  JniUtils {

    // 数据转换
    // 指针与long的转换
    template <typename PTR_T, typename std::enable_if_t<std::is_pointer<PTR_T>::value, int> = 0> PTR_T jlong_to_ptr (jlong ptr) {
        return reinterpret_cast<PTR_T>(static_cast<std::uintptr_t>(ptr));
    }
    template <typename PTR_T, typename std::enable_if_t<std::is_pointer<PTR_T>::value, int> = 0> jlong ptr_to_jlong (PTR_T ptr) {
        return static_cast<jlong>(reinterpret_cast<std::uintptr_t>(ptr));
    }
    void utf16_to_utf8 (const jchar *p, jsize len, char *res) {
        for (jsize i = 0; i < len; i++) {
            unsigned int cur = p[i];
            // TODO conversion unsigned int -> signed char is implementation defined
            if (cur <= 0x7f) {
                *res++ = static_cast<char>(cur);
            } else if (cur <= 0x7ff) {
                *res++ = static_cast<char>(0xc0 | (cur >> 6));
                *res++ = static_cast<char>(0x80 | (cur & 0x3f));
            } else if ((cur & 0xF800) != 0xD800) {
                *res++ = static_cast<char>(0xe0 | (cur >> 12));
                *res++ = static_cast<char>(0x80 | ((cur >> 6) & 0x3f));
                *res++ = static_cast<char>(0x80 | (cur & 0x3f));
            } else {
                // correctness is already checked
                unsigned int next = p[++i];
                unsigned int val = ((cur - 0xD800) << 10) + next - 0xDC00 + 0x10000;

                *res++ = static_cast<char>(0xf0 | (val >> 18));
                *res++ = static_cast<char>(0x80 | ((val >> 12) & 0x3f));
                *res++ = static_cast<char>(0x80 | ((val >> 6) & 0x3f));
                *res++ = static_cast<char>(0x80 | (val & 0x3f));
            }
        }
    }
    jsize get_utf16_from_utf8_length(const char *p, size_t len, jsize *surrogates) {
        // UTF-8 correctness is supposed
        jsize result = 0;
        for (size_t i = 0; i < len; i++) {
            result += ((p[i] & 0xc0) != 0x80);
            *surrogates += ((p[i] & 0xf8) == 0xf0);
        }
        return result;
    }
    void utf8_to_utf16(const char *p, size_t len, jchar *res) {
        // UTF-8 correctness is supposed
        for (size_t i = 0; i < len;) {
            unsigned int a = static_cast<unsigned char>(p[i++]);
            if (a >= 0x80) {
                unsigned int b = static_cast<unsigned char>(p[i++]);
                if (a >= 0xe0) {
                    unsigned int c = static_cast<unsigned char>(p[i++]);
                    if (a >= 0xf0) {
                        unsigned int d = static_cast<unsigned char>(p[i++]);
                        unsigned int val = ((a & 0x07) << 18) + ((b & 0x3f) << 12) + ((c & 0x3f) << 6) + (d & 0x3f) - 0x10000;
                        *res++ = static_cast<jchar>(0xD800 + (val >> 10));
                        *res++ = static_cast<jchar>(0xDC00 + (val & 0x3ff));
                    } else {
                        *res++ = static_cast<jchar>(((a & 0x0f) << 12) + ((b & 0x3f) << 6) + (c & 0x3f));
                    }
                } else {
                    *res++ = static_cast<jchar>(((a & 0x1f) << 6) + (b & 0x3f));
                }
            } else {
                *res++ = static_cast<jchar>(a);
            }
        }
    }
    size_t get_utf8_from_utf16_length(const jchar *p, jsize len) {
        size_t result = 0;
        for (jsize i = 0; i < len; i++) {
            unsigned int cur = p[i];
            if ((cur & 0xF800) == 0xD800) {
                if (i < len) {
                    unsigned int next = p[++i];
                    if ((next & 0xFC00) == 0xDC00 && (cur & 0x400) == 0) {
                        result += 4;
                        continue;
                    }
                }

                // TODO wrong UTF-16, it is possible
                return 0;
            }
            result += 1 + (cur >= 0x80) + (cur >= 0x800);
        }
        return result;
    }
    // jstring 与 std::string的转换
    // jstring(utf-16, 所有字符都是2字节) --> std::string(utf-8, 变长)
    std::string jstring2cppstring (JNIEnv *env, jstring s) {
        if (!s) {
            return "";
        }
        jsize s_len = env->GetStringLength(s);
        const jchar *p = env->GetStringChars(s, nullptr);
        if (p == nullptr) {
            // parse_error = true;
            return std::string();
        }
        size_t len = get_utf8_from_utf16_length(p, s_len);
        std::string res(len, '\0');
        if (len) {
            utf16_to_utf8(p, s_len, &res[0]);
        }
        env->ReleaseStringChars(s, p);
        return res;
    }
    // std::string(utf-8, 变长) --> jstring(utf-16, 所有字符都是2字节)
    jstring cppstring2jstring (JNIEnv *env, const std::string &s) {
        jsize surrogates = 0;
        jsize unicode_len = get_utf16_from_utf8_length(s.c_str(), s.size(), &surrogates);
        if (surrogates == 0) {
            // TODO '\0'
            return env->NewStringUTF(s.c_str());
        }
        jsize result_len = surrogates + unicode_len;
        if (result_len <= 256) {
            jchar result[256];
            utf8_to_utf16(s.c_str(), s.size(), result);
            return env->NewString(result, result_len);
        }

        auto result = std::make_unique<jchar[]>(result_len);
        utf8_to_utf16(s.c_str(), s.size(), result.get());
        return env->NewString(result.get(), result_len);
    }
    // GetStringChars: jstring--> UTF-16 jchar*, 释放使用ReleaseStringChars
    // GetStringUTFChars: jstring--> UTF-8 jchar*, 释放使用ReleaseStringUTFChars
    // NewString: jchar* --> jstring
    // jstring(utf-16, 所有字符都是2字节) --> std::wstring(utf-16)
    std::wstring jstring2cppwstring (JNIEnv *env, jstring s) {
        const jchar* jstr = env->GetStringChars(s, NULL);
        std::wstring wstr((const wchar_t *)jstr);
        env->ReleaseStringChars(s, jstr);
        return wstr;
    }
    // 返回 Java 字符串的长度(Unicode 字符数)
    jsize getJstringLength(JNIEnv* env, jstring string){
        return env->GetStringLength(string);
    }
    // 返回Java 字符串的大小 (以字节为单位返回字符串的 UTF-8 长度)
    jsize getJstringSize(JNIEnv* env, jstring string){
        return env->GetStringUTFLength(string);
    }



    // 获取类类型
    /**
     * FindClass 加载本地定义的类  如 "java/lang/String"
     * @param env
     * @param class_name 类名, 使用/分割， 如 "java/lang/String", "[I"
     * @return 类类型
     */
    jclass getClassByName(JNIEnv* env, const char* class_name){
        jclass clazz = env->FindClass(class_name);
        if (clazz == nullptr) {
            return nullptr;
        }
        return clazz;
    }
    /**
     * GetObjectClass 通过对象获取类类型
     * @param env
     * @param obj 对象实例, 如jobject stu; jclass jstuClass = env->GetObjectClass(stu);
     * @return
     */
    jclass getClassByObj(JNIEnv* env, jobject obj){
        jclass clazz = env->GetObjectClass(obj);
        if (clazz == nullptr) {
            return nullptr;
        }
        return clazz;
    }
    // DefineClass  从原始类数据的缓冲区中加载类
    // GetSuperClass(jclass obj)

    // 方法相关
    // GetMethodID , 其中函数名"init", 函数类型"()V" 代表构造函数
    // Call<type>Method
    // GetStaticMethodID
    // CallStatic<type>Method

    // GetMethodID 获取方法ID
    jmethodID getMethodID(JNIEnv *env,  std::string &class_path, const std::string& name, const std::string &sig) {
        //2.1 获取类类型, 由于在MainActivity中， 直接获取即可
        jclass jclass1 = JniUtils::getClassByName(env, class_path.c_str());
        //2.2 获取方法ID， 参数：类类型， 方法名， 方法原型
        jmethodID jId = env->GetMethodID(jclass1, name.c_str(), sig.c_str());
        return jId;
    }
    jmethodID getMethodID(JNIEnv *env,  jclass jclass1, const std::string& name, const std::string &sig) {
        //2.2 获取方法ID， 参数：类类型， 方法名， 方法原型
        jmethodID jId = env->GetMethodID(jclass1, name.c_str(), sig.c_str());
        return jId;
    }
    // 调用方法
    // 成员方法调用
    // Call<type>Method, 调用boolean function(int a, double b, char c)如 env->CallBooleanMethod(obj, id_function, 10L, 3.4, L'a')
    // 静态方法调用
    // CallStatic<type>Method
    // 多态方法调用
    // CallNonvirtual<type>Method


    // 字段相关
    // GetFieldID
    // Get<Type>Field, jobject参数代表类实例
    // Set<type>Field

    // GetStaticFieldID
    // GetStatic<type>Field, jobject参数代表class类型对象
    // SetStatic<type>Field

    // 数组相关
    // New<Type>Array:  创建类型为Type的数组,
    // jobjectArray
    // jbooleanArray
    // jcharArray
    // jshortArray
    // jintArray
    // jlangArray
    // jdoubleArray

    // 创建新的数组, 如array_new<jintArray>(env, 10)
    template <typename ARRAY_T> inline ARRAY_T array_new (JNIEnv *env, jsize length);
    // 设置值， 如
    template <typename T> inline void array_set (JNIEnv *env, T array, jsize offset, jsize length, const char *data);
    // 转化 j<Type>Array --> j<type>*, 与array_release配对使用, 如array_get<jint, jintArray>
    template <typename T, typename ARRAY_T> inline T * array_get (JNIEnv *env, ARRAY_T array);
    // 释放, 与array_get配对使用,array_release<jint, jintArray>
    template <typename T, typename ARRAY_T> inline void array_release (JNIEnv *env, ARRAY_T array, T *elements);
    // 获取数组长度
    jsize getArrayLength(JNIEnv *env, jarray array) { return env->GetArrayLength( array); }

    // 利用宏定义偏特化
    #define JNI_ARRAY_RELEASE(TYPE,ARRAY) \
    template <> inline void array_release<TYPE,TYPE##Array> (JNIEnv *env, TYPE##Array array, TYPE *elements) { env->Release##ARRAY##ArrayElements(array, elements, JNI_ABORT); }
    #define JNI_ARRAY_GET(TYPE,ARRAY) \
    template <> inline TYPE * array_get<TYPE> (JNIEnv *env, TYPE##Array array) { return env->Get##ARRAY##ArrayElements(array, NULL); }
    #define JNI_ARRAY_NEW(TYPE,ARRAY) \
    template <> inline TYPE##Array array_new<TYPE##Array> (JNIEnv *env, jsize length) { return env->New##ARRAY##Array(length); }
    #define JNI_ARRAY_SET(TYPE,ARRAY) \
    template <> inline void array_set<TYPE##Array> (JNIEnv *env, TYPE##Array array, jsize offset, jsize length, const char *data) { env->Set##ARRAY##ArrayRegion(array, offset, length, (const TYPE *) data); }

    #define JNI_ARRAY(JNI_ARRAY_FUNC) \
    JNI_ARRAY_FUNC(jint, Int); \
    JNI_ARRAY_FUNC(jlong, Long); \
    JNI_ARRAY_FUNC(jbyte, Byte); \
    JNI_ARRAY_FUNC(jfloat, Float); \
    JNI_ARRAY_FUNC(jdouble, Double);

    JNI_ARRAY(JNI_ARRAY_NEW);
    JNI_ARRAY(JNI_ARRAY_SET);
    JNI_ARRAY(JNI_ARRAY_GET);
    JNI_ARRAY(JNI_ARRAY_RELEASE);
    #undef JNI_ARRAY_NEW
    #undef JNI_ARRAY_SET
    #undef JNI_ARRAY_GET
    #undef JNI_ARRAY_RELEASE
    #undef JNI_ARRAY

    // 反调试相关
    int getnumberfor_str(char* str){
        if(str == NULL){
            return -1;
        }
        char result[20];
        int count = 0;
        while(*str != '\0'){
            if(*str >= 48 && *str <= 57){
                result[count] = *str;
                count++;
            }
            str++;
        }
        int val = atoi(result);
        return val;
    }

    void thread_fuction() {
        int pid = getpid();
        char file_name[20] = {'\0'};
        sprintf(file_name, "/proc/%d/status",pid);
        char linestr[256];
        int i=0, traceid;
        FILE *fp;
        while(1){
            i = 0;
            fp = fopen(file_name,"r");
            if(fp == NULL){
                break;
            }
            while(!feof(fp)){
                fgets(linestr, 256, fp);
                // tracerPid 在第六行
                if(i == 5){
                    traceid = getnumberfor_str(linestr);
                    if(traceid > 0){
                        JNI_LOGD("I was be traced...trace pid:%d",traceid);
                        exit(0);
                    }
                    break;
                }
                i++;
            }
            fclose(fp);
            sleep(5);
        }
    }
    pthread_t t_id;
    void create_thread_check_traceid(){
        int err = pthread_create(&t_id, NULL, reinterpret_cast<void *(*)(void *)>(thread_fuction), NULL);
        if(err != 0 ) {
            JNI_LOGD("create thread fail: %s\n",strerror(err));
        }
    }
    /**
 * 获取 Dalvik_dalvik_system_DexFile_openDexFile_bytearray 函数地址
 * @param methodName, 函数名, 如 openDexFile
 * @return 函数地址
 */
    void* GetFunAddr(char* modulename, char* methodName){
        // 1. 获取模块及地址
        void *handle = dlopen(modulename, RTLD_LAZY);
        JNI_LOGD("模块基址: %p", handle);
        // 2. 获取数组 dvm_dalvik_system_DexFile 对应的导出符号
        void* jniNativeMethod = (JNINativeMethod *)(dlsym(handle, "dvm_dalvik_system_DexFile"));
        JNI_LOGD("%s:%s 符号基址: %p",modulename, methodName, jniNativeMethod);
        // 3. 卸载模块
        dlclose(handle);
        // 4. 返回函数地址
        return jniNativeMethod;
    }

    // 测试案例
    void info(JNIEnv* env, jobject thiz){
        // 获取类类型
        JNI_LOGD("获取类类型: ");
        jclass cls =JniUtils::getClassByName(env, "java/lang/String");
        JNI_LOGD("调用方法: ");
        //1 获取类类型, 由于在MainActivity中， 直接获取即可
        jclass jclass1 = env->GetObjectClass(thiz);
        //2 获取方法ID， 参数：类类型， 方法名， 方法原型
        //jmethodID jId = env->GetMethodID(jclass1, "showText", "()V");
        //3 调用方法
        // CallVoidMethod(类类型,方法ID,参数类型列表): 调用返回为void的函数
        //env->CallVoidMethod(thiz, jId);
        JNI_LOGD("修改字段: ");
        //1 获取类类型
        jclass jclass2 = env->GetObjectClass(thiz);
        //2 获取方法ID
        //jfieldID jId2 = env->GetFieldID(jclass1, "msg", "Ljava/lang/String;");
        //3 修改字段，SetObjectField用于修改引用类型字段
        //env->SetObjectField(thiz, jId2, env->NewStringUTF("success"));
        JNI_LOGD("数组操作: ");
        jintArray jint_array = JniUtils::array_new<jintArray>(env, 10);
        // 获取数组长度
        int nArrLength = JniUtils::getArrayLength(env, jint_array);
        // jintArray --> jint*
        jint* int_array =  JniUtils::array_get<jint, jintArray>(env, jint_array);
        // 遍历数组
        for(jsize n = 0; n < nArrLength; n++){
            int_array[n] = n*2;
            JNI_LOGD("%d ", int_array[n]);
        }
        std::cout<<std::endl;
        // 释放
        JniUtils::array_release<jint, jintArray>(env, jint_array, int_array);
    }
};


#endif //NATIVETEST_JNIUTILS_H
