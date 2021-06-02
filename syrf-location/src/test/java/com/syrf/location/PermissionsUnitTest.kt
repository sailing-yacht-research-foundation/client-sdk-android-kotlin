package com.syrf.location

import android.app.Activity
import org.junit.Assert
import org.junit.Test

import org.junit.Before
import org.mockito.Mockito.mock
import com.syrf.location.permissions.PermissionsManager

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PermissionsUnitTest {

    private lateinit var permissionsManager: PermissionsManager

    @Before
    fun setUp() {
        val context = mock(Activity::class.java)
        permissionsManager = PermissionsManager(context)
    }

    @Test
    fun permissionsNotEmpty() {
        var isSuccess = false
        val successOnPermissionsRequest = { isSuccess = true }
        val failOnPermissionsRequest = { isSuccess = false }
        permissionsManager.handleResults(emptyArray(), successOnPermissionsRequest, failOnPermissionsRequest)
        Assert.assertFalse(isSuccess)
    }
}