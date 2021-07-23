package com.syrf.location.interfaces

import android.app.Activity
import com.syrf.location.utils.NoConfigException
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SYRFCoreTest {

    private lateinit var context: Activity

    @Before
    fun setUp() {
        context = Mockito.mock(Activity::class.java)
    }

    @Test
    fun `when use library before configuration then throw NoConfigException`() {
        // When
        val thrown = Assert.assertThrows(
            Exception::class.java
        ) {
            SYRFCore.executeJavascript("")
        }

        // Then
        Assert.assertTrue(thrown is NoConfigException)
    }
}