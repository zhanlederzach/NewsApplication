package kz.newsapplication.utils.extensions

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.mobile.telecomapp.utils.NavigationAnimation
import kz.newsapplication.R

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
            transaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
        }
        NavigationAnimation.SLIDE_UP -> {
            transaction.setCustomAnimations(
                R.anim.slide_up,
                R.anim.stay,
                R.anim.stay,
                R.anim.slide_down
            )
        }
        else -> {}
    }
}

fun Activity.showTopToast(text: String?) {
    if (text.isNullOrEmpty()) {
        return
    }
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
//    val contentView = LayoutInflater.from(this).inflate(R.layout.notification_view, null, false)
//    val tvError = contentView.findViewById<TextView>(R.id.tvError)
//    tvError.text = text
//
//    val snackBar = TSnackbar.make(findViewById(android.R.id.content), "", Snackbar.LENGTH_LONG)
//    val snackBarLayout = snackBar.view as TSnackbar.SnackbarLayout
//    val textView = snackBarLayout.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
//    textView.visibility = View.INVISIBLE
//
//    val params = snackBar.getView().getLayoutParams() as ViewGroup.MarginLayoutParams
//    snackBar.getView().apply {
//        setLayoutParams(params)
//        (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.TOP
//        (layoutParams as FrameLayout.LayoutParams).setMargins(16, 16, 16, 16)
//        background = getDrawable(R.drawable.background_rounded_notification_white)
//        ViewCompat.setElevation(this, 5f)
//    }
//    snackBarLayout.addView(contentView, 0)
//    snackBar.show()
}