#include <jni.h>
#include <string>
#include <dlfcn.h>
#include <android/log.h>
#include "JniUtils.h"

// 函数声明
int MyOpenDexFile(jbyte *bytes, jint length);
void* GetOpenDexFileAddr(char* methodName, char* sig);
#define LOGD(a,b)  __android_log_print(ANDROID_LOG_DEBUG, "ayuan-log", a,b)
// 函数指针
typedef uint8_t             u1;
typedef uint16_t            u2;
typedef uint32_t            u4;
typedef uint64_t            u8;
typedef int8_t              s1;
typedef int16_t             s2;
typedef int32_t             s4;
typedef int64_t             s8;

struct Object {
    /* ptr to class object */
    void*    clazz;
    u4              lock;
};
struct ArrayObject : Object {
    u4              length;
    u8              contents[1];
};
union JValue {
    u1      z;
    s1      b;
    u2      c;
    s2      s;
    s4      i;
    s8      j;
    float   f;
    double  d;
    Object* l;
};

typedef void (*OPEN_DEX_FILE)( const u4* args, JValue* pResult);
// 全局变量
OPEN_DEX_FILE g_openDexFile;


/**
* 获取 Dalvik_dalvik_system_DexFile_openDexFile_bytearray 函数地址
* @param methodName, 函数名, 如 openDexFile
* @param sig 函数签名用于防止重名函数， 如 ([B)I
* @return 函数地址
*/
void* GetOpenDexFileAddr(char* methodName, char* sig){
    // 1. 获取数组 dvm_dalvik_system_DexFile 对应的导出符号, JNINativeMethod是每个数组项的原型
    JNINativeMethod* jniNativeMethod = (JNINativeMethod *)JniUtils::GetFunAddr("libdvm.so", "dvm_dalvik_system_DexFile");
    JNI_LOGD("数组基址: %p", jniNativeMethod);
    // 2. 遍历数组， 获得函数 Dalvik_dalvik_system_DexFile_openDexFile_bytearray 地址
    int i=0;
    JNINativeMethod * nativeMethod = NULL;
    do{
        nativeMethod = (jniNativeMethod + i++);
        JNI_LOGD("函数名称: %s", nativeMethod->name);
        if(!strcmp(nativeMethod->name, methodName) && !strcmp(nativeMethod->signature, sig)){
            break;
        }
    }while(nativeMethod->name != NULL);
    // 5. 返回函数地址
    return nativeMethod->fnPtr;
}


// 1. JNI_OnLoad中获取并保存系统中的OpenDexFile函数的地址, 并且构造函数指针， 为此需要函数原型的支持
extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved){
    // 1. get env
    JNIEnv* env;
    jint jret = vm->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (jret != JNI_OK){
        return JNI_ERR;
    }
    // 获取函数地址
    g_openDexFile = (OPEN_DEX_FILE)GetOpenDexFileAddr("openDexFile", "([B)I");
    LOGD("openDexFile函数地址: %p", g_openDexFile);
    return JNI_VERSION_1_6;
}
// 2. JNI函数调用OpenDexFile函数，参数和返回值需要的数据类型从源码中提取, 返回cookie
extern "C" JNIEXPORT jint JNICALL
Java_com_example_dummydex_MyDexClassLoader_getCookieFromJNI(
        JNIEnv *env, jobject thiz,
        jbyteArray bytes, jint length) {
    int cookie = 0;
    jbyte* cBytes = env->GetByteArrayElements(bytes, NULL);
    // 调用Native层函数， 返回cookie
    cookie = MyOpenDexFile(cBytes, length);
    env->ReleaseByteArrayElements(bytes, cBytes, 0);
    return cookie;
}
int MyOpenDexFile(jbyte *bytes, jint length) {
    // 函数原型:static void (const u4* args, JValue* pResult)
    // 1. 构造参数args
    u4* args = nullptr;
    // 其中分析可知， ArrayObject* fileContentsObj = (ArrayObject*) args[0];
    ArrayObject* fileContentsObj = (ArrayObject*)malloc(sizeof(ArrayObject)+length);
    fileContentsObj->length = length;
    memcpy(fileContentsObj->contents, bytes, length);
    //    u4 args[] = {*(u4*)&fileContentsObj};
    args = (u4*)&fileContentsObj;
    LOGD("地址1: %p", fileContentsObj);
    LOGD("地址2: %p", &fileContentsObj);
    // 2. 构造参数pResult
    JValue pResult = {};
    // 3. 调用函数
    g_openDexFile(args,&pResult);
    // 4. 获取返回值
    int cookie = 0;
    cookie = pResult.i;
    LOGD("cookie 返回值: %p", cookie);
    return cookie;
}
