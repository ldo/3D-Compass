LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := gl_useful

LOCAL_LDLIBS := -L${SYSROOT}/usr/lib -lGLESv2 -llog

LOCAL_SRC_FILES := gl_useful.c

LOCAL_CFLAGS += -std=c99

include $(BUILD_SHARED_LIBRARY)
