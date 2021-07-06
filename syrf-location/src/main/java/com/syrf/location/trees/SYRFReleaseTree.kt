package com.syrf.location.trees

import com.syrf.location.configs.SYRFLoggingConfig
import com.syrf.location.configs.SYRFLoggingConfig.Companion.SYRF_LOGGING_TAG

/**
 * Tree class that extend from [SYRFBaseTree] class for release mode
 * @property config: The variable will be used for logging config
 */
class SYRFReleaseTree(config: SYRFLoggingConfig) : SYRFBaseTree(config) {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return tag == SYRF_LOGGING_TAG && priority >= config.releasePriority
    }
}