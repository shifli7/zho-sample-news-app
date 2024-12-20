package com.example.zho.samplenewsapp.presentation.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.zho.samplenewsapp.BuildConfig
import com.example.zho.samplenewsapp.R
import com.example.zho.samplenewsapp.domain.model.NewsItem
import com.example.zho.samplenewsapp.presentation.MainViewModel
import timber.log.Timber

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NewsDetailScreen(
    modifier: Modifier = Modifier,
    newsItem: NewsItem? = null,
    uuid: String? = null,
    viewModel: MainViewModel? = null,
    navController: NavController? = null
) {
    val newsItemResult = viewModel?.newsItemByUuid?.collectAsState()

    val newsItemFromViewModel = viewModel?.holdNewsItemObj?.collectAsState()

    Timber.d("newsItemFromViewModel - $viewModel, hold - ${viewModel?.holdNewsItemObj}, " +
            "value - ${newsItemFromViewModel}")

    newsItemFromViewModel?.value?.let {
        NewsDetailView(modifier, it, navController, viewModel)
    }

    if (newsItem != null) {
        NewsDetailView(modifier, newsItem, navController, viewModel)
    }

    if (newsItemResult?.value != null) {
        newsItemResult.value?.let {
            NewsDetailView(modifier, it, navController, viewModel)
        }
    }
}

fun Context.shareList(url: String) {
    val shareIntent = Intent.createChooser(
        Intent(Intent.ACTION_SEND)
            .apply {
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
            },
        null
    )
    startActivity(shareIntent)
}

@Composable
fun TopActionView(
    modifier: Modifier = Modifier,
    navController: NavController?,
    newsItem: NewsItem? = null,
    viewModel: MainViewModel? = null,
    isBookmark : Boolean = false
) {
    val isBookmarked = remember {
        mutableStateOf(newsItem?.isBookmarked ?: false)
    }

    val context = LocalContext.current

    Row (
        modifier = modifier
            .fillMaxSize()
            .padding(top = 25.dp)
    ) {
        OutlinedButton(
            onClick = {
                navController?.popBackStack()
            },
            modifier = modifier
                .padding(top = 10.dp, start = 8.dp),
            border = null,
            contentPadding = PaddingValues(1.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back button",
                tint = Color.Black,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(
            Modifier
                .weight(3f)
                .fillMaxWidth())

        OutlinedButton(
            onClick = {
                newsItem?.let {
                    context.shareList(it.articleWebUrl)
                }
            },
            modifier = modifier
                .padding(top = 10.dp, end = 8.dp),
            border = null,
            contentPadding = PaddingValues(1.dp)
        ) {
            Icon(
                Icons.Default.Share,
                contentDescription = "share button",
                tint = Color.Black,
                modifier = Modifier
                    .size(25.dp)
            )
        }

        OutlinedButton(
            onClick = {
                isBookmarked.value = !isBookmarked.value
                if (newsItem != null) {
                    newsItem.isBookmarked = isBookmarked.value
                    Timber.d("TopActionView uuid - ${newsItem.uuid} newsItem.isBookmarked - ${newsItem.isBookmarked}")
                    //viewModel?.bookmarkNewsItem(newsItem)
                    viewModel?.updateNewsItemBookmark(newsItem.uuid, newsItem.isBookmarked )
                    viewModel?.setHoldNewsItemObj(newsItem)
                }
            },
            modifier = modifier
                .padding(top = 10.dp, end = 15.dp),
            border = null,
            contentPadding = PaddingValues(1.dp)
        ) {
            if (isBookmarked.value) {
                Icon(
                    painter = painterResource(R.drawable.bookmark_filled_24),
                    contentDescription = "Create BookMark",
                    tint = Color.Blue,
                    modifier = Modifier
                        .size(width = 40.dp, height = 40.dp)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.bookmark_border_24),
                    contentDescription = "Already Bookmarked",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun NewsDetailView(
    modifier: Modifier = Modifier,
    newsItem: NewsItem,
    navController: NavController?,
    viewModel: MainViewModel? = null
) {
//    val isBookmarked = viewModel?.isNewsItemBookmark?.collectAsState()
//    LaunchedEffect(Unit) {
//        viewModel?.getNewsItemIsBookmarked(newsItem.uuid)
//    }

    Column (
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        var contentScale by remember {
            mutableStateOf(ContentScale.FillWidth)
        }

        TopActionView(
            navController = navController,
            newsItem = newsItem,
            viewModel = viewModel,
            isBookmark = newsItem.isBookmarked
        )

        AsyncImage(
//            model = "https://static01.nyt.com/images/2024/12/17/multimedia/17DAYS-CHAST-qmhj/17DAYS-CHAST-qmhj-largeHorizontalJumbo.jpg",
            model = "${BuildConfig.IMAGE_BASEURL}${newsItem.imageUrl}",
            contentDescription = newsItem.heading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 12.dp, end = 12.dp, bottom = 10.dp)
                .size(width = 350.dp, height = 300.dp),
            contentScale = contentScale ,
            error = painterResource(R.drawable.brnews_placeholder_512px),
            onError = {
                Timber.d("onError AsyncImage")
                contentScale = ContentScale.Fit
            }
        )
        Spacer(Modifier.padding(6.dp))

        Text(
//            "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
            newsItem.snippet,
            style = TextStyle(
                fontWeight = FontWeight.Light,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 20.dp)
        )

        Spacer(Modifier.padding(10.dp))

        Text(
//            "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
            newsItem.heading,
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp)
        )

        Spacer(Modifier.padding(8.dp))

        Text(
//            "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
            newsItem.abstract,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp)
        )

        Spacer(Modifier.padding(4.dp))

        Text(
            newsItem.publishedBy,
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Italic,
                fontSize = 15.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp)
        )

        Spacer(Modifier.padding(6.dp))

        Text(
            "Published On ${newsItem.publishedOn}",
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp)
        )

        Spacer(Modifier.padding(10.dp))

        Text(
            newsItem.paragraph,
            style = TextStyle(
                lineHeight = 1.5.em,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, bottom = 40.dp)
        )

        Spacer(Modifier.padding(8.dp))
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun NewsDetailsScreenPreview() {
    val newsItem = NewsItem(
        heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
        imageUrl = "images/2024/12/19/crosswords/20wordle-review-art-1280/20wordle-review-art-1280-largeHorizontal375.jpg",
        publishedBy = "By New York Times Games",
        paragraph = "Welcome to The Wordle Review. Be warned: This page contains spoilers for today’s puzzle. Solve Wordle first, or scroll at your own risk.\n" +
                "\n" +
                "Wordle is released at midnight in your time zone. In order to accommodate all time zones, there will be two Wordle Reviews live every day, dated based on Eastern Standard Time. If you find yourself on the wrong review, check the number of your puzzle, and go to this page to find the corresponding review.",
        wordCount = 100,
        articleWebUrl = "https://www.nytimes.com/2024/12/19/crosswords/wordle-review-1280.html",
        publishedOn = "18 Nov 22",
        abstract = "On this week’s round table Michael Barbaro sits down with Maggie Haberman, Andrew Ross Sorkin and Catie Edmondson to discuss the latest news in Donald J. Trump’s transition to power.",
        snippet = "A posthumous anthology of photo essays by the curator and art historian reveals the “troubling reality” of prejudice and the power of images to “undermine the very concept of difference.”"
    )

    Scaffold {
        NewsDetailScreen(
            modifier = Modifier,
            newsItem = newsItem
        )
    }
}
