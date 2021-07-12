package com.syrf.location.interfaces

import android.app.Activity
import com.syrf.location.jnimaps.JNIReturnObject
import com.syrf.location.utils.NoConfigException
import com.syrf.location.utils.SDKValidator

/**
 * The interface that exported to the client.
 * Help to execute any javascript code.
 */
interface SYRFCoreInterface {
    fun configure(context: Activity)

    fun executeJavascript(script: String): String

    fun executeJavascriptToGetObject(script: String, functionName: String): JNIReturnObject

}

/**
 * The singleton, implementation of [SYRFCoreInterface] class.
 * This will load a native library and used it for executing javascript code
 */
object SYRFCore : SYRFCoreInterface {

    private var isConfigured = false

    /**
     * Load the native library
     */
    init {
        System.loadLibrary("core")
    }

    /**
     * Configure the module. The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        SDKValidator.checkForApiKey(context)
        isConfigured = true
    }

    /**
     * The function that help you execute javascript code
     * @param script The script will be executed
     * @throws NoConfigException
     */
    override fun executeJavascript(script: String): String {
        checkConfig()
        return executeJS(script)
    }

    override fun executeJavascriptToGetObject(script: String, functionName: String): JNIReturnObject {
        checkConfig()
        return executeJSToGetObject(script, functionName)
    }

    /**
     * Check for config and throw an exception if it is not initialized
     * @throws NoConfigException
     */
    @Throws(Exception::class)
    private fun checkConfig() {
        if (!isConfigured) {
            throw NoConfigException()
        }
    }

    /**
     * The link to native function from library for executing javascript code
     * @param script The script will be executed
     */
    private external fun executeJS(script: String): String

    /**
     * The link to native function from library for executing javascript code
     * @param script The script will be executed
     * @param functionName The javascript function name
     */
    private external fun executeJSToGetObject(script: String, functionName: String): JNIReturnObject

}