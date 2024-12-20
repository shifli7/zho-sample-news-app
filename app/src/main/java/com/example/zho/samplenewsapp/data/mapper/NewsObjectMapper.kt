package com.example.zho.samplenewsapp.data.mapper

import com.example.zho.samplenewsapp.common.utils.dateConversion
import com.example.zho.samplenewsapp.data.local.entity.NewsItemEntity
import com.example.zho.samplenewsapp.data.model.ResponseData
import com.example.zho.samplenewsapp.domain.model.NewsItem

fun ResponseData.toNewsItems() : List<NewsItem> {
    if (this.docs.isNullOrEmpty()) return emptyList()

    return this.docs.filterNotNull()
        .map {
            NewsItem(
                heading = it.headline?.main ?: it.snippet ?: "",
                imageUrl = it.multimedia?.filterNotNull()?.
                    find {
                        it.subtype == "largeHorizontalJumbo" ||
                                it.subtype == "largeHorizontal375"
                    }?.url ?: "",
                publishedOn = dateConversion(it.pubDate.toString(),
                    "yyyy-M-dd'T'H:mm:ss+SSSS",
                    "dd MMM yy") ?: "Recently",
                publishedBy = it.byline?.original ?: "",
                paragraph = it.leadParagraph ?: "",
                snippet = it.snippet ?: "",
                abstract = it.abstract ?: "",
                articleWebUrl = it.webUrl ?: "",
                wordCount = it.wordCount ?: 0
        )
    }
}

fun NewsItem.toNewsItemEntity(): NewsItemEntity {
    // TODO HARDCODED VALUE
    return NewsItemEntity(
        uuid = uuid,
        heading = heading,
        imageUrl = imageUrl,
        publishedOn = publishedOn,
        publishedBy = publishedBy,
        abstract = abstract,
        snippet = snippet,
        paragraph = paragraph,
        articleWebUrl = articleWebUrl,
        wordCount = wordCount
    )
}

fun NewsItemEntity.toNewsItem(): NewsItem {
    return NewsItem(
        uuid = uuid,
        isBookmarked =isBookmarked,
        heading = heading,
        imageUrl = imageUrl,
        publishedOn = publishedOn,
        publishedBy = publishedBy,
        abstract = abstract,
        snippet = snippet,
        paragraph = paragraph,
        articleWebUrl = articleWebUrl,
        wordCount = wordCount
    )
}

fun List<NewsItemEntity>.toNewsItemList(): List<NewsItem> {
    return this.map {
        it.toNewsItem()
    }
}

fun List<NewsItem>.toNewsItemEntity(): List<NewsItemEntity> {
    return this.map {
        it.toNewsItemEntity()
    }
}