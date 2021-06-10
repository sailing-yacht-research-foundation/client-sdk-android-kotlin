package com.syrf.core.interfaces

import android.util.Log
import com.syrf.core.BuildConfig
import com.syrf.core.configs.SYRFLoggingConfig
import com.syrf.core.configs.SYRFLoggingConfig.Companion.SYRF_LOGGING_TAG
import com.syrf.core.trees.SYRFDebugTree
import com.syrf.core.trees.SYRFReleaseTree
import timber.log.Timber

/**
 * The interface that exported to the client.
 * You can use methods from it to config and get logging from all another SYRF-SDKs.
 * The recommendation is call init in Application's onCreate method.
 */
interface SYRFLoggingInterface {
    fun init()
    fun init(config: SYRFLoggingConfig)
    fun getConfig(): SYRFLoggingConfig
}

/**
 * The singleton, implementation of [SYRFLoggingInterface] class.
 * When you call init method, this will plant a Timber's tree based in [SYRFLoggingConfig]
 */
object SYRFLogging : SYRFLoggingInterface {

    private lateinit var config: SYRFLoggingConfig

    override fun init() {
        this.init(SYRFLoggingConfig.DEFAULT)
    }

    override fun init(config: SYRFLoggingConfig) {
        this.config = config
        plantTheTree()
    }

    override fun getConfig(): SYRFLoggingConfig {
        checkConfig()
        return config
    }

    private fun plantTheTree() {
        if (BuildConfig.DEBUG && config.isDebugLogEnabled()) {
            Timber.plant(SYRFDebugTree(config))
        }

        if (!BuildConfig.DEBUG && config.isReleaseLogEnabled()) {
            Timber.plant(SYRFReleaseTree(config))
        }
    }

    @Throws(Exception::class)
    private fun checkConfig() {
        if (!this::config.isInitialized) {
            throw Exception("Config should be set before library use")
        }
    }
}

/**
 * A wrapper of [Timber] class that will be used in all SYRF-SDKs.
 * It hardcoded the logging tag to [SYRF_LOGGING_TAG] in purpose of tree configuration
 */
object SYRFTimber {
    /** Log a verbose exception. */
    fun v(t: Throwable) {
        prepareLog(Log.VERBOSE, t)
    }

    /** Log a verbose message. */
    fun v(message: String) {
        prepareLog(Log.VERBOSE, null, message)
    }

    /** Log a debug exception. */
    fun d(t: Throwable) {
        prepareLog(Log.DEBUG, t)
    }

    /** Log a debug message. */
    fun d(message: String) {
        prepareLog(Log.DEBUG, null, message)
    }

    /** Log an info exception. */
    fun i(t: Throwable) {
        prepareLog(Log.INFO, t)
    }

    /** Log an info message. */
    fun i(message: String) {
        prepareLog(Log.INFO, null, message)
    }

    /** Log a warning exception. */
    fun w(t: Throwable) {
        prepareLog(Log.WARN, t)
    }

    /** Log a warning message. */
    fun w(message: String) {
        prepareLog(Log.WARN, null, message)
    }

    /** Log an error exception. */
    fun e(t: Throwable) {
        prepareLog(Log.ERROR, t)
    }

    /** Log an error message. */
    fun e(message: String) {
        prepareLog(Log.ERROR, null, message)
    }

    /** Log an assert exception. */
    fun wtf(t: Throwable) {
        prepareLog(Log.ASSERT, t)
    }

    /** Log an assert message. */
    fun wtf(message: String) {
        prepareLog(Log.ASSERT, null, message)
    }

    private fun prepareLog(priority: Int, t: Throwable? = null, message: String? = null) {
        Timber.tag(SYRF_LOGGING_TAG).log(priority, t, message)
    }
}