package kz.newsapplication.repository

import com.google.gson.Gson
import io.reactivex.Single
import kz.newsapplication.api.NewsApi
import kz.newsapplication.model.News

class NewsRepositoryImpl(
    private val newsApi: NewsApi,
    private val gson: Gson
) : NewsRepository {

    override fun getTopHeadNews(page: Int, pageSize: Int): Single<List<News>> {
        return newsApi.getTopHeadlines(
            page = page,
            pageSize = pageSize
        ).flatMap { response ->
            if (response.isSuccessful) {
                Single.just(response.body()?.articles)
            } else {
                Single.error(Exception(""))
            }
        }
    }

    override fun getEverything(page: Int, pageSize: Int): Single<List<News>> {
        return newsApi.getEverything(
            page = page,
            pageSize = pageSize
        ).flatMap { response ->
            if (response.isSuccessful) {
                Single.just(response.body()?.articles)
            } else {
                Single.error(Exception(""))
            }
        }
    }

//    override fun getFavoriteNews(): Single<List<News>> {
//        TODO("Not yet implemented")
//    }

}