package com.syrf.location.permissions

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PermissionsManagerTest {

    private var isSuccessCallbackInvoked = false
    private var isExceptionCallbackInvoked = false
    private val successOnPermissionsRequest = { isSuccessCallbackInvoked = true }
    private val failOnPermissionsRequest = { isExceptionCallbackInvoked = true }

    private lateinit var permissionsManager: PermissionsManager
    private lateinit var context: Activity

    @Before
    fun setUp() {
        context = mock(Activity::class.java)
        permissionsManager = PermissionsManager(context)
    }

    @Test
    fun `when permissions array is empty then the exceptionCallback will be invoked`() {
        // Given
        resetGlobalState()
        val permissions = emptyArray<String>()

        // When
        permissionsManager.handleResults(
            permissions,
            successOnPermissionsRequest,
            failOnPermissionsRequest
        )

        // Then
        Assert.assertTrue(isExceptionCallbackInvoked)
        Assert.assertFalse(isSuccessCallbackInvoked)
    }

    @Test
    fun `when one of permissions is not granted then the exceptionCallback will be invoked`() {
        // Given
        resetGlobalState()
        val permissions = PERMISSIONS
        mockCheckPermissionResult(
            permission = ACCESS_FINE_LOCATION_PERMISSION,
            result = PackageManager.PERMISSION_GRANTED
        )
        mockCheckPermissionResult(
            permission = ACCESS_COARSE_LOCATION_PERMISSION,
            result = PackageManager.PERMISSION_DENIED
        )

        // When
        permissionsManager.handleResults(
            permissions,
            successOnPermissionsRequest,
            failOnPermissionsRequest
        )

        // Then
        Assert.assertTrue(isExceptionCallbackInvoked)
        Assert.assertFalse(isSuccessCallbackInvoked)
    }

    @Test
    fun `when all permissions are granted then the successCallback will be invoked`() {
        // Given
        resetGlobalState()
        val permissions = PERMISSIONS
        mockCheckPermissionResult(
            permission = ACCESS_FINE_LOCATION_PERMISSION,
            result = PackageManager.PERMISSION_GRANTED
        )
        mockCheckPermissionResult(
            permission = ACCESS_COARSE_LOCATION_PERMISSION,
            result = PackageManager.PERMISSION_GRANTED
        )

        // When
        permissionsManager.handleResults(
            permissions,
            successOnPermissionsRequest,
            failOnPermissionsRequest
        )

        // Then
        Assert.assertFalse(isExceptionCallbackInvoked)
        Assert.assertTrue(isSuccessCallbackInvoked)
    }

    private fun mockCheckPermissionResult(permission: String, result: Int) {
        `when`(
            ContextCompat.checkSelfPermission(
                context,
                permission
            )
        ).thenReturn(result)
    }

    private fun resetGlobalState() {
        isSuccessCallbackInvoked = false
        isExceptionCallbackInvoked = false
    }

    companion object {
        const val ACCESS_FINE_LOCATION_PERMISSION = android.Manifest.permission.ACCESS_FINE_LOCATION
        const val ACCESS_COARSE_LOCATION_PERMISSION =
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        val PERMISSIONS =
            arrayOf(ACCESS_FINE_LOCATION_PERMISSION, ACCESS_COARSE_LOCATION_PERMISSION)
    }
}