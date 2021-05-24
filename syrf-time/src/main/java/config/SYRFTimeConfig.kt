package config

class SYRFTimeConfig private constructor(
    val ntpHosts: List<String>?,
) {
    data class Builder(
        var ntpHosts: List<String>? = null,
    ) {
        fun ntpHosts(ntpHosts: List<String>) = apply { this.ntpHosts = ntpHosts }
        fun set() = SYRFTimeConfig(ntpHosts)
    }
}
