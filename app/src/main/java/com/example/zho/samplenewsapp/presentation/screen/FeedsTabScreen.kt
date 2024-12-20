package com.example.zho.samplenewsapp.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.zho.samplenewsapp.BuildConfig
import com.example.zho.samplenewsapp.R
import com.example.zho.samplenewsapp.domain.model.NewsItem
import com.example.zho.samplenewsapp.presentation.MainViewModel
import com.example.zho.samplenewsapp.presentation.view.LoadingIndicator
import com.example.zho.samplenewsapp.presentation.view.NewsCardView
import com.example.zho.samplenewsapp.presentation.view.ShowNoDataView
import timber.log.Timber


@Composable
fun FeedsTabScreen(
    modifier: Modifier = Modifier,
    lazyPagingNewsItem: LazyPagingItems<NewsItem>?,
    navController: NavController? = null,
    viewModel: MainViewModel? = null
) {

    if (lazyPagingNewsItem != null) {
        Box {
            if (
                lazyPagingNewsItem.loadState.append is LoadState.Error ||
                lazyPagingNewsItem.loadState.refresh is LoadState.Error
            ) {
                var msg = "Oops!!, Something went Wrong. Please Check Network Connection from your end."

                if (BuildConfig.DEBUG) {
                    msg += "\n\nDev Note: Pls check Network log. There might be News API RateLimit Issue."
                }
                viewModel?.setSnackBarStatus(msg)
            }

            if (lazyPagingNewsItem.loadState.refresh is LoadState.Loading) {
                LoadingIndicator(Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = modifier.padding(bottom = 70.dp)
                ) {
                    items(lazyPagingNewsItem.itemCount) {
                        Timber.d("lazyPagingNewsItem - $lazyPagingNewsItem")
                        lazyPagingNewsItem[it]?.let { item ->
                            NewsCardView(
                                newsItem = item,
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                    }

                    if (lazyPagingNewsItem.itemCount <= 0) {
                        item {
                            ShowNoDataView(
                                painter = painterResource(R.drawable.library_books_24),
                                contentDescription = "No News Feed",
                                text = "No News Feed"
                            )
                        }
                    }

                    item {
                        if (lazyPagingNewsItem.loadState.append is LoadState.Loading) {
                            LoadingIndicator()
                        }
                    }
                }
            }
        }
    } else {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShowNoDataView(
                modifier = Modifier,
                painter = painterResource(R.drawable.library_books_24),
                contentDescription = "No News Feed",
                text = "No News Feed"
            )
        }
    }
}
