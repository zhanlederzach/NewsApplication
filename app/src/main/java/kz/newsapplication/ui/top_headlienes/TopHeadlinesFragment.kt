package kz.newsapplication.ui.top_headlienes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kz.newsapplication.R
import kz.newsapplication.model.News
import kz.newsapplication.ui.MainActivity
import kz.newsapplication.ui.NewsState
import kz.newsapplication.ui.NewsViewModel
import kz.newsapplication.ui.top_headlienes.TopHeadlinesBaseAdapter.Companion.TOP_HEAD_LINE
import kz.newsapplication.ui.top_headlienes.top_headliens_details.TopHeadliensDetailsFragment
import kz.newsapplication.utils.Constants
import kz.newsapplication.utils.Screen
import kz.newsapplication.utils.extensions.showTopToast
import kz.newsapplication.utils.widgets.AppLoadMoreView
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent
import java.util.*

class TopHeadlinesFragment : Fragment(), KoinComponent {

    companion object {
        fun newInstance(bundle: Bundle? = null): TopHeadlinesFragment {
            return TopHeadlinesFragment().apply {
                arguments = bundle
            }
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val newsAdapter by lazy {
        TopHeadlinesBaseAdapter(type = TOP_HEAD_LINE, onFavoriteClickListener = onFavoriteClickListener)
    }

    private val viewModel: NewsViewModel by inject()

    private val onFavoriteClickListener: OnFavoriteClickListener = object : OnFavoriteClickListener {
        override fun onFavoriteClick(position: Int, news: News) {
            if (news.isFavorite) {
                news.isFavorite = false
                viewModel.deleteFromFavorites(news)
            } else {
                news.isFavorite = true
                viewModel.addToFavorite(news)
            }
            newsAdapter.notifyItemChanged(position)
        }
    }

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

        swipeRefreshLayout.setOnRefreshListener {
            newsAdapter.clearAll()
            newsAdapter.setNewData(ArrayList())
            viewModel.getTopHeadlines(isRefresh = true)
        }
    }

    private fun setAdapter() {
        newsAdapter.apply {
            setOnItemClickListener { adapter, _, position ->
                (requireActivity() as MainActivity).navigateTo(
                    addToStack = true,
                    tag = Screen.TOP_HEADLINES_DETAILS.name,
                    fragment = TopHeadliensDetailsFragment.newInstance(
                        bundleOf(
                            Constants.NEWS to (adapter as TopHeadlinesBaseAdapter).getItem(position)
                        )
                    )
                )
            }
            setEnableLoadMore(true)
            setLoadMoreView(AppLoadMoreView())
            setOnLoadMoreListener({
                viewModel.getTopHeadlines()
            }, recyclerView)
            setHasStableIds(true)
        }
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.adapter = newsAdapter
    }

    private fun setData() {
        viewModel.getTopHeadlines()
        viewModel.getTopHeadlinesPeriodic()
        viewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is NewsState.Error -> {
                    requireActivity().showTopToast(result.message)
                }
                is NewsState.NewsResult -> {
                    if (!result.news.isNullOrEmpty()) {
                        newsAdapter.replaceData(result.news as MutableCollection<out News>)
                    }
                }
                is NewsState.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is NewsState.LoadMoreFinished -> {
                    newsAdapter.apply {
                        loadMoreComplete()
                        loadMoreEnd(false)
                    }
                }
                is NewsState.LoadMoreResult -> {
                    newsAdapter.apply {
                        loadMoreComplete()
                        addData(result.news as MutableCollection<out News>)
                    }
                }
                is NewsState.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
            }
        })
        viewModel.liveDataPeriodic.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is NewsState.NewsResult -> {
                    if (!result.news.isNullOrEmpty()) {
                        newsAdapter.replaceData(result.news as MutableCollection<out News>)
                    }
                }
                is NewsState.Error -> { }
            }
        })
    }
}
