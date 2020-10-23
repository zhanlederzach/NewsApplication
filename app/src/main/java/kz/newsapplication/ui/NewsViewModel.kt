package kz.newsapplication.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kz.newsapplication.di.applySchedulersFlowable
import kz.newsapplication.di.applySchedulersSingle
import kz.newsapplication.model.News
import kz.newsapplication.repository.NewsRepository
import kz.newsapplication.utils.Constants
import kz.newsapplication.utils.base.BaseViewModel
import java.util.concurrent.TimeUnit

class NewsViewModel(
    private val newsRepository: NewsRepository
) : BaseViewModel() {

    companion object {
        const val TAG = "top_head_view_model"
    }

    val liveData by lazy {
        MutableLiveData<NewsState>()
    }

    val liveDataPeriodic by lazy {
        MutableLiveData<NewsState>()
    }

    private var currentPage = Constants.DEFAULT_PAGE

    val liveDataEverything by lazy {
        MutableLiveData<NewsState>()
    }

    private var currentPageEverything = Constants.DEFAULT_PAGE

    fun getEverything(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPageEverything = Constants.DEFAULT_PAGE
        }
        disposables.add(
            newsRepository.getEverything(page = currentPageEverything, pageSize = Constants.PAGE_LIMIT)
                .compose(applySchedulersSingle())
                .doOnSubscribe {
                    if (currentPageEverything == Constants.DEFAULT_PAGE) {
                        liveDataEverything.value = NewsState.ShowLoading
                    }
                }
                .doFinally { liveDataEverything.value = NewsState.HideLoading }
                .subscribe({
                    if (currentPageEverything == Constants.DEFAULT_PAGE) {
                        liveDataEverything.value = NewsState.NewsResult(it)
                    } else {
                        if (it.isEmpty()) {
                            liveDataEverything.value = NewsState.LoadMoreFinished
                        } else {
                            liveData.value = NewsState.LoadMoreResult(it)
                        }
                    }
                    currentPageEverything++
                }, { error ->
                    liveDataEverything.value = NewsState.Error(error.message)
                    liveDataEverything.value = NewsState.LoadMoreFinished
                })
        )
    }

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
                .doFinally {
                    liveData.value = NewsState.HideLoading
                }
                .subscribe({
                    Log.d(TAG, it.toString());
                    if (currentPage == Constants.DEFAULT_PAGE) {
                        liveData.value = NewsState.NewsResult(it)
                    } else {
                        if (it.isEmpty()) {
                            liveData.value = NewsState.LoadMoreFinished
                        } else {
                            liveData.value = NewsState.LoadMoreResult(it)
                        }
                    }
                    currentPage++
                }, { error ->
                    Log.d(TAG, error.message.toString())
                    liveData.value = NewsState.Error(error.message)
                    liveData.value = NewsState.LoadMoreFinished
                })
        )
    }

    fun getTopHeadlinesPeriodic() {
        disposables.add(
            Flowable.interval(5000, 20000, TimeUnit.MILLISECONDS, Schedulers.io())
                .flatMap {
                    newsRepository.getTopHeadNews(
                        page = Constants.DEFAULT_PAGE,
                        pageSize = Constants.PAGE_LIMIT * (currentPage - 1)
                    ).toFlowable()
                }
                .onBackpressureDrop()
                .compose(applySchedulersFlowable())
                .subscribe(
                    { result ->
                        liveDataPeriodic.value = NewsState.NewsResult(result)
                    },
                    { error ->
                        Log.d(TAG, error.message.toString());
                        liveDataPeriodic.value = NewsState.Error(error.message)
                    }
                )
        )
    }

    fun addToFavorite(news: News?) {
        if (news == null) return
        newsRepository.addNewsToFavorite(news)
    }

    fun deleteFromFavorites(news: News?) {
        if (news == null) return
        newsRepository.deleteNewsFromFavorite(news)
    }

    fun getNewsByUrl(news: News?): News? {
        if (news == null) return null
        return newsRepository.getNewsByUrl(news)
    }

    fun getFavoriteNews() {
        disposables.add(
            newsRepository.getFavoriteNews()
                .compose(applySchedulersSingle())
                .doOnSubscribe { liveData.value = NewsState.ShowLoading }
                .doFinally { liveData.value = NewsState.HideLoading }
                .subscribe({
                    liveData.value = NewsState.NewsResult(it)
                }, { error ->
                    liveData.value = NewsState.Error(error.message ?: "")
                })
        )
    }
}