package com.example.zho.samplenewsapp.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.zho.samplenewsapp.BuildConfig
import com.example.zho.samplenewsapp.R
import com.example.zho.samplenewsapp.domain.model.NewsItem
import com.example.zho.samplenewsapp.presentation.MainViewModel
import com.example.zho.samplenewsapp.presentation.NewsDetailScreenDestination
import timber.log.Timber


@Composable
fun NewsCardView(
    newsItem: NewsItem,
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    viewModel: MainViewModel? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 7.dp, bottom = 7.dp)
            .clickable {
                Timber.d("NewCardView holdNewsItem - $newsItem")
                viewModel?.setHoldNewsItemObj(newsItem)
                navController?.navigate(
                    NewsDetailScreenDestination(
                        newsItem.uuid
                    )
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        var contentScale by remember {
            mutableStateOf(ContentScale.FillWidth)
        }

        AsyncImage(
//            model = "https://static01.nyt.com/images/2024/12/17/multimedia/17DAYS-CHAST-qmhj/17DAYS-CHAST-qmhj-largeHorizontalJumbo.jpg",
            model = "${BuildConfig.IMAGE_BASEURL}${newsItem.imageUrl}",
            contentDescription = newsItem.heading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 12.dp, end = 12.dp, bottom = 10.dp)
                .size(width = 350.dp, height = 200.dp),
            contentScale = contentScale ,
            error = painterResource(R.drawable.brnews_placeholder_512px),
            onError = {
                Timber.d("onError AsyncImage")
                contentScale = ContentScale.Fit
            }
        )
        Spacer(Modifier.padding(3.dp))

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
        Spacer(Modifier.padding(4.dp))

        Text(
            "Published On ${newsItem.publishedOn}",
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp)
        )
        Spacer(Modifier.padding(8.dp))

    }
}