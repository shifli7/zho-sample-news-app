package com.example.zho.samplenewsapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.zho.samplenewsapp.data.local.dao.NewsItemDao
import com.example.zho.samplenewsapp.data.local.entity.NewsItemEntity

@Database (
    entities = [
        NewsItemEntity::class
    ],
    version = 1
)
abstract class NewsDatabase: RoomDatabase() {

    abstract val newsItemDao: NewsItemDao

}