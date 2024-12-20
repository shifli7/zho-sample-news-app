package com.example.zho.samplenewsapp.domain.repo

import androidx.paging.Pager
import androidx.paging.PagingData
import com.example.zho.samplenewsapp.common.model.Result
import com.example.zho.samplenewsapp.domain.model.NewsItem
import kotlinx.coroutines.flow.Flow

interface NewsRepo {
    suspend fun getNewsByPage(pageNo: Int): Flow<Result<List<NewsItem>?>>
    suspend fun getNewsBySearch(queryText: String): Flow<Result<List<NewsItem>?>>
    fun getNewsFromPager(): Flow<PagingData<NewsItem>>
    suspend fun getBookMarkNewsItems(): Flow<List<NewsItem>>
    suspend fun getNewsItemByUuid(uuid: String): Flow<NewsItem>
    suspend fun bookmarkNewsItem(newsItem: NewsItem)
    suspend fun updateNewsItemBookmark(uuid: String, isBookmark:Boolean)
    suspend fun getNewsItemIsBookmarked(uuid: String): Flow<Boolean>
}