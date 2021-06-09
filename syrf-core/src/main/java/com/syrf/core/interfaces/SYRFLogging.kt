package com.syrf.core.interfaces

import com.syrf.core.BuildConfig
import com.syrf.core.configs.SYRFLoggingConfig
import com.syrf.core.timbertrees.SYRFDebugTree
import com.syrf.core.timbertrees.SYRFReleaseTree
import timber.log.Timber

interface SYRFLoggingInterface {
    fun init()
    fun init(config: SYRFLoggingConfig)
    fun getConfig(): SYRFLoggingConfig
}

object SYRFLogging: SYRFLoggingInterface {

    private lateinit var config: SYRFLoggingConfig

    override fun init() {
        this.init(SYRFLoggingConfig.DEFAULT)
    }

    override fun init(config: SYRFLoggingConfig) {
        this.config = config
        initTimberWithConfig()
    }

    override fun getConfig(): SYRFLoggingConfig {
        checkConfig()
        return config
    }

    private fun initTimberWithConfig() {
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