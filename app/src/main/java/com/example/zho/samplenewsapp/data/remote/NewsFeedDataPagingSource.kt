package com.example.zho.samplenewsapp.data.remote

import android.app.Application
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.example.zho.samplenewsapp.data.local.db.NewsDatabase
import com.example.zho.samplenewsapp.data.mapper.toNewsItem
import com.example.zho.samplenewsapp.data.mapper.toNewsItemEntity
import com.example.zho.samplenewsapp.data.mapper.toNewsItemList
import com.example.zho.samplenewsapp.data.mapper.toNewsItems
import com.example.zho.samplenewsapp.domain.model.NewsItem
import okio.IOException
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

class NewsFeedDataPagingSource @Inject constructor(
    private val app: Application,
    private val db: NewsDatabase,
    private val newsNetworkApi: NewsNetworkApi
) : PagingSource<Int, NewsItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsItem> {
        try {
            Timber.d("pagingSource page - ${params.key}")
            val page = params.key ?: 1

            val response = newsNetworkApi.getNewsByPage(pageNo = page)

            Timber.d("pagingSource response =  isSuccessful - ${response.isSuccessful}")

//            //TODO HARDCODED VALUE
//            val tempData = listOf(
//                NewsItem(
//                    heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
//                    imageUrl = "images/2024/12/19/crosswords/20wordle-review-art-1280/20wordle-review-art-1280-largeHorizontal375.jpg",
//                    publishedBy = "By New York Times Games",
//                    paragraph = "Welcome to The Wordle Review. Be warned: This page contains spoilers for today’s puzzle. Solve Wordle first, or scroll at your own risk.\n" +
//                            "\n" +
//                            "Wordle is released at midnight in your time zone. In order to accommodate all time zones, there will be two Wordle Reviews live every day, dated based on Eastern Standard Time. If you find yourself on the wrong review, check the number of your puzzle, and go to this page to find the corresponding review.",
//                    wordCount = 100,
//                    articleWebUrl = "https://www.nytimes.com/2024/12/19/crosswords/wordle-review-1280.html",
//                    publishedOn = "18 Nov 22"
//                ),
//                NewsItem(
//                    heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
//                    imageUrl = "images/2024/12/19/crosswords/20wordle-review-art-1280/20wordle-review-art-1280-largeHorizontal375.jpg",
//                    publishedBy = "By New York Times Games",
//                    paragraph = "Welcome to The Wordle Review. Be warned: This page contains spoilers for today’s puzzle. Solve Wordle first, or scroll at your own risk.\n" +
//                            "\n" +
//                            "Wordle is released at midnight in your time zone. In order to accommodate all time zones, there will be two Wordle Reviews live every day, dated based on Eastern Standard Time. If you find yourself on the wrong review, check the number of your puzzle, and go to this page to find the corresponding review.",
//                    wordCount = 100,
//                    articleWebUrl = "https://www.nytimes.com/2024/12/19/crosswords/wordle-review-1280.html",
//                    publishedOn = "18 Nov 22"
//                ),
//                NewsItem(
//                    heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
//                    imageUrl = "images/2024/12/19/crosswords/20wordle-review-art-1280/20wordle-review-art-1280-largeHorizontal375.jpg",
//                    publishedBy = "By New York Times Games",
//                    paragraph = "Welcome to The Wordle Review. Be warned: This page contains spoilers for today’s puzzle. Solve Wordle first, or scroll at your own risk.\n" +
//                            "\n" +
//                            "Wordle is released at midnight in your time zone. In order to accommodate all time zones, there will be two Wordle Reviews live every day, dated based on Eastern Standard Time. If you find yourself on the wrong review, check the number of your puzzle, and go to this page to find the corresponding review.",
//                    wordCount = 100,
//                    articleWebUrl = "https://www.nytimes.com/2024/12/19/crosswords/wordle-review-1280.html",
//                    publishedOn = "18 Nov 22"
//                ),
//                NewsItem(
//                    heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
//                    imageUrl = "images/2024/12/19/crosswords/20wordle-review-art-1280/20wordle-review-art-1280-largeHorizontal375.jpg",
//                    publishedBy = "By New York Times Games",
//                    paragraph = "Welcome to The Wordle Review. Be warned: This page contains spoilers for today’s puzzle. Solve Wordle first, or scroll at your own risk.\n" +
//                            "\n" +
//                            "Wordle is released at midnight in your time zone. In order to accommodate all time zones, there will be two Wordle Reviews live every day, dated based on Eastern Standard Time. If you find yourself on the wrong review, check the number of your puzzle, and go to this page to find the corresponding review.",
//                    wordCount = 100,
//                    articleWebUrl = "https://www.nytimes.com/2024/12/19/crosswords/wordle-review-1280.html",
//                    publishedOn = "18 Nov 22"
//                ),
//            )
//            //db.newsItemDao.insertNewsItemLists(tempData.toNewsItemEntity())
//            return LoadResult.Page(
//                data = tempData,
//                prevKey = null,
//                nextKey = null
//            )

            if (!response.isSuccessful || response.body()?.responseData == null) {
                val data = db.newsItemDao.getAllNewsItems().toNewsItemList()
                return LoadResult.Page(
                    data = data,
                    prevKey = null,
                    nextKey = null
                )
            }

            val newsItemEntityList = response.body()?.responseData?.toNewsItems()?.toNewsItemEntity()
            db.withTransaction {
                if (response.isSuccessful && !newsItemEntityList.isNullOrEmpty()) {
                    if (db.newsItemDao.getTotalNonBookmarkNewsItemCount() >= 30) {

                        val first10Record = db.newsItemDao.getNonBookMarkedNewsItems(10)
                        val first10Ids = first10Record.map { it.id }
                        db.newsItemDao.deleteNewsItemsByIds(first10Ids)

                        //db.newsItemDao.deleteAllNonBookmarkNewsItems()
                    }
                }
                if (newsItemEntityList != null) {
                    db.newsItemDao.insertNewsItemLists(newsItemEntityList)
                }
            }

            val data = newsItemEntityList?.toNewsItemList()

            Timber.d("pagingSource response = data.isNullOrEmpty() - ${data.isNullOrEmpty()}, data - $data")

            Timber.d("pagingSource newsItemEntityList = $newsItemEntityList")
            Timber.d("pagingSource newsItemEntityList to NewsItemList = ${newsItemEntityList?.toNewsItemList()}")
            Timber.d("pagingSource newsItemEntityList to NewsItem = ${newsItemEntityList?.get(0)?.toNewsItem()}")


            return LoadResult.Page(
                data = data ?: emptyList(),
                prevKey = null,
                nextKey = if (data.isNullOrEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            Timber.d("EXCEPTION IOException OCCURRED: ${e.message}")
            if (e is UnknownHostException) {
                val data = db.newsItemDao.getAllNewsItems().toNewsItemList()
                return LoadResult.Page(
                    data = data,
                    prevKey = null,
                    nextKey = null
                )
            }
            return LoadResult.Error(e)
        } catch (e: Exception) {
            Timber.d("EXCEPTION OCCURRED: ${e.message}")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NewsItem>): Int? {
       return state.anchorPosition?.let { anchorPosition ->
           state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
               ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
       }
    }

}


//
//@OptIn(ExperimentalPagingApi::class)
//class NewsRemotePagingMediator (
//    private val newsNetworkApi: NewsNetworkApi
//) : RemoteMediator<Int, List<NewsItem?>>() {
//
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, List<NewsItem?>>
//    ): MediatorResult {
//        return try {
//
//            val loadKey = when(loadType) {
//                LoadType.REFRESH -> 1
//                LoadType.APPEND -> {
//                    val lastItem = state.lastItemOrNull()
//                    if (lastItem.isNullOrEmpty()) {
//                        1
//                    } else {
//                        Timber.d("load() = pageSize = ${state.config.pageSize} , lastItem - $lastItem")
//                        1
//                    }
//                }
//                LoadType.PREPEND -> return MediatorResult.Success(
//                    endOfPaginationReached = true
//                )
//            }
//
//            val newsResponse = newsNetworkApi.getNewsByPage(loadKey)
//
//            MediatorResult.Success(
//                endOfPaginationReached = newsResponse.body()?.responseData?.docs.isNullOrEmpty()
//            )
//        } catch (e: IOException) {
//            Timber.d("EXCEPTION OCCURRED - ${e.localizedMessage}")
//            MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            Timber.d("EXCEPTION OCCURRED - ${e.localizedMessage}")
//            MediatorResult.Error(e)
//        }
//    }
//}