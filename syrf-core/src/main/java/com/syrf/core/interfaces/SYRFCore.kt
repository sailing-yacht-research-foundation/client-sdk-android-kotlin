package com.syrf.core.interfaces

interface SYRFCoreInterface {
}

object SYRFCore: SYRFCoreInterface {
    init {
        System.loadLibrary("core");
    }

    fun executeJavascript(script: String): String {
        return executeJS(script)
    }

    private external fun executeJS(script: String): String
}