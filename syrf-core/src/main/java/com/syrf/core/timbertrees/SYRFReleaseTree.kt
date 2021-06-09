package com.syrf.core.timbertrees

import com.syrf.core.configs.SYRFLoggingConfig

class SYRFReleaseTree(config: SYRFLoggingConfig) : SYRFBaseTree(config) {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority > config.releasePriority
    }
}