LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := geos
LOCAL_SRC_FILES := libs/$(TARGET_ARCH_ABI)/libgeos.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := geospatial
LOCAL_SRC_FILES := main.cpp
LOCAL_SHARED_LIBRARIES := geos
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := turf
LOCAL_SRC_FILES := turf.cpp
include $(BUILD_SHARED_LIBRARY)
