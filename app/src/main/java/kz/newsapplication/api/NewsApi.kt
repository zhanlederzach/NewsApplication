package kz.newsapplication.api

import io.reactivex.Single
import kz.newsapplication.model.BaseResponse
import kz.newsapplication.model.News
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String? = "us",
        @Query("apiKey") apiKey: String = "e65ee0938a2a43ebb15923b48faed18d",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Single<Response<BaseResponse<List<News>>>>

    @GET("everything")
    fun getEverything(
        @Query("q") q: String? = "apple",
        @Query("apiKey") apiKey: String = "e65ee0938a2a43ebb15923b48faed18d",
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Single<Response<BaseResponse<List<News>>>>
}