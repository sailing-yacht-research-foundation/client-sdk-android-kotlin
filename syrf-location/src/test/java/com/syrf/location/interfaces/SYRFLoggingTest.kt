package com.syrf.location.interfaces

import android.app.Activity
import com.syrf.location.configs.SYRFLoggingConfig
import com.syrf.location.mockSDKValidator
import com.syrf.location.utils.NoConfigException
import org.junit.Assert
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.Exception

@FixMethodOrder(MethodSorters.JVM)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SYRFLoggingTest {

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
            SYRFLogging.getConfig()
        }

        // Then
        Assert.assertTrue(thrown is NoConfigException)
    }

    @Test
    fun `when init library without config then getConfig function return default value`() {
        // When
        SYRFLogging.init(context)

        // Then
        Assert.assertEquals(SYRFLoggingConfig.DEFAULT, SYRFLogging.getConfig())
    }

    @Test
    fun `when init library with config then getConfig function return same value`() {
        // Given
        val config = SYRFLoggingConfig.Builder()
            .debugPriority(SYRFLoggingConfig.DEBUG)
            .releasePriority(SYRFLoggingConfig.ASSERT)
            .set()

        // When
        SYRFLogging.init(config, context)

        // Then
        Assert.assertEquals(config, SYRFLogging.getConfig())
    }

}