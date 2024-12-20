package com.example.zho.samplenewsapp.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zho.samplenewsapp.R
import com.example.zho.samplenewsapp.domain.model.NewsItem
import com.example.zho.samplenewsapp.presentation.MainViewModel
import com.example.zho.samplenewsapp.presentation.view.NewsCardView
import com.example.zho.samplenewsapp.presentation.view.ShowNoDataView
import timber.log.Timber


@Composable
fun BookmarkScreen(
    modifier: Modifier = Modifier,
    bookmarkNewsItems: List<NewsItem>?,
    navController: NavController?,
    viewModel: MainViewModel? = null
) {
//    val viewModel = hiltViewModel<MainViewModel>()

    LaunchedEffect(Unit) {
        viewModel?.getBookmarkNewsItems()
    }

    if (!bookmarkNewsItems.isNullOrEmpty()) {
        Box {
            LazyColumn(
                modifier = modifier.padding(bottom = 70.dp)
            ) {
                items(bookmarkNewsItems.size) {
                    Timber.d("bookmarkNewsItems - $bookmarkNewsItems")
                    bookmarkNewsItems[it].let { item ->
                        NewsCardView(
                            newsItem = item,
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    } else {
        ShowNoDataView(
            painter = painterResource(R.drawable.baseline_bookmark_add_24),
            contentDescription = "Add Bookmarks",
            text = "Add Bookmarks"
        )
    }
}
