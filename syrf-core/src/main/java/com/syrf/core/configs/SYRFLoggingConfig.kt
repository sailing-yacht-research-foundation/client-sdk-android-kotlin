package com.syrf.core.configs

import android.util.Log
import com.syrf.core.interfaces.SYRFLogging

/**
 * The class help you config params for [SYRFLogging]
 * @property debugPriority: priority for logging when application is running in debug mode
 * @property releasePriority: priority for logging when application is running in release mode
 * debugPriority and releasePriority used for determining what log type can be written in logcat
 * and should be one of [VERBOSE], [DEBUG], [INFO],[WARN], [ERROR], [ASSERT], [NONE]
 * @property maxLogLength: max log length per line, if message longer than this value
 * it will be split into chunks
 */
class SYRFLoggingConfig private constructor(
    val debugPriority: Int,
    val releasePriority: Int,
    val maxLogLength: Int
) {
    companion object {
        const val VERBOSE = Log.VERBOSE
        const val DEBUG = Log.DEBUG
        const val INFO = Log.INFO
        const val WARN = Log.WARN
        const val ERROR = Log.ERROR
        const val ASSERT = Log.ASSERT
        const val NONE = Log.ASSERT + 1

        const val SYRF_LOGGING_TAG = "SYRFLogging"
        private const val MAX_LOG_LENGTH = 4000

        val DEFAULT: SYRFLoggingConfig =
            SYRFLoggingConfig(
                debugPriority = VERBOSE,
                releasePriority = NONE,
                maxLogLength = MAX_LOG_LENGTH
            )
    }

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

    fun isDebugLogEnabled(): Boolean {
        return debugPriority < NONE
    }

    fun isReleaseLogEnabled(): Boolean {
        return releasePriority < NONE
    }
}
