package com.syrf.core.timbertrees

import android.os.Build
import android.util.Log
import android.util.Log.*
import com.syrf.core.configs.SYRFLoggingConfig
import com.syrf.core.interfaces.SYRFLogging
import timber.log.Timber
import kotlin.math.min

abstract class SYRFBaseTree(val config: SYRFLoggingConfig): Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Workaround for devices that doesn't show lower priority logs
        var newPriority = priority
        if (Build.MANUFACTURER == "HUAWEI" || Build.MANUFACTURER == "samsung") {
            if (priority == VERBOSE || priority == DEBUG || priority == INFO) {
                newPriority = ERROR
            }
        }

        // Message is short enough, doesn't need to be broken into chunks
        if (message.length < config.maxLogLength) {
            println(newPriority, tag, message)
            return
        }

        // Split by line, then ensure each line can fit into Log's max length
        var i = 0
        val length = message.length
        while (i < length) {
            var newline = message.indexOf('\n', i)
            newline = if (newline != -1) newline else length
            do {
                val end = min(newline, i + config.maxLogLength)
                val part = message.substring(i, end)
                println(newPriority, tag, part)
                i = end
            } while (i < newline)
            i++
        }
    }
}