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
import kz.newsapplication.ui.top_headlienes.TopHeadlinesFragment
import kz.newsapplication.utils.BasePagerAdapter
import kz.newsapplication.utils.Screen

class MainFragment: Fragment() {

    companion object {
        fun newInstance(data: Bundle? = null): MainFragment =
            MainFragment().apply {
                arguments = data
            }

        const val TOP_HEAD_LINES = 0
        const val EVERYTHING = 1
    }

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var ivFavorite: ImageView
    private lateinit var mainPagerAdapter: BasePagerAdapter

    val fragments: List<Fragment> = listOf(
        TopHeadlinesFragment.newInstance(),
        EverythingFragment.newInstance()
    )

    var currentPage = 0

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
        val titles = listOf(
            getString(R.string.top_headlines),
            getString(R.string.everything)
        )
        mainPagerAdapter = BasePagerAdapter(childFragmentManager, fragments, titles)
        viewPager.adapter = mainPagerAdapter
        viewPager.offscreenPageLimit = fragments.size
        tabLayout.setupWithViewPager(viewPager)
        reduceMarginsInTabs(tabLayout, 28)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                currentPage = position
            }
        })
    }

    private fun reduceMarginsInTabs(tabLayout: TabLayout, marginOffset: Int) {
        if (tabLayout.childCount < 0) return
        val tabStrip = tabLayout.getChildAt(0)
        if (tabStrip is ViewGroup) {
            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)
                if (tabView.layoutParams is ViewGroup.MarginLayoutParams) {
                    (tabView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = marginOffset
                    (tabView.layoutParams as ViewGroup.MarginLayoutParams).rightMargin = marginOffset
                }
            }
            tabLayout.requestLayout()
        }
    }
}