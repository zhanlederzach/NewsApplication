package kz.newsapplication.model

import com.google.gson.annotations.SerializedName

data class News(
//    @SerializedName("source") val source: Source,
//    @SerializedName("author") val author: String,
    @SerializedName("title") val title: String
//    @SerializedName("description") val description: String
)