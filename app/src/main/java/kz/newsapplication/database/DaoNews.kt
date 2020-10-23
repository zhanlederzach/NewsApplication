package kz.newsapplication.database

import androidx.room.*
import io.reactivex.Single
import kz.newsapplication.model.News

@Dao
interface DaoNews {

    @Query("SELECT * FROM news")
    fun getFavoriteNews(): Single<List<News>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(news: News)

    @Delete
    fun deleteNews(news: News)

    @Query("SELECT * FROM news WHERE url = :url")
    fun getNewsById(url: String): News?
}