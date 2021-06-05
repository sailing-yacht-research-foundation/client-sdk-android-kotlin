//
// Created by Sergij Rylskyj on 31.05.2021.
//

#ifndef COM_SYRF_CLIENTSDK_MAIN_H
#define COM_SYRF_CLIENTSDK_MAIN_H

#include "jni.h"

extern "C"
JNIEXPORT void JNICALL Java_SYRFGeospatial_checkNotGeometryCollection
        (JNIEnv *env, jobject obj, jstring javaString);

#endif //COM_SYRF_CLIENTSDK_MAIN_H