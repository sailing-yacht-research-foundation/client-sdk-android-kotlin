//
// Created by Sergij Rylskyj on 08.06.2021.
//

#include <jni.h>
#include <string>
#include <JavaScriptCore/JavaScriptCore.h>

std::string JSStringToStdString(JSStringRef jsString) {
    size_t maxBufferSize = JSStringGetMaximumUTF8CStringSize(jsString);
    char *utf8Buffer = new char[maxBufferSize];
    size_t bytesWritten = JSStringGetUTF8CString(jsString, utf8Buffer, maxBufferSize);
    std::string utf_string = std::string(utf8Buffer, bytesWritten - 1);
    delete[] utf8Buffer;
    return utf_string;
}

JSGlobalContextRef globalContext = nullptr;

extern "C"
JNIEXPORT void JNICALL
Java_com_syrf_location_interfaces_SYRFCore_executeJS(JNIEnv *env, jobject thiz, jstring script) {
    JSContextGroupRef contextGroup = JSContextGroupCreate();
    globalContext = JSGlobalContextCreateInGroup(contextGroup, nullptr);
    const char *cString = env->GetStringUTFChars(script, 0);
    JSStringRef statement = JSStringCreateWithUTF8CString(cString);
    JSEvaluateScript(globalContext, statement, nullptr, nullptr, 1, nullptr);
    JSContextGroupRelease(contextGroup);
    JSStringRelease(statement);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_syrf_location_interfaces_SYRFCore_executeJSToGetObject(JNIEnv *env, jobject thiz,
                                                                jstring function) {
    JSValueRef exception = nullptr;
    JSStringRef fName = JSStringCreateWithUTF8CString(env->GetStringUTFChars(function, nullptr));
    JSValueRef retValue = JSEvaluateScript(globalContext, fName, nullptr, nullptr, 1, &exception);
    JSStringRef retString = JSValueToStringCopy(globalContext, retValue, nullptr);
    std::string hello = JSStringToStdString(retString);

    if (exception) {
        // TODO: handle the exception
    }

    return env->NewStringUTF(hello.c_str());
}