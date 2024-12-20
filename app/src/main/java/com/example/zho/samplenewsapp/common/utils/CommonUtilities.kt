package com.example.zho.samplenewsapp.common.utils

import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun dateConversion(date: String, fromPattern: String, toPattern: String): String? {
    try {
        val fromFormatter = DateTimeFormatter.ofPattern(fromPattern)
        val toFormatter = DateTimeFormatter.ofPattern(toPattern)
        return LocalDate.parse(date, fromFormatter).format(toFormatter)
    } catch (e : Exception) {
        Timber.d("EXCEPTION OCCURRED: ${e.message}", e)
    }
    return null
}