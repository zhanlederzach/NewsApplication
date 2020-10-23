package kz.newsapplication.ui.top_headlienes

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kz.newsapplication.R
import kz.newsapplication.model.News
import kz.newsapplication.utils.DateUtil
import kz.newsapplication.utils.NewsDiffUtils
import kz.newsapplication.utils.extensions.circularProgress

interface OnFavoriteClickListener {
    fun onFavoriteClick(position: Int, news: News)
}

class TopHeadlinesBaseAdapter(type: Int,
                              private val onFavoriteClickListener: OnFavoriteClickListener? = null
) : BaseQuickAdapter<News, BaseViewHolder>(
    when (type) {
        TOP_HEAD_LINE -> { R.layout.item_top_headline }
        EVERYTHING -> { R.layout.item_favorites }
        else -> { R.layout.item_favorites }
    }
) {

    companion object {
        const val TOP_HEAD_LINE = 1
        const val EVERYTHING = 2
        const val FAVORITES = 3
    }

    override fun convert(helper: BaseViewHolder, item: News) {
        if (helper.layoutPosition == 0) {
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(
                helper.itemView.context.resources.getDimension(R.dimen.margin_medium).toInt(),
                helper.itemView.context.resources.getDimension(R.dimen.margin_12dp).toInt(),
                helper.itemView.context.resources.getDimension(R.dimen.margin_medium).toInt(),
                helper.itemView.context.resources.getDimension(R.dimen.margin_12dp).toInt()
            )
            helper.getView<CardView>(R.id.cardView).layoutParams = params
        }
        helper.apply {
            getView<TextView>(R.id.tvTitle).text = item.title
            getView<TextView>(R.id.tvDescription).text = item.description
            getView<TextView>(R.id.tvDatePublished).text = DateUtil.convertIsoToDate(item.publishedAt)
            getView<ImageView>(R.id.ivFavorite).setOnClickListener {
                onFavoriteClickListener?.onFavoriteClick(adapterPosition, item)
            }
            setImageFavorite(getView(R.id.ivFavorite), item.isFavorite)
            Glide
                .with(itemView.context)
                .load(item.urlToImage)
                .placeholder(itemView.context.circularProgress())
                .error(ContextCompat.getDrawable(itemView.context, R.drawable.bg_white_rounded))
                .into(getView(R.id.ivNews))
        }
    }

    override fun replaceData(newData: MutableCollection<out News>) {
        val diffResult = DiffUtil.calculateDiff(
            NewsDiffUtils(
                newData as ArrayList<News>,
                mData as ArrayList<News>
            )
        )
        diffResult.dispatchUpdatesTo(this)
        mData = newData
        notifyDataSetChanged()
    }

    fun clearAll() {
        mData.clear()
        notifyDataSetChanged()
    }

    private fun setImageFavorite(imageView: ImageView, isLiked: Boolean) {
        if (isLiked) {
            imageView.background = ContextCompat.getDrawable(imageView.context, R.drawable.ic_star_filled)
        } else {
            imageView.background = ContextCompat.getDrawable(imageView.context, R.drawable.ic_star_border)
        }
    }
}