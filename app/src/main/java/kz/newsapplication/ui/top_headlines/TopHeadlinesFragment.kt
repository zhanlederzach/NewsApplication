package kz.newsapplication.ui.top_headlines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kz.newsapplication.R
import kz.newsapplication.model.News
import kz.newsapplication.ui.NewsViewModel
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class TopHeadlinesFragment : Fragment(), KoinComponent {

    companion object {
        fun newInstance(bundle: Bundle? = null): TopHeadlinesFragment {
            val fragment = TopHeadlinesFragment().apply {
                arguments = bundle
            }
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val newsAdapter by lazy {
        TopHeadlinesAdapter()
    }

    private val viewModel: NewsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top_headlines, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        setData()
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
    }

    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = newsAdapter
    }

    private fun setData() {
        viewModel.getTopHeadlines()
        viewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {

            }
        })
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