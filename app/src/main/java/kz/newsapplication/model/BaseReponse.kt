package kz.newsapplication.model

import com.google.gson.annotations.SerializedName

class BaseResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int?,
    @SerializedName("articles") val articles: T?
)