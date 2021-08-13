package com.syrf.location

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.syrf.location.utils.Constants
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

fun mockSDKValidator(context: Activity) {
    val applicationInfo = ApplicationInfo()
    applicationInfo.metaData = Bundle().apply { putString(Constants.SDK_KEY_NAME, "qwerty123456") }
    `when`(context.packageManager).thenReturn(mock())
    `when`(
        context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
    ).thenReturn(applicationInfo)
}

fun mockCheckPermissionResult(context: Activity, permission: String, result: Int) {
    `when`(
        ContextCompat.checkSelfPermission(
            context,
            permission
        )
    ).thenReturn(result)
}