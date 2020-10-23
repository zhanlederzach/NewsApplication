package kz.newsapplication.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
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
import kz.newsapplication.ui.top_headlienes.TopHeadlinesBaseAdapter.Companion.FAVORITES
import kz.newsapplication.ui.top_headlienes.top_headliens_details.TopHeadliensDetailsFragment
import kz.newsapplication.utils.Constants
import kz.newsapplication.utils.Likeable
import kz.newsapplication.utils.Screen
import kz.newsapplication.utils.extensions.showTopToast
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class FavoritesFragment : Fragment(), KoinComponent, Likeable {

    companion object {
        fun newInstance(bundle: Bundle? = null): FavoritesFragment {
            return FavoritesFragment().apply {
                arguments = bundle
            }
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var lastPressedPosition: Int = -1

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

    private val emptyView by lazy {
        layoutInflater.inflate(R.layout.view_empty_list, null)
    }

    private val newsAdapter by lazy {
        TopHeadlinesBaseAdapter(type = FAVORITES, onFavoriteClickListener = onFavoriteClickListener)
    }

    private val viewModel: NewsViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        setData()
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.recyclerView)
        toolbar = findViewById(R.id.toolbar)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        swipeRefreshLayout.setOnRefreshListener {
            newsAdapter.setNewData(ArrayList())
            viewModel.getFavoriteNews()
        }
    }

    private fun setAdapter() {
        newsAdapter.apply {
            setOnItemClickListener { adapter, _, position ->
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
        }
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.adapter = newsAdapter
    }

    private fun setData() {
        viewModel.getFavoriteNews()
        viewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is NewsState.NewsResult -> {
                    if (!result.news.isNullOrEmpty()) {
                        newsAdapter.replaceData(result.news as MutableCollection<out News>)
                    } else {
                        newsAdapter.emptyView = emptyView
                    }
                }
                is NewsState.Error -> {
                    requireActivity().showTopToast(result.message)
                }
                is NewsState.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is NewsState.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
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
