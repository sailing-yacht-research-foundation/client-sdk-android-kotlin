package com.syrf.navigation.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SYRFNavigationConfig constructor(
    val throttleForegroundDelay: Int,
    val throttleBackgroundDelay: Int,

) : Parcelable {

}