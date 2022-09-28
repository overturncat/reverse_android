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

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_myapplication_Main2Activity_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from C++";
    // 把C字符串转换为Java字符串
    JNI_LOGD("so 节点成功启动");
    return env->NewStringUTF(hello.c_str());
}