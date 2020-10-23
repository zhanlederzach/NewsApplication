package kz.newsapplication.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "news", primaryKeys = ["url"])
data class News(
    @SerializedName("author") val author: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val urlToImage: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("title") val title: String,
    var isFavorite: Boolean = false
) : Serializable, Comparable<Any> {

    override fun compareTo(other: Any): Int {
        if (other !is News) return 1
        if (other.url == url && other.author == author &&
                other.publishedAt == publishedAt) return 0
        return 1
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is News) return false
        if (other.url == url && other.author == author &&
                other.publishedAt == publishedAt &&
                other.title == title) return true
        return super.equals(other)
    }
}