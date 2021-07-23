package com.syrf.location.utils

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Exception

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SDKValidatorTest {

    private lateinit var context: Activity

    @Before
    fun setUp() {
        context = Mockito.mock(Activity::class.java)
    }

    @Test
    fun `when apiKey is null or empty then throw NoApiKeyException`() {
        // Given
        val apiKey = null
        mockAPIKeyValidator(apiKey)

        //
        // When
        val thrown = Assert.assertThrows(
            Exception::class.java
        ) {
            SDKValidator.checkForApiKey(context)
        }

        // Then
        Assert.assertTrue(thrown is NoApiKeyException)
    }

    @Test
    fun `when apiKey is invalid then throw InvalidApiKeyException`() {
        // Given
        val apiKey = "aaaaa"
        mockAPIKeyValidator(apiKey)

        // When
        val thrown = Assert.assertThrows(
            Exception::class.java
        ) {
            SDKValidator.checkForApiKey(context)
        }

        // Then
        Assert.assertTrue(thrown is InvalidApiKeyException)
    }

    @Test
    fun `when apiKey is valid then checkForApiKey function passed`() {
        // Given
        val apiKey = "qwerty123456"
        mockAPIKeyValidator(apiKey)

        // When
        SDKValidator.checkForApiKey(context)
    }

    private fun mockAPIKeyValidator(apiKey: String?) {
        val applicationInfo = ApplicationInfo()
        applicationInfo.metaData = Bundle().apply { putString(Constants.SDK_KEY_NAME, apiKey) }
        `when`(context.packageManager).thenReturn(mock())
        `when`(
            context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
        ).thenReturn(applicationInfo)
    }

}