package kz.newsapplication.ui.top_headlienes.top_headliens_details

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kz.newsapplication.R
import kz.newsapplication.model.News
import kz.newsapplication.ui.NewsViewModel
import kz.newsapplication.utils.Constants
import kz.newsapplication.utils.DateUtil
import kz.newsapplication.utils.extensions.circularProgress
import org.koin.android.ext.android.inject
import org.koin.core.KoinComponent

class TopHeadliensDetailsFragment : Fragment(), KoinComponent {

    companion object {
        fun newInstance(bundle: Bundle? = null): TopHeadliensDetailsFragment {
            return TopHeadliensDetailsFragment().apply {
                arguments = bundle
            }
        }
    }

    private lateinit var ivNews: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDatePublished: TextView
    private lateinit var webView: WebView
    private lateinit var toolbar: Toolbar
    private lateinit var ivFavorite: ImageView
    private lateinit var viewDivider: View

    private var news: News? = null
    var isFavorite: Boolean = false

    private val viewModel: NewsViewModel by inject()

    private val onFavoriteClickListener: View.OnClickListener = View.OnClickListener {
        isFavorite = if (isFavorite) {
            viewModel.deleteFromFavorites(news)
            setImageFavorite(isLiked = false)
            false
        } else {
            viewModel.addToFavorite(news)
            setImageFavorite(isLiked = true)
            true
        }
        val data = Intent()
        data.putExtras(bundleOf(Constants.LIKE_VALUE to isFavorite))
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, data)
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args?.getSerializable(Constants.NEWS) != null &&
            args.getSerializable(Constants.NEWS) is News) {
            news = args.getSerializable(Constants.NEWS) as News
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top_headlines_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setData()
    }

    private fun bindViews(view: View) = with(view) {
        ivNews = findViewById(R.id.ivNews)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        tvDatePublished = findViewById(R.id.tvDatePublished)
        webView = findViewById(R.id.webView)
        toolbar = findViewById(R.id.toolbar)
        ivFavorite = findViewById(R.id.ivFavorite)
        viewDivider = findViewById(R.id.viewDivider)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.title = news?.title
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        setWebView()
    }

    private fun setData() {
        setDescription()
        isFavorite = viewModel.getNewsByUrl(news) != null
        setImageFavorite(isLiked = isFavorite)
        ivFavorite.setOnClickListener(onFavoriteClickListener)
    }

    private fun setDescription() {
        setImageView()
        tvTitle.text = news?.title
        if (!news?.description.isNullOrEmpty()) {
            tvDescription.text = news?.description
            viewDivider.visibility = View.VISIBLE
        }
        tvDatePublished.text = DateUtil.convertIsoToDate(news?.publishedAt)
        webView.loadData(news?.content, "text/html; charset=UTF-8", null)
    }

    private fun setImageView() {
        Glide
            .with(requireContext())
            .load(news?.urlToImage)
            .placeholder(requireContext().circularProgress())
            .error(ContextCompat.getDrawable(requireContext(), R.drawable.bg_white_rounded))
            .into(ivNews)
    }

    private fun setWebView() {
        webView.apply {
            clearCache(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(true)
            isHorizontalScrollBarEnabled = false
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowContentAccess = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.mediaPlaybackRequiresUserGesture = false
            if (Build.VERSION.SDK_INT >= 26) {
                settings.safeBrowsingEnabled = false
            }
        }
    }

    private fun setImageFavorite(isLiked: Boolean) {
        if (isLiked) {
            ivFavorite.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_filled)
        } else {
            ivFavorite.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_star_border)
        }
    }

}