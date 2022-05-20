package com.syrf.navigation.data

data class SYRFToggler(
    val location: Boolean?,
    val heading: Boolean?,
    val deviceInfo: Boolean?
) {
    override fun toString(): String {
        return "location: ${location}, heading: ${heading}, deviceInfo: ${deviceInfo}"
    }
}
