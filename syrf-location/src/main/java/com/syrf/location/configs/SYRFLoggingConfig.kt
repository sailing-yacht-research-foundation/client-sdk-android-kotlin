package com.syrf.location.configs

import android.util.Log
import com.syrf.location.interfaces.SYRFLogging

/**
 * The class help you config params for [SYRFLogging]
 * @property debugPriority The priority for logging when application is running in debug mode
 * @property releasePriority The priority for logging when application is running in release mode
 * debugPriority and releasePriority used for determining what log type can be written in logcat
 * and should be one of [VERBOSE], [DEBUG], [INFO],[WARN], [ERROR], [ASSERT], [NONE]
 * @property maxLogLength Max log length per line, if message longer than this value
 * it will be split into chunks when printed in the logcat
 */
class SYRFLoggingConfig private constructor(
    val debugPriority: Int,
    val releasePriority: Int,
    val maxLogLength: Int
) {
    companion object {
        /**
         * List of log levels clone from [Log] class
         * with adding [NONE] value, using for completely disable all log
         */
        const val VERBOSE = Log.VERBOSE
        const val DEBUG = Log.DEBUG
        const val INFO = Log.INFO
        const val WARN = Log.WARN
        const val ERROR = Log.ERROR
        const val ASSERT = Log.ASSERT
        const val NONE = Log.ASSERT + 1

        /**
         * The log tag used for all logging from SDKs
         */
        const val SYRF_LOGGING_TAG = "SYRFLogging"
        private const val MAX_LOG_LENGTH = 4000

        /**
         * Provide a default config for using in cases client init logging
         * without config or missing some properties in config
         */
        val DEFAULT: SYRFLoggingConfig =
            SYRFLoggingConfig(
                debugPriority = VERBOSE,
                releasePriority = NONE,
                maxLogLength = MAX_LOG_LENGTH
            )
    }

    /**
     * Builder class that help to create an instance of [SYRFLoggingConfig]
     */
    data class Builder(
        var debugPriority: Int? = null,
        var releasePriority: Int? = null,
        var maxLogLength: Int? = null
    ) {
        fun debugPriority(debugPriority: Int) = apply { this.debugPriority = debugPriority }
        fun releasePriority(releasePriority: Int) = apply { this.releasePriority = releasePriority }
        fun maxLogLength(maxLogLength: Int) = apply { this.maxLogLength = maxLogLength }
        fun set() = SYRFLoggingConfig(
             debugPriority ?: DEFAULT.debugPriority,
            releasePriority ?: DEFAULT.releasePriority,
            maxLogLength ?: DEFAULT.maxLogLength
        )
    }

    /**
     * Check whether the debug logging is enabled
     */
    fun isDebugLogEnabled(): Boolean {
        return debugPriority < NONE
    }

    /**
     * Check whether the release logging is enabled
     */
    fun isReleaseLogEnabled(): Boolean {
        return releasePriority < NONE
    }
}
