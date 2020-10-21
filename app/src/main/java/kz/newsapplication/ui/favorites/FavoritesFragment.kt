package kz.newsapplication.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.newsapplication.R
import kz.newsapplication.model.News
import kz.newsapplication.ui.top_headlines.TopHeadlinesAdapter
import org.koin.core.KoinComponent

class FavoritesFragment : Fragment(), KoinComponent {

    companion object {
        fun newInstance(bundle: Bundle? = null): FavoritesFragment {
            val fragment = FavoritesFragment().apply {
                arguments = bundle
            }
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    private val newsAdapter by lazy {
        TopHeadlinesAdapter()
    }

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

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
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
            add(News("Favorites"))
            add(News("Favorites2"))
            add(News("Favorites3"))
            add(News("Favorites4"))
            add(News("Favorites5"))
        }
        return listOfNews
    }
}