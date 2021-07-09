package com.syrf.location.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

object SDKValidator {

    /**
     * Check for sdk secure token (api key). The method should be called at top of any module's
     * configuration method for make sure that 3rd party developer have right access to SDK
     * @param context The context. Should be activity or application context
     * @throws NoApiKeyException if api key is not found
     * @throws InvalidApiKeyException if api key is invalid
     */
    fun checkForApiKey(context: Context) {
        val applicationInfo: ApplicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = applicationInfo.metaData
        val apiKey = bundle.getString(Constants.SDK_KEY_NAME)

        if (apiKey.isNullOrEmpty()) {
            throw NoApiKeyException()
        }

        if (!isKeyValid(apiKey)) {
            throw InvalidApiKeyException()
        }
    }

    private fun isKeyValid(apiKey: String): Boolean {
        // Todo: Remove hardcoded and change to BE validation
        return apiKey == "qwerty123456"
    }
}