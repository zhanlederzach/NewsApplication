package kz.newsapplication.utils.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.mobile.telecomapp.utils.NavigationAnimation

fun AppCompatActivity.changeFragment(
    layoutId: Int,
    fragment: Fragment,
    tagFragmentName: String,
    addToStack: Boolean = false,
    animation: NavigationAnimation = NavigationAnimation.SLIDE
) {
    val fragmentManager = supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()
    if (addToStack) {
        fragmentTransaction.addToBackStack(tagFragmentName)
    }
    setTransactionAnimation(fragmentTransaction, animation)
    val currentFragment = fragmentManager.primaryNavigationFragment
    if (currentFragment != null) {
        fragmentTransaction.hide(currentFragment)
    }
    var fragmentTemp = fragmentManager.findFragmentByTag(tagFragmentName)
    if (fragmentTemp == null) {
        fragmentTemp = fragment
        fragmentTransaction.add(layoutId, fragmentTemp, tagFragmentName)
    } else {
        fragmentTransaction.show(fragmentTemp)
    }
    if (!isFinishing) {
        fragmentTransaction.setPrimaryNavigationFragment(fragmentTemp)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitAllowingStateLoss()
    }
}

private fun setTransactionAnimation(transaction: FragmentTransaction, animation: NavigationAnimation) {
    when (animation) {
        NavigationAnimation.SLIDE -> {
//            transaction.setCustomAnimations(
//                R.anim.enter_from_right,
//                R.anim.exit_to_left,
//                R.anim.enter_from_left,
//                R.anim.exit_to_right
//            )
        }
        NavigationAnimation.SLIDE_UP -> {
//            transaction.setCustomAnimations(
//                R.anim.slide_up,
//                R.anim.stay,
//                R.anim.stay,
//                R.anim.slide_down
//            )
        }
        else -> {}
    }
}