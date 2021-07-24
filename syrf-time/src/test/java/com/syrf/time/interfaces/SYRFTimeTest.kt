package com.syrf.time.interfaces

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import com.syrf.location.utils.Constants
import com.syrf.location.utils.NoConfigException
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SYRFTimeTest {

    private lateinit var context: Activity

    @Before
    fun setUp() {
        context = Mockito.mock(Activity::class.java)
        mockSDKValidator(context)
    }

    @Test
    fun `when use library before configuration then throw NoConfigException`() {
        // When
        val thrown = Assert.assertThrows(
            Exception::class.java
        ) {
            SYRFTime.getCurrentTimeMS()
        }

        // Then
        Assert.assertTrue(thrown is NoConfigException)
    }

    private fun mockSDKValidator(context: Activity) {
        val applicationInfo = ApplicationInfo()
        applicationInfo.metaData =
            Bundle().apply { putString(Constants.SDK_KEY_NAME, "qwerty123456") }
        `when`(context.packageManager).thenReturn(mock())
        `when`(
            context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
        ).thenReturn(applicationInfo)
    }

}