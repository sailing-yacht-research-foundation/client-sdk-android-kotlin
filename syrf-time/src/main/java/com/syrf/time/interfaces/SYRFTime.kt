package com.syrf.time.interfaces

import android.content.Context
import com.lyft.kronos.AndroidClockFactory
import com.lyft.kronos.KronosClock
import com.syrf.location.utils.NoConfigException
import com.syrf.location.utils.SDKValidator
import com.syrf.time.configs.SYRFTimeConfig
import java.lang.Exception

/**
 * The interface that exported to the client.
 * You can use methods from it to config and get the current time.
 */
interface SYRFTimeInterface {
    fun configure(config: SYRFTimeConfig, context: Context)
    fun getCurrentTimeMS(): Long
}

/**
 * The singleton, implementation of [SYRFTimeInterface] class.
 * It using Kronos - is an open source Network Time Protocol (NTP) synchronization library
 * for providing a trusted clock on the JVM. The time will be unaffected when
 * the local time is changed while app is running
 */
object SYRFTime: SYRFTimeInterface {
    private lateinit var kronosClock: KronosClock

    /**
     * Configure the Time Module. The method should be called before any usage
     *
     * @param config Configuration object
     * @param context The context.
     */
    override fun configure(config: SYRFTimeConfig, context: Context) {
        SDKValidator.checkForApiKey(context)

        kronosClock = AndroidClockFactory.createKronosClock(context, ntpHosts = config.ntpHosts)
        kronosClock.syncInBackground()
    }

    /**
     * Get the current time that is synced with the NTP servers
     * @throws NoConfigException
     */
    override fun getCurrentTimeMS(): Long {
        if (this::kronosClock.isInitialized) {
            return kronosClock.getCurrentTimeMs()
        }
        throw NoConfigException()
    }
}