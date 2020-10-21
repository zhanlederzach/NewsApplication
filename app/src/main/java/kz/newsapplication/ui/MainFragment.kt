package kz.newsapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.mobile.telecomapp.utils.NavigationAnimation
import kz.newsapplication.R
import kz.newsapplication.ui.everything.EverythingFragment
import kz.newsapplication.ui.favorites.FavoritesFragment
import kz.newsapplication.ui.top_headlines.TopHeadlinesFragment
import kz.newsapplication.utils.BasePagerAdapter
import kz.newsapplication.utils.Screen

class MainFragment: Fragment() {

    companion object {
        fun newInstance(data: Bundle? = null): MainFragment =
            MainFragment().apply {
                arguments = data
            }
    }

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var ivFavorite: ImageView
    private lateinit var mainPagerAdapter: BasePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
    }

    private fun bindViews(view: View) = with(view) {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        ivFavorite = findViewById(R.id.ivFavorite)

        ivFavorite.setOnClickListener {
            (requireActivity() as MainActivity).navigateTo(
                fragment = FavoritesFragment.newInstance(),
                addToStack = true,
                tag = Screen.FAVORITES.name,
                animation = NavigationAnimation.SLIDE
            )
        }
    }

    private fun setAdapter() {
        val fragments: List<Fragment> = listOf(
            TopHeadlinesFragment.newInstance(),
            EverythingFragment.newInstance()
        )
        val titles = listOf(
            getString(R.string.top_headlines),
            getString(R.string.everything)
        )
        mainPagerAdapter = BasePagerAdapter(childFragmentManager, fragments, titles)
        viewPager.adapter = mainPagerAdapter
        viewPager.offscreenPageLimit = fragments.size
        tabLayout.setupWithViewPager(viewPager)
    }
}