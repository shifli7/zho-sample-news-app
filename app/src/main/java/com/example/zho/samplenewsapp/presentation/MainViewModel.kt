package com.example.zho.samplenewsapp.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.zho.samplenewsapp.common.model.Result
import com.example.zho.samplenewsapp.domain.model.NewsItem
import com.example.zho.samplenewsapp.domain.repo.NewsRepo
import com.example.zho.samplenewsapp.presentation.ui.theme.NewsBgColorList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val newsRepo: NewsRepo
) : ViewModel() {

    init {
        // makeApiCall()
    }

    private var _newsItemList: MutableStateFlow<List<NewsItem>> = MutableStateFlow(emptyList())
    val newsItemList = _newsItemList.asStateFlow()

    private var _searchNewsItemList: MutableStateFlow<List<NewsItem>> = MutableStateFlow(emptyList())
    val searchNewsItemList = _searchNewsItemList.asStateFlow()

    private var _bookmarkNewsList: MutableStateFlow<List<NewsItem>> = MutableStateFlow(emptyList())
    val bookmarkNewsList = _bookmarkNewsList.asStateFlow()

    private var _showBottomFabButton = MutableStateFlow(true)
    var showBottomFabButton = _showBottomFabButton

    private var _snackBarState: MutableStateFlow<String?> = MutableStateFlow(null)
    var snackBarState = _snackBarState

    var isSearching = mutableStateOf(false)

    private var _newsSearchSate: MutableStateFlow<NewsSearchSate?> = MutableStateFlow(null)
    val newsSearchSate = _newsSearchSate.asStateFlow()

    private var _newsItemByUuid: MutableStateFlow<NewsItem?> = MutableStateFlow(null)
    val newsItemByUuid = _newsItemByUuid.asStateFlow()

    private val _backgroundColor: MutableStateFlow<Color?> = MutableStateFlow(null)
    val backgroundColor = _backgroundColor.asStateFlow()

    private val _holdNewsItemObj: MutableStateFlow<NewsItem?> = MutableStateFlow(null)
    val holdNewsItemObj = _holdNewsItemObj.asStateFlow()

    private val _isNewsItemBookmark = MutableStateFlow(false)
    val isNewsItemBookmark = _isNewsItemBookmark.asStateFlow()


    fun setHoldNewsItemObj(newsItem: NewsItem) {
        _holdNewsItemObj.value = newsItem
    }

    fun toggleFabButtonState(value: Boolean) {
        _showBottomFabButton.value = value
    }

    fun setSnackBarStatus(msg: String? = null) {
        _snackBarState.value = msg
    }

    fun getRandomBgColor() {
        val colors: List<Color> = NewsBgColorList
        val ind = colors.indices.random()
        _backgroundColor.value = colors[ind]
    }

    fun makeApiCall() {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepo.getNewsByPage(1).collectLatest { result ->
                Timber.d("getNewsByPage response = $result, data - ${result.data}")
                when (result) {
                    is Result.Success -> {
                        result.data?.let {
                            _newsItemList.value = it
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun searchNews(queryText: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, e ->
            Timber.d("News searchNews exception")
            Timber.d("EXCEPTION OCCURRED message - ${e.message}")

            if(e is UnknownHostException){
                setSnackBarStatus("Oops!!, Please Check Network Connectivity")
            }
            _newsSearchSate.value = NewsSearchSate.Error
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            isSearching.value = true
            _newsSearchSate.value = NewsSearchSate.Loading
            newsRepo.getNewsBySearch(queryText)
                .debounce(100L)
                .collectLatest { result ->
                Timber.d("searchNews response = $result, data - ${result.data}")
                when (result) {
                    is Result.Success -> {
                        isSearching.value = false
                        result.data?.let {
                            _searchNewsItemList.value = it
                            _newsSearchSate.value = NewsSearchSate.Success
                        }
                    }
                    else -> {
                        isSearching.value = false
                        _newsSearchSate.value = NewsSearchSate.Error
                    }
                }
            }
        }
    }

    fun getNewsFromPager(): Flow<PagingData<NewsItem>> {
        return newsRepo.getNewsFromPager().cachedIn(viewModelScope)
    }

    fun getBookmarkNewsItems() {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepo.getBookMarkNewsItems().collectLatest {
                Timber.d("getBookmarkNewsItems value - $it")
                _bookmarkNewsList.value = it
            }
        }
    }

    fun getNewsItemsByUuid(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepo.getNewsItemByUuid(uuid).collectLatest {
                _newsItemByUuid.value = it
            }
        }
    }

    fun bookmarkNewsItem(newsItem: NewsItem) {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepo.bookmarkNewsItem(newsItem)
        }
    }

    fun updateNewsItemBookmark(uuid: String, isBookmark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepo.updateNewsItemBookmark(uuid, isBookmark)
        }
    }

    fun getNewsItemIsBookmarked(uuid: String) {
        viewModelScope.launch {
            newsRepo.getNewsItemIsBookmarked(uuid).collectLatest {
                _isNewsItemBookmark.value = it
            }
        }
    }
}

sealed interface NewsSearchSate {
    data object Loading : NewsSearchSate
    data object Success : NewsSearchSate
    data object Error : NewsSearchSate
}