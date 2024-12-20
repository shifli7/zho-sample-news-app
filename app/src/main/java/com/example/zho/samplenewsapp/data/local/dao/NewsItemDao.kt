package com.example.zho.samplenewsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.zho.samplenewsapp.data.local.entity.NewsItemEntity
import com.example.zho.samplenewsapp.domain.model.NewsItem

@Dao
interface NewsItemDao {

    @Query("SELECT * FROM newsitementity")
    suspend fun getAllNewsItems(): List<NewsItemEntity>

    @Query("SELECT * FROM newsitementity WHERE isBookmarked IS 1 ORDER BY id DESC")
    suspend fun getBookMarkedNewsItems(): List<NewsItemEntity>

    @Query("SELECT * FROM newsitementity WHERE isBookmarked IS 0 ORDER BY id LIMIT :limit")
    suspend fun getNonBookMarkedNewsItems(limit: Int): List<NewsItemEntity>

    @Query("DELETE FROM newsitementity WHERE id IN (:ids)")
    suspend fun deleteNewsItemsByIds(ids: List<Int?>)

    @Query("DELETE FROM newsitementity WHERE isBookmarked IS 0")
    suspend fun deleteAllNonBookmarkNewsItems()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsItemLists(newsItemEntityList: List<NewsItemEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNewsItemAsBookmarked(newsItemEntity: NewsItemEntity): Int

    @Query("UPDATE newsitementity SET isBookmarked = :isBookmarked WHERE uuid = :uuid")
    suspend fun updateNewsItemBookmarkState(uuid: String, isBookmarked: Boolean): Int

    @Query("SELECT * FROM newsitementity WHERE uuid IS :uuid")
    suspend fun getNewsItemByUuid(uuid: String): NewsItemEntity

    @Query("SELECT COUNT(id) FROM newsitementity WHERE isBookmarked IS 0")
    suspend fun getTotalNonBookmarkNewsItemCount(): Int

    @Query("SELECT isBookmarked FROM newsitementity WHERE uuid IS :uuid")
    suspend fun getNewsItemIsBookmarked(uuid: String): Boolean

}