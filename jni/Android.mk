LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := execpty
LOCAL_SRC_FILES := execpty.c
#LOCAL_MODULE_FILENAME := libexecpty.so

include $(BUILD_EXECUTABLE)
