package com.example.zho.samplenewsapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
data class NewsItem(
    val uuid: String = UUID.randomUUID().toString(),
    var isBookmarked: Boolean = false,
    val heading: String,
    var imageUrl: String,
    val publishedOn: String,
    val publishedBy: String,
    val paragraph: String,
    val abstract: String,
    val snippet: String,
    val articleWebUrl: String,
    val wordCount: Int
) : Parcelable
