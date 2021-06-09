package com.syrf.core.configs

import android.util.Log
import com.syrf.core.interfaces.SYRFLogging

/**
 * The class help you config params for [SYRFLogging]
 * @property debugPriority: priority for logging when application is running in debug mode
 * @property releasePriority: priority for logging when application is running in release mode
 * debugLevel and releaseLevel should be one of [NONE], [VERBOSE], [DEBUG], [INFO],
 * [WARN], [ERROR], [ASSERT]
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
