package com.example.zho.samplenewsapp.common.model

sealed class Result<out T>(val data: T?, val message: String? = null) {
    class Success<T>(data: T) : Result<T>(data)
    class Failure<T>(message: String?, data: T? = null) : Result<T>(data, message)
}