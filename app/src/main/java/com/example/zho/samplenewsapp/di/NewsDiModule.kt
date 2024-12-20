package com.example.zho.samplenewsapp.di

import android.app.Application
import androidx.room.Room
import com.example.zho.samplenewsapp.BuildConfig
import com.example.zho.samplenewsapp.data.local.db.NewsDatabase
import com.example.zho.samplenewsapp.data.remote.NewsNetworkApi
import com.example.zho.samplenewsapp.data.remote.NewsFeedDataPagingSource
import com.example.zho.samplenewsapp.data.repo.NewsRepoImpl
import com.example.zho.samplenewsapp.domain.repo.NewsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsDiModule {

    @Provides
    @Singleton
    fun getOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
        }.build()
    }

    @Provides
    @Singleton
    fun getRetrofitBuilder(baseUrl: String): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(baseUrl)
            addConverterFactory(GsonConverterFactory.create())
            client(getOkhttpClient())
        }.build()
    }

    @Provides
    @Singleton
    fun getNewsNetworkApi(): NewsNetworkApi {
        return getRetrofitBuilder(NewsNetworkApi.BASEURL).create(NewsNetworkApi::class.java)
    }

    @Provides
    @Singleton
    fun getNewsFeedDataPagingSource(app: Application): NewsFeedDataPagingSource {
        return NewsFeedDataPagingSource(
            app = app,
            newsNetworkApi = getNewsNetworkApi(),
            db = getRoomDatabase(app)
        )
    }


    @Provides
    @Singleton
    fun getRoomDatabase(app: Application): NewsDatabase {
        return Room.databaseBuilder(
            app,
            NewsDatabase::class.java,
            "news_database_db"
        ).build()
    }

    @Provides
    @Singleton
    fun getNewsRepoImpl(app: Application) : NewsRepo {
        return NewsRepoImpl(
            getRoomDatabase(app),
            getNewsNetworkApi(),
            getNewsFeedDataPagingSource(app)
        )
    }

}