package com.syrf.testapp

import android.app.Application
import com.syrf.location.configs.SYRFLoggingConfig
import com.syrf.location.interfaces.SYRFLogging

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
        // SYRFLogging.init(this)

        // Init SYRFLogging with custom configuration
        val config = SYRFLoggingConfig.Builder()
            .debugPriority(SYRFLoggingConfig.INFO)
            .releasePriority(SYRFLoggingConfig.ERROR)
            .maxLogLength(3000)
            .set()
        SYRFLogging.init(config, this)
    }
}