package kz.newsapplication.utils.extensions

import android.content.Context
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

fun Context.circularProgress(): CircularProgressDrawable {
    return CircularProgressDrawable(this).apply {
        strokeWidth = 3f
        centerRadius = 15f
        start()
    }
}