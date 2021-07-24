package com.syrf.location.interfaces

import android.app.Activity
import android.content.pm.PackageManager
import com.syrf.location.configs.SYRFLocationConfig
import com.syrf.location.mockCheckPermissionResult
import com.syrf.location.mockSDKValidator
import com.syrf.location.utils.CurrentPositionUpdateCallback
import com.syrf.location.utils.MissingLocationException
import com.syrf.location.utils.NoConfigException
import com.syrf.location.utils.SubscribeToLocationUpdateCallback
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
class SYRFLocationTest {

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
            SYRFLocation.subscribeToLocationUpdates(context)
        }

        // Then
        Assert.assertTrue(thrown is NoConfigException)
    }

    @Test
    fun `when configure library without config then getLocationConfig function return default value`() {
        // When
        SYRFLocation.configure(context)

        // Then
        Assert.assertEquals(SYRFLocationConfig.DEFAULT, SYRFLocation.getLocationConfig())
    }

    @Test
    fun `when configure library with config then getLocationConfig function return same value`() {
        // Given
        val config = SYRFLocationConfig.Builder()
            .updateInterval(1)
            .maximumLocationAccuracy(SYRFLocationConfig.PRIORITY_HIGH_ACCURACY)
            .set()

        // When
        SYRFLocation.configure(config, context)

        // Then
        Assert.assertEquals(config, SYRFLocation.getLocationConfig())
    }

    @Test
    fun `when no permissions then subscribeToLocationUpdates throw MissingLocationException`() {
        // Given
        mockNoLocationPermission()
        var thrown: Throwable? = null
        val callback: SubscribeToLocationUpdateCallback = { _, throwable ->
            thrown = throwable
        }

        // When
        SYRFLocation.configure(context)
        SYRFLocation.subscribeToLocationUpdates(context, callback)

        // Then
        Assert.assertNotNull(thrown)
        Assert.assertTrue(thrown is MissingLocationException)
    }

    @Test
    fun `when no permissions then getCurrentPosition throw MissingLocationException`() {
        // Given
        mockNoLocationPermission()
        var thrown: Throwable? = null
        val callback: CurrentPositionUpdateCallback = { _, throwable ->
            thrown = throwable
        }

        // When
        SYRFLocation.configure(context)
        SYRFLocation.getCurrentPosition(context, callback)

        // Then
        Assert.assertNotNull(thrown)
        Assert.assertTrue(thrown is MissingLocationException)
    }

    private fun mockNoLocationPermission() {
        mockCheckPermissionResult(
            context = context,
            permission = android.Manifest.permission.ACCESS_FINE_LOCATION,
            result = PackageManager.PERMISSION_GRANTED
        )
        mockCheckPermissionResult(
            context = context,
            permission = android.Manifest.permission.ACCESS_COARSE_LOCATION,
            result = PackageManager.PERMISSION_DENIED
        )
    }
}