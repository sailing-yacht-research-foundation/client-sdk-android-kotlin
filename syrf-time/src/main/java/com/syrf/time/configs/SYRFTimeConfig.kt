package com.syrf.time.configs

import com.lyft.kronos.DefaultParam
import com.syrf.time.interfaces.SYRFTime

/**
 * The class help you config params for [SYRFTime]
 * @property ntpHosts: a list of NTP servers with which to sync
 */
class SYRFTimeConfig private constructor(
    val ntpHosts: List<String>,
) {

    /**
     * Builder class that help to create an instance of [SYRFTimeConfig]
     */
    data class Builder(
        var ntpHosts: List<String>? = null,
    ) {
        fun ntpHosts(ntpHosts: List<String>) = apply { this.ntpHosts = ntpHosts }
        fun set() = SYRFTimeConfig(ntpHosts ?: DefaultParam.NTP_HOSTS)
    }
}
