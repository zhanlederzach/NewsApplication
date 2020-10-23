package kz.newsapplication.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mobile.telecomapp.utils.NavigationAnimation
import kz.newsapplication.R
import kz.newsapplication.ui.favorites.FavoritesFragment
import kz.newsapplication.ui.top_headlienes.top_headliens_details.TopHeadliensDetailsFragment
import kz.newsapplication.utils.Likeable
import kz.newsapplication.utils.Screen
import kz.newsapplication.utils.extensions.changeFragment

class MainActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, data: Bundle? = null) {
            val intent = Intent(context, MainActivity::class.java)
            data?.let { intent.putExtras(it) }
            context.startActivity(intent)
        }
    }

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            navigateTo(
                fragment = MainFragment.newInstance(intent.extras),
                tag = Screen.MAIN_FRAGMENT.name,
                animation = NavigationAnimation.NONE
            )
            supportFragmentManager.addOnBackStackChangedListener {
                if (supportFragmentManager.backStackEntryCount - 1 < supportFragmentManager.backStackEntryCount &&
                    supportFragmentManager.backStackEntryCount - 1 >= 0
                ) {
                    val tag =
                        supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
                    if (!tag.isNullOrEmpty()) {
                        currentFragment = supportFragmentManager.findFragmentByTag(tag)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        var isFavorite: Boolean? = null
        if (currentFragment != null && currentFragment is TopHeadliensDetailsFragment) {
            isFavorite = (currentFragment as TopHeadliensDetailsFragment).isFavorite
        }

        super.onBackPressed()

        if (isFavorite != null && supportFragmentManager.primaryNavigationFragment != null) {
            when (supportFragmentManager.primaryNavigationFragment) {
                is MainFragment -> {
                    ((supportFragmentManager.primaryNavigationFragment as MainFragment).fragments[MainFragment.EVERYTHING] as Likeable)
                        .setLike(isFavorite)
                }
                is FavoritesFragment -> {
                    ((supportFragmentManager.primaryNavigationFragment as FavoritesFragment) as Likeable)
                        .setLike(isFavorite)
                }
            }
        }
    }

    fun navigateTo(
        fragment: Fragment,
        tag: String,
        addToStack: Boolean = false,
        animation: NavigationAnimation = NavigationAnimation.SLIDE
    ) {
        changeFragment(
            layoutId = R.id.menuContainer,
            fragment = fragment,
            tagFragmentName = tag,
            addToStack = addToStack,
            animation = animation
        )
    }
}