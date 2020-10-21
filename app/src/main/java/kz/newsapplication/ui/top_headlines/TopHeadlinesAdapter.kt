package kz.newsapplication.ui.top_headlines

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kz.newsapplication.R
import kz.newsapplication.model.News

class TopHeadlinesAdapter : RecyclerView.Adapter<TopHeadlinesAdapter.BaseViewHolder<News>>() {

    private var news: ArrayList<News> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<News> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
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

        override fun bind(item: News) {
            tvTitle.text = item.title
        }
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }
}