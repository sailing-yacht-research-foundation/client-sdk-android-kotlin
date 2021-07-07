package com.syrf.location.utils

class NoConfigException : Exception("Config should be set before library use")

class MissingLocationException : Exception("The location permission is required for library usage")

class NoApiKeyException : Exception(
    "For SDK usage. Please add <meta-data android:name=\"${Constants.SDK_KEY_NAME}\" " +
            "android:value=\"your_sdk_value\"/> in your AndroidManifest.xml file."
)

class InvalidApiKeyException :
    Exception("Invalid API key. Please check and update your key in AndroidManifest.xml file.")
