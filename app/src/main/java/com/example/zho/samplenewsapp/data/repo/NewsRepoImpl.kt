package com.example.zho.samplenewsapp.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.zho.samplenewsapp.common.model.Result
import com.example.zho.samplenewsapp.data.local.db.NewsDatabase
import com.example.zho.samplenewsapp.data.mapper.toNewsItem
import com.example.zho.samplenewsapp.data.mapper.toNewsItemEntity
import com.example.zho.samplenewsapp.data.mapper.toNewsItemList
import com.example.zho.samplenewsapp.data.mapper.toNewsItems
import com.example.zho.samplenewsapp.data.remote.NewsNetworkApi
import com.example.zho.samplenewsapp.data.remote.NewsFeedDataPagingSource
import com.example.zho.samplenewsapp.domain.model.NewsItem
import com.example.zho.samplenewsapp.domain.repo.NewsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class NewsRepoImpl @Inject constructor(
    private val db: NewsDatabase,
    private val newsNetworkApi: NewsNetworkApi,
    private val newsFeedDataPagingSource: NewsFeedDataPagingSource
) : NewsRepo {

    override suspend fun getNewsByPage(pageNo: Int): Flow<Result<List<NewsItem>?>> = flow {
        val response = newsNetworkApi.getNewsByPage(pageNo = pageNo)

        if (!response.isSuccessful || response.code() >= 400 ) {
            emit(Result.Failure(message = "Request Failed."))
        }

        if (response.body() == null || response.body()?.responseData == null) {
             emit(Result.Failure(message = "Response No Data Found."))
        }

        emit(Result.Success(data = response.body()?.responseData?.toNewsItems()))

    }

    override suspend fun getNewsBySearch(queryText: String): Flow<Result<List<NewsItem>?>> = flow {
        val response = newsNetworkApi.getNewsBySearch(queryText)

        if (!response.isSuccessful || response.code() >= 400 ) {
            emit(Result.Failure(message = "Request Failed."))
        }

        if (response.body() == null || response.body()?.responseData == null) {
            emit(Result.Failure(message = "Response No Data Found."))
        }

        val newsItemEntity = response.body()?.responseData?.toNewsItems()?.toNewsItemEntity()
        newsItemEntity?.let {
            db.newsItemDao.insertNewsItemLists(newsItemEntity)
        }
        emit(Result.Success(data = newsItemEntity?.toNewsItemList()))
    }

    override fun getNewsFromPager(): Flow<PagingData<NewsItem>> {
        return Pager(
            config = PagingConfig(pageSize = 1),
            pagingSourceFactory = {
                newsFeedDataPagingSource
            }
        ).flow
    }

    override suspend fun getBookMarkNewsItems(): Flow<List<NewsItem>> = flow {
        val result = db.newsItemDao.getBookMarkedNewsItems().toNewsItemList()
        emit(result)
    }

    override suspend fun getNewsItemByUuid(uuid: String): Flow<NewsItem> = flow {
        val result = db.newsItemDao.getNewsItemByUuid(uuid).toNewsItem()
        emit(result)
    }

    override suspend fun bookmarkNewsItem(newsItem: NewsItem) {
        Timber.d("Repo bookmarkNewsItem - $newsItem")
        val id = db.newsItemDao.updateNewsItemAsBookmarked(newsItem.toNewsItemEntity())
        Timber.d("Repo bookmarkNewsItem id - $id")
    }

    override suspend fun updateNewsItemBookmark(uuid: String, isBookmark: Boolean) {
        Timber.d("Repo bookmarkNewsItem uuid - $uuid, isBookmark - $isBookmark")
        val result = db.newsItemDao.updateNewsItemBookmarkState(uuid, isBookmark)
        Timber.d("Repo bookmarkNewsItem result - $result")
    }

    override suspend fun getNewsItemIsBookmarked(uuid: String): Flow<Boolean> = flow {
        db.newsItemDao.getNewsItemIsBookmarked(uuid)
    }
}