LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under)

LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/
LOCAL_PACKAGE_NAME := PluginInstaller
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
