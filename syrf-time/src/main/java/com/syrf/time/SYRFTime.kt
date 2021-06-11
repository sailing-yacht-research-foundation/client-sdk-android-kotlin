package com.syrf.time

import android.content.Context
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import com.syrf.time.configs.SYRFTimeConfig
import java.lang.Exception

interface SYRFTimeInterface {
    fun configure(config: SYRFTimeConfig, context: Context)
    fun getCurrentTimeMS(): Long
}

object SYRFTime: SYRFTimeInterface {
    private lateinit var kronosClock: KronosClock

    /**
     * Configure the Time Module. The method should be called before any usage
     *
     * @param config Configuration object
     * @param context The context.
     */
    override fun configure(config: SYRFTimeConfig, context: Context) {
        kronosClock = AndroidClockFactory.createKronosClock(context)
        kronosClock.syncInBackground()
    }

    /**
     * Get the current time that is synced with the NTP servers
     * @throws Exception
     */
    override fun getCurrentTimeMS(): Long {
        if (this::kronosClock.isInitialized) {
            return kronosClock.getCurrentTimeMs()
        }
        throw Exception("The module should be configured before use")
    }
}