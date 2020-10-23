package kz.newsapplication.ui.top_headlienes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kz.newsapplication.R
import kz.newsapplication.model.News
import kz.newsapplication.utils.DateUtil
import kz.newsapplication.utils.extensions.circularProgress

class TopHeadliensAdapter : RecyclerView.Adapter<TopHeadliensAdapter.BaseViewHolder<News>>() {

    private var news: ArrayList<News> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<News> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_top_headline, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<News>, position: Int) {
        val element = news[position]
        when (holder) {
            is NewsViewHolder -> holder.bind(element)
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }

    fun addItems(news: ArrayList<News>) {
        this.news = news
    }

    inner class NewsViewHolder(itemView: View) : BaseViewHolder<News>(itemView) {

        private var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private var tvDatePublished: TextView = itemView.findViewById(R.id.tvDatePublished)
        private var cardView: CardView = itemView.findViewById(R.id.cardView)
        private var ivNews: ImageView = itemView.findViewById(R.id.ivNews)

        override fun bind(item: News) {
            if (adapterPosition == 0) {
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(
                    itemView.context.resources.getDimension(R.dimen.margin_medium).toInt(),
                    itemView.context.resources.getDimension(R.dimen.margin_12dp).toInt(),
                    itemView.context.resources.getDimension(R.dimen.margin_medium).toInt(),
                    itemView.context.resources.getDimension(R.dimen.margin_12dp).toInt()
                )
                cardView.layoutParams = params
            }
            tvTitle.text = item.title
            tvDescription.text = item.description
            tvDatePublished.text = item.publishedAt
            Glide
                .with(itemView.context)
                .load(item.urlToImage)
                .placeholder(itemView.context.circularProgress())
                .error(itemView.context.circularProgress())
                .into(ivNews)
            tvDatePublished.text = DateUtil.convertIsoToDate(item.publishedAt)
        }
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }
}