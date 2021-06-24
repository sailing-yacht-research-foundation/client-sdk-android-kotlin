package com.syrf.core.interfaces

/**
 * The interface that exported to the client.
 * Help to execute any javascript code.
 */
interface SYRFCoreInterface {
    fun executeJavascript(script: String): String
}

/**
 * The singleton, implementation of [SYRFCoreInterface] class.
 * This will load a native library and used it for executing javascript code
 */
object SYRFCore: SYRFCoreInterface {

    /**
     * Load the native library
     */
    init {
        System.loadLibrary("core");
    }

    /**
     * The function that help you execute javascript code
     * @param script The script will be executed
     */
    override fun executeJavascript(script: String): String {
        return executeJS(script)
    }

    /**
     * The link to native function from library for executing javascript code
     * @param script The script will be executed
     */
    private external fun executeJS(script: String): String
}