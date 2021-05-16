package com.syrf.testapp.services

import java.text.SimpleDateFormat
import java.util.*

object TimeService {
    fun currentTime(): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.US)
        return simpleDateFormat.format(SYRFTime.getCurrentTimeMS())
    }
}