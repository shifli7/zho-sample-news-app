package com.example.zho.samplenewsapp.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.zho.samplenewsapp.R
import com.example.zho.samplenewsapp.presentation.MainViewModel
import com.example.zho.samplenewsapp.presentation.NewsSearchSate
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarAndResultView(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel?,
    navController: NavController?
) {
//    val viewModel = hiltViewModel<MainViewModel>()

    val newsSearchState = viewModel?.newsSearchSate?.collectAsState()
    val searchNewsItemList = viewModel?.searchNewsItemList?.collectAsState()

    val textFieldState = rememberTextFieldState()
    var queryText by rememberSaveable { mutableStateOf("") }
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = modifier
            .background(Color.White)
            .fillMaxWidth()
    ) {
        SearchBar(
            modifier = modifier,
            inputField = {
                SearchBarDefaults.InputField(
                    query = queryText,
                    expanded = isExpanded,
                    onSearch = {
                        viewModel?.searchNews(queryText)
                    },
                    onExpandedChange = {
                        Timber.d("onExpandedChange - $isExpanded")
                        isExpanded = true
                    },
                    onQueryChange = { queryText = it },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (isExpanded) {
                            Icon(
                                Icons.Default.Clear, contentDescription = null,
                                modifier = Modifier.clickable {
                                    if (queryText.isEmpty()) {
                                        isExpanded = false
                                    }
                                    queryText = ""
                                }
                            )
                        }
                    },
                    placeholder = {
                        Text("Search News & Bookmarks")
                    }
                )
            },
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
        ) {

            when(newsSearchState?.value) {
                is NewsSearchSate.Loading -> {
                    LoadingIndicator(
                        modifier = Modifier
                            .padding(top = 50.dp)
                    )
                }
                is NewsSearchSate.Error -> {
                    ShowNoDataView(
                        painter = painterResource(R.drawable.baseline_error_outline_24),
                        contentDescription = "Oops!!, Something Went Wrong",
                        text = "Oops!!, Something Went Wrong"
                    )
                    viewModel?.setSnackBarStatus("Oops!!, Something went Wrong. Please Check Network Connection")
                }
                is NewsSearchSate.Success -> {
                    if (searchNewsItemList != null) {
                        LazyColumn {
                            items(searchNewsItemList.value.size) {
                                SearchSuggestionNewsView(
                                    newsItem = searchNewsItemList.value[it],
                                    navController = navController,
                                    viewModel = viewModel
                                )
                            }

                            if (searchNewsItemList.value.isEmpty() && queryText.isNotEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
//                                Text(
//                                    "No Data Found for $queryText",
//                                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                                )
                                    }
                                }
                            }
                        }
                    } else {
                        ShowNoDataView()
                    }
                }
                else -> {
                    ShowNoDataView()
                }
            }
        }
    }
}
