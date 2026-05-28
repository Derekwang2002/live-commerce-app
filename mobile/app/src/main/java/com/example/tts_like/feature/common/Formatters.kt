package com.example.tts_like.feature.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun money(value: Double): String {
    return "¥${String.format(Locale.US, "%.2f", value)}"
}

fun shortTime(millis: Long?): String {
    if (millis == null) return "-"
    return SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(Date(millis))
}
