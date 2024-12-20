package com.example.zho.samplenewsapp.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.zho.samplenewsapp.domain.model.NewsItem
import com.example.zho.samplenewsapp.presentation.ui.theme.SampleNewsAppTheme
import com.example.zho.samplenewsapp.presentation.screen.HomeScreen
import com.example.zho.samplenewsapp.presentation.screen.NewsDetailScreen
import com.example.zho.samplenewsapp.presentation.view.SearchSuggestionNewsView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            val viewModel = hiltViewModel<MainViewModel>()

            val showBottomFabButton by viewModel.showBottomFabButton.collectAsState()
            val scaffoldContentColor by viewModel.backgroundColor.collectAsState()

            val hostStateSnackBar = remember { SnackbarHostState() }
            val snackBarState by viewModel.snackBarState.collectAsState()
            LaunchedEffect(!snackBarState.isNullOrEmpty()) {
                snackBarState?.let {
                    hostStateSnackBar.showSnackbar(
                        message = it,
                        actionLabel = "Dismiss"
                    )
                }
            }


            SampleNewsAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButtonPosition = FabPosition.Center,
                    containerColor = scaffoldContentColor ?: Color.White,
                    floatingActionButton = {
                        AnimatedVisibility(
                            visible = showBottomFabButton,
                            enter = slideInVertically(initialOffsetY = { it * 2 }),
                            exit = slideOutVertically(targetOffsetY = { it * 2 })
                        ) {
                            OutlinedButton(
                                onClick = viewModel::getRandomBgColor,
                                border = BorderStroke(2.dp, Color.Black),
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 30.dp, end = 30.dp)
                            ) {
                                Text("Change Background")
                            }
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(
                            hostState = hostStateSnackBar,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = HomeScreenDestination
                    ) {
                        composable<HomeScreenDestination> {
                            viewModel.toggleFabButtonState(true)
                            HomeScreen(
                                modifier = Modifier
                                    .padding(innerPadding),
                                navController = navController,
                                viewModel = viewModel
                            )
                        }

                        composable<NewsDetailScreenDestination> {
                            val args = it.toRoute<NewsDetailScreenDestination>()
                            val uuid = args.id
                            Timber.d("composable<NewsDetailScreenDestination>  - $uuid")
                            viewModel.toggleFabButtonState(false)

                            NewsDetailScreen(
                                modifier = Modifier,
                                uuid = uuid,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Serializable
object HomeScreenDestination

@Serializable
data class NewsDetailScreenDestination(val id: String)



@Preview
@Composable
fun SearchSuggestionNewsPreview() {

    SearchSuggestionNewsView(Modifier, NewsItem(
        heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
        imageUrl = "",
        publishedBy = "",
        paragraph = "",
        wordCount = 100,
        articleWebUrl = "",
        abstract = "",
        snippet = "",
        publishedOn = "18 Nov 22"
      )
    )
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

    val newItemList: List<NewsItem> = listOf(
        NewsItem(
            heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
            imageUrl = "",
            publishedBy = "",
            paragraph = "",
            wordCount = 100,
            abstract = "",
            snippet = "",
            articleWebUrl = "",
            publishedOn = "18 Nov 22"
        ),
        NewsItem(
            heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
            imageUrl = "",
            publishedBy = "",
            paragraph = "",
            wordCount = 100,
            abstract = "",
            snippet = "",
            articleWebUrl = "",
            publishedOn = "18 Nov 22"
        ),
        NewsItem(
            heading = "As Trump Looms, Biden Makes a Last-Ditch Pitch to Global Leader",
            imageUrl = "",
            publishedBy = "",
            paragraph = "",
            wordCount = 100,
            abstract = "",
            snippet = "",
            articleWebUrl = "",
            publishedOn = "18 Nov 22"
        )
    )

    SampleNewsAppTheme {
        HomeScreen(
            modifier = Modifier
                .padding(top = 40.dp),
            newItemList
        )
    }
}