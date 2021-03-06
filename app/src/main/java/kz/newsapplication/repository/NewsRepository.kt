package kz.newsapplication.repository

import io.reactivex.Single
import kz.newsapplication.model.News

interface NewsRepository {
    fun getTopHeadNews(page: Int, pageSize: Int): Single<List<News>>
    fun getEverything(page: Int, pageSize: Int): Single<List<News>>
    fun getFavoriteNews(): Single<List<News>>
    fun addNewsToFavorite(news: News)
    fun deleteNewsFromFavorite(news: News)
    fun getNewsByUrl(news: News): News?
}