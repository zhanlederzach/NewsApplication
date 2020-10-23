package kz.newsapplication.repository

import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import kz.newsapplication.api.NewsApi
import kz.newsapplication.database.DaoNews
import kz.newsapplication.model.ErrorResponse
import kz.newsapplication.model.News

class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val daoNews: DaoNews,
    private val gson: Gson
) : NewsRepository {

    companion object {
        const val TAG = "news_repo_impl"
    }

    override fun getTopHeadNews(page: Int, pageSize: Int): Single<List<News>> {
        return daoNews.getFavoriteNews().flatMap { favoriteList ->
            newsApi.getTopHeadlines(
                page = page,
                pageSize = pageSize
            ).flatMap { response ->
                if (response.isSuccessful) {
                    response.body()?.articles?.map { news ->
                        if (favoriteList.contains(news)) {
                            news.isFavorite = true
                        }
                    }
                    Single.just(response.body()?.articles)
                } else {
                    if (response.code() == 429) {
                        val errorResponse = gson.fromJson(
                            response.errorBody()?.string(), ErrorResponse::class.java
                        )
                        Single.error(Throwable(errorResponse.message))
                    } else {
                        Single.error(Exception(""))
                    }
                }.doOnError {
                    Log.d(TAG, it.message.toString());
                }
            }
        }
    }

    override fun getEverything(page: Int, pageSize: Int): Single<List<News>> {
        return daoNews.getFavoriteNews().flatMap { favoriteList ->
            newsApi.getEverything(
                page = page,
                pageSize = pageSize
            ).flatMap { response ->
                if (response.isSuccessful) {
                    response.body()?.articles?.map { news ->
                        if (favoriteList.contains(news)) {
                            news.isFavorite = true
                        }
                    }
                    Single.just(response.body()?.articles)
                } else {
                    if (response.code() == 429) {
                        val errorResponse =
                            gson.fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                        Single.error(Throwable(errorResponse.message))
                    } else {
                        Single.error(Exception(""))
                    }
                }.doOnError {
                    Log.d(TAG, it.message.toString())
                }
            }
        }
    }

    override fun getFavoriteNews(): Single<List<News>> {
        return daoNews.getFavoriteNews()
    }

    override fun addNewsToFavorite(news: News) {
        daoNews.insertNews(news)
    }

    override fun deleteNewsFromFavorite(news: News) {
        daoNews.deleteNews(news)
    }

    override fun getNewsByUrl(news: News): News? {
        if (news.url.isEmpty()) return null
        return daoNews.getNewsById(news.url)
    }

}