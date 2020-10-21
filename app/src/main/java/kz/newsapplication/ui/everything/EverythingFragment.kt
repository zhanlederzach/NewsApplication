package kz.newsapplication.ui.everything

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.newsapplication.R
import kz.newsapplication.model.News
import kz.newsapplication.ui.top_headlines.TopHeadlinesAdapter
import org.koin.core.KoinComponent

class EverythingFragment : Fragment(), KoinComponent {

    companion object {
        fun newInstance(bundle: Bundle? = null): EverythingFragment {
            val fragment = EverythingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView

    private val newsAdapter by lazy {
        TopHeadlinesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_everything, container, false)
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = newsAdapter
    }

    private fun setData() {
        newsAdapter.addItems(
            generateFakeNews()
        )
    }

    private fun generateFakeNews(): ArrayList<News> {
        val listOfNews: ArrayList<News> = arrayListOf()
        listOfNews.apply {
            add(News("Hello"))
            add(News("Hello2"))
            add(News("Hello3"))
            add(News("Hello4"))
            add(News("Hello5"))
        }
        return listOfNews
    }
}