package kz.newsapplication.ui

import kz.newsapplication.model.News

sealed class NewsState {
    object ShowLoading : NewsState()
    object HideLoading : NewsState()
    object LoadMoreFinished : NewsState()
    data class NewsResult(val news: List<News>) : NewsState()
    data class LoadMoreResult(val news: List<News>) : NewsState()
    data class Error(val message: String?) : NewsState()
}