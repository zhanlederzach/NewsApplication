package kz.newsapplication.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kz.newsapplication.di.applySchedulersSingle
import kz.newsapplication.repository.NewsRepository
import kz.newsapplication.utils.Constants
import kz.newsapplication.utils.base.BaseViewModel

class NewsViewModel(
    private val newsRepository: NewsRepository
) : BaseViewModel() {

    val liveData by lazy {
        MutableLiveData<NewsState>()
    }

    private var currentPage = Constants.DEFAULT_PAGE

    fun getTopHeadlines(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = Constants.DEFAULT_PAGE
        }
        disposables.add(
            newsRepository.getTopHeadNews(page = currentPage, pageSize = Constants.PAGE_LIMIT)
                .compose(applySchedulersSingle())
                .doOnSubscribe {
                    if (currentPage == Constants.DEFAULT_PAGE) {
                        liveData.value = NewsState.ShowLoading
                    }
                }
                .doFinally { liveData.value = NewsState.HideLoading }
                .subscribe({
                    Log.d("news_view_model", it.toString());
                    if (currentPage == Constants.DEFAULT_PAGE) {
                        liveData.value = NewsState.NewsResult(it)
                    } else {
                        liveData.value = NewsState.LoadMoreResult(it)
                        if (it.isEmpty()) {
                            liveData.value = NewsState.LoadMoreFinished
                        }
                    }
                    currentPage++
                }, { error ->
                    Log.d("news_view_model", error.message);
                    liveData.value = NewsState.Error(error.message ?: "")
                    liveData.value = NewsState.LoadMoreFinished
                })
        )
    }
}