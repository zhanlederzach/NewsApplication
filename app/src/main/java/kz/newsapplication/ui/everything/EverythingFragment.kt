package kz.newsapplication.ui.everything

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
import kz.newsapplication.ui.top_headlienes.OnFavoriteClickListener
import kz.newsapplication.ui.top_headlienes.TopHeadlinesBaseAdapter
import kz.newsapplication.ui.top_headlienes.top_headliens_details.TopHeadliensDetailsFragment
import kz.newsapplication.utils.Constants
import kz.newsapplication.utils.Likeable
import kz.newsapplication.utils.Screen
import kz.newsapplication.utils.extensions.showTopToast
import kz.newsapplication.utils.widgets.AppLoadMoreView
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.ArrayList

class EverythingFragment : Fragment(), KoinComponent, Likeable {

    companion object {
        fun newInstance(bundle: Bundle? = null): EverythingFragment {
            val fragment = EverythingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val viewModel: NewsViewModel by inject()
    private var lastPressedPosition: Int = -1

    private val onFavoriteClickListener: OnFavoriteClickListener = object :
        OnFavoriteClickListener {
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

    private val newsAdapter by lazy {
        TopHeadlinesBaseAdapter(onFavoriteClickListener = onFavoriteClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_everything, container, false)
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

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            newsAdapter.clearAll()
            newsAdapter.setNewData(ArrayList())
            viewModel.getEverything(isRefresh = true)
        }
    }

    private fun setAdapter() {
        newsAdapter.apply {
            setEnableLoadMore(true)
            setOnItemClickListener { adapter, view, position ->
                (requireActivity() as MainActivity).navigateTo(
                    fragment = TopHeadliensDetailsFragment.newInstance(
                        bundleOf(
                            Constants.NEWS to adapter.data[position] as News
                        )
                    ),
                    addToStack = true,
                    tag = Screen.TOP_HEADLINES_DETAILS.name
                )
                lastPressedPosition = position
            }
            setEnableLoadMore(true)
            setLoadMoreView(AppLoadMoreView())
            setOnLoadMoreListener({
                viewModel.getEverything()
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
        viewModel.getEverything()
        viewModel.liveDataEverything.observe(viewLifecycleOwner, Observer { result ->
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
    }

    override fun setLike(isFavorite: Boolean) {
        val position = lastPressedPosition
        if (newsAdapter.data.isNotEmpty() && position >= 0 &&
            position < newsAdapter.data.size
        ) {
            newsAdapter.data[position]?.isFavorite = isFavorite
            newsAdapter.notifyItemChanged(position)
        }
        lastPressedPosition = -1
    }
}
