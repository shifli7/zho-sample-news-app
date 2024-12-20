package com.example.zho.samplenewsapp.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.zho.samplenewsapp.domain.model.NewsItem
import com.example.zho.samplenewsapp.presentation.MainViewModel
import com.example.zho.samplenewsapp.presentation.view.ConnectivityStatusView
import com.example.zho.samplenewsapp.presentation.view.SearchBarAndResultView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    newsItemList: List<NewsItem>? = null,
    navController: NavController? = null,
    viewModel: MainViewModel? = null
) {

//    val viewModel = hiltViewModel<MainViewModel>()
    val feedsLazyNewsItem = viewModel?.getNewsFromPager()?.collectAsLazyPagingItems()
    val bookmarkNewsList = viewModel?.bookmarkNewsList?.collectAsState()

    Column (
        modifier = modifier.fillMaxSize()
    ) {

        val coroutineScope = rememberCoroutineScope()
        val pageState = rememberPagerState(
            pageCount = { 2 }
        )

        Column (
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConnectivityStatusView(
                context = LocalContext.current,
                viewModel = viewModel
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .semantics { isTraversalGroup = true }
        ) {
            SearchBarAndResultView(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = 0f },
                viewModel,
                navController
            )
        }


        TabRow(
            selectedTabIndex = pageState.currentPage,
            contentColor = Color.Black,
        ) {
            Tab(
                selected = pageState.currentPage == 0,
                text = {
                    Text(text = "Feeds")
                },
                onClick = {
                    coroutineScope.launch {
                        pageState.animateScrollToPage(0)
                    }
                }
            )
            Tab(
                selected = pageState.currentPage == 1,
                text = {
                    Text(text = "Bookmarks")
                },
                onClick = {
                    coroutineScope.launch {
                        pageState.animateScrollToPage(1)
                    }
                }
            )
        }
        HorizontalPager(
            state = pageState
        ) { page ->
            if (page == 0) {

                FeedsTabScreen(
                    modifier = modifier,
                    lazyPagingNewsItem = feedsLazyNewsItem,
                    navController = navController,
                    viewModel = viewModel
                )

            } else {

                BookmarkScreen(
                    modifier = modifier,
                    bookmarkNewsItems = bookmarkNewsList?.value,
                    navController = navController,
                    viewModel = viewModel
                )

            }
        }
    }
}
