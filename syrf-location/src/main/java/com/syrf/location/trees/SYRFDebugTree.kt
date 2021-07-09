package com.syrf.location.trees

import com.syrf.location.configs.SYRFLoggingConfig
import com.syrf.location.configs.SYRFLoggingConfig.Companion.SYRF_LOGGING_TAG

/**
 * Tree class that extend from [SYRFBaseTree] class for debug mode
 * @property config: The variable will be used for logging config
 */
class SYRFDebugTree(config: SYRFLoggingConfig) : SYRFBaseTree(config) {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return tag == SYRF_LOGGING_TAG && priority >= config.debugPriority
    }
}