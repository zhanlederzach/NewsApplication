package kz.newsapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mobile.telecomapp.utils.NavigationAnimation
import kz.newsapplication.R
import kz.newsapplication.utils.Screen
import kz.newsapplication.utils.extensions.changeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            navigateTo(
                fragment = MainFragment.newInstance(
                    intent.extras
                ),
                tag = Screen.MAIN_FRAGMENT.name,
                animation = NavigationAnimation.NONE
            )
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