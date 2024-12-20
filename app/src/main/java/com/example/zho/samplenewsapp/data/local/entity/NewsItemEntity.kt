package com.example.zho.samplenewsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class NewsItemEntity (
    @PrimaryKey
    var id: Int? = null,
    val uuid: String,
    var isBookmarked: Boolean = false,
    val heading: String,
    var imageUrl: String,
    val publishedOn: String,
    val publishedBy: String,
    val abstract: String,
    val snippet: String,
    val paragraph: String,
    val articleWebUrl: String,
    val wordCount: Int
)
