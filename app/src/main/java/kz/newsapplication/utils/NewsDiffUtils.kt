package kz.newsapplication.utils

import android.os.Bundle

import androidx.recyclerview.widget.DiffUtil
import kz.newsapplication.model.News

class NewsDiffUtils(val newList: ArrayList<News>?, val oldList: ArrayList<News>?) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return if (oldList != null) oldList!!.size else 0
    }

    override fun getNewListSize(): Int {
        return if (newList != null) oldList!!.size else 0
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return newList!![newItemPosition].url === oldList!![oldItemPosition].url
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return newList!![newItemPosition] == oldList!![oldItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val newModel: News = newList!![newItemPosition]
        val oldModel: News = oldList!![oldItemPosition]
        val diff = Bundle()
        if (newModel.url !== oldModel.url) {
            diff.putString("url", newModel.url)
        }
        return if (diff.size() == 0) {
            null
        } else diff
    }
}