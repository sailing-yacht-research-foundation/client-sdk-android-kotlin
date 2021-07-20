package com.syrf.geospatial.utils

import java.io.File

fun readFileDirectlyAsText(file: File): String
        = file.readText(Charsets.UTF_8)