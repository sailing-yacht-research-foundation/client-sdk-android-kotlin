package com.syrf.location.interfaces

import android.content.Context
import android.util.Log
import com.syrf.location.BuildConfig
import com.syrf.location.configs.SYRFLoggingConfig
import com.syrf.location.configs.SYRFLoggingConfig.Companion.SYRF_LOGGING_TAG
import com.syrf.location.trees.SYRFDebugTree
import com.syrf.location.trees.SYRFReleaseTree
import com.syrf.location.utils.NoConfigException
import com.syrf.location.utils.SDKValidator
import timber.log.Timber

/**
 * The interface that exported to the client.
 * You can use methods from it to config and get logging from all another SYRF-SDKs.
 * The recommendation is call init in Application's onCreate method.
 */
interface SYRFLoggingInterface {
    fun init(context: Context)
    fun init(config: SYRFLoggingConfig, context: Context)
    fun getConfig(): SYRFLoggingConfig
}

/**
 * The singleton, implementation of [SYRFLoggingInterface] class.
 * When you call init method, this will plant a Timber's tree based in [SYRFLoggingConfig]
 */
object SYRFLogging : SYRFLoggingInterface {

    private lateinit var config: SYRFLoggingConfig

    /**
     * Init logging with default config
     * @param context The context. Should be the activity or application context
     */
    override fun init(context: Context) {
        init(SYRFLoggingConfig.DEFAULT, context)
    }

    /**
     * Init logging with provided config
     * @param config The configuration object
     * @param context The context. Should be the activity or application context
     */
    override fun init(config: SYRFLoggingConfig, context: Context) {
        SDKValidator.checkForApiKey(context)

        SYRFLogging.config = config
        plantTheTree()
    }

    /**
     * Check for initialization of config and return initialized value
     */
    override fun getConfig(): SYRFLoggingConfig {
        checkConfig()
        return config
    }

    /**
     * This method is used for plant a customized timber's tree based
     * on [BuildConfig.DEBUG] flag and provided instance of [SYRFLoggingConfig]
     */
    private fun plantTheTree() {
        if (BuildConfig.DEBUG && config.isDebugLogEnabled()) {
            Timber.plant(SYRFDebugTree(config))
        }

        if (!BuildConfig.DEBUG && config.isReleaseLogEnabled()) {
            Timber.plant(SYRFReleaseTree(config))
        }
    }

    /**
     * Check for config and throw an exception if it is not initialized
     * @throws NoConfigException
     */
    @Throws(Exception::class)
    private fun checkConfig() {
        if (!this::config.isInitialized) {
            throw NoConfigException()
        }
    }
}

/**
 * A wrapper of [Timber] class that will be used in all SYRF-SDKs.
 * It hardcoded the logging tag to [SYRF_LOGGING_TAG] in purpose of tree configuration
 */
object SYRFTimber {
    /** Log a verbose exception.
     * @param t The logging exception
     */
    fun v(t: Throwable) {
        printLog(Log.VERBOSE, t)
    }

    /** Log a verbose message.
     * @param message The logging message
     */
    fun v(message: String) {
        printLog(Log.VERBOSE, null, message)
    }

    /** Log a debug exception.
     * @param t The logging exception
     */
    fun d(t: Throwable) {
        printLog(Log.DEBUG, t)
    }

    /** Log a debug message.
     * @param message The logging message
     */
    fun d(message: String) {
        printLog(Log.DEBUG, null, message)
    }

    /** Log an info exception.
     * @param t The logging exception
     */
    fun i(t: Throwable) {
        printLog(Log.INFO, t)
    }

    /** Log an info message.
     * @param message The logging message
     */
    fun i(message: String) {
        printLog(Log.INFO, null, message)
    }

    /** Log a warning exception.
     * @param t The logging exception
     */
    fun w(t: Throwable) {
        printLog(Log.WARN, t)
    }

    /** Log a warning message.
     * @param message The logging message
     */
    fun w(message: String) {
        printLog(Log.WARN, null, message)
    }

    /** Log an error exception.
     * @param t The logging exception
     */
    fun e(t: Throwable) {
        printLog(Log.ERROR, t)
    }

    /** Log an error message.
     * @param message The logging message
     */
    fun e(message: String) {
        printLog(Log.ERROR, null, message)
    }

    /** Log an assert exception.
     * @param t The logging exception
     */
    fun wtf(t: Throwable) {
        printLog(Log.ASSERT, t)
    }

    /** Log an assert message.
     * @param message The logging message
     */
    fun wtf(message: String) {
        printLog(Log.ASSERT, null, message)
    }

    /**
     * Print a message or exception with [SYRF_LOGGING_TAG]
     * @param priority The logging priority
     * @param t The logging exception
     * @param message The logging message
     */
    private fun printLog(priority: Int, t: Throwable? = null, message: String? = null) {
        Timber.tag(SYRF_LOGGING_TAG).log(priority, t, message)
    }
}