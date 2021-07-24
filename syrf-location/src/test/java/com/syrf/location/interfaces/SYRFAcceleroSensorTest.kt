package com.syrf.location.interfaces

import android.app.Activity
import com.syrf.location.configs.SYRFAccelerometerConfig
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

@FixMethodOrder(MethodSorters.JVM)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SYRFAcceleroSensorTest {

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
            SYRFAcceleroSensor.getConfig()
        }

        // Then
        Assert.assertTrue(thrown is NoConfigException)
    }

    @Test
    fun `when configure library without config then getConfig function return default value`() {
        // When
        SYRFAcceleroSensor.configure(context)

        // Then
        Assert.assertEquals(SYRFAccelerometerConfig.DEFAULT, SYRFAcceleroSensor.getConfig())
    }

    @Test
    fun `when configure library with config then getConfig function return same value`() {
        // Given
        val config = SYRFAccelerometerConfig.Builder()
            .set()

        // When
        SYRFAcceleroSensor.configure(config, context)

        // Then
        Assert.assertEquals(config, SYRFAcceleroSensor.getConfig())
    }

}