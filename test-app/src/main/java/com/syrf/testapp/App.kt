package com.syrf.testapp

import android.app.Application
import com.syrf.core.configs.SYRFLoggingConfig
import com.syrf.core.interfaces.SYRFLogging

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        initSYRFLogging()
    }

    /**
     * Init SYRF logging. You can init with default or custom config
     */
    private fun initSYRFLogging() {
        // Init SYRFLogging with default configuration
        // SYRFLogging.init()

        // Init SYRFLogging with custom configuration
        val config = SYRFLoggingConfig.Builder()
            .debugPriority(SYRFLoggingConfig.INFO)
            .releasePriority(SYRFLoggingConfig.ERROR)
            .maxLogLength(3000)
            .set()
        SYRFLogging.init(config)
    }
}