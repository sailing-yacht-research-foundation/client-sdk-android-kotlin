package com.syrf.location.interfaces

import android.app.Activity
import com.syrf.location.utils.NoConfigException
import com.syrf.location.utils.SDKValidator

/**
 * The interface that exported to the client.
 * Help to execute any javascript code.
 */
interface SYRFCoreInterface {
    fun configure(context: Activity)

    fun executeJavascript(script: String)

    fun executeJavascriptFunction(functionName: String, vararg params: Any?): String
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
    override fun executeJavascript(script: String) {
        checkConfig()
        executeJS(script)
    }

    override fun executeJavascriptFunction(
        functionName: String,
        vararg params: Any?
    ): String {
        checkConfig()
        val function = "$functionName(${params.joinToString(separator = ",")})"
        return executeJSToGetObject(function)
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
    private external fun executeJS(script: String)

    /**
     * The link to native function from library for executing javascript code
     * @param function The javascript function
     */
    private external fun executeJSToGetObject(function: String): String

}