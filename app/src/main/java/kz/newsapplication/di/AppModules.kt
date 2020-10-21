package kz.newsapplication.di

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.newsapplication.R
import kz.newsapplication.api.NewsApi
import kz.newsapplication.model.errors.NoConnectionException
import kz.newsapplication.repository.NewsRepository
import kz.newsapplication.repository.NewsRepositoryImpl
import kz.newsapplication.ui.NewsViewModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { createGson() }
    single { createConnectionCheckerInterceptor(context = get()) }
    single { createHttpClient(connectionCheckerInterceptor = get())}
    single { createApiService(okHttpClient = get(), gson = get())}

    // Repositories
    single { NewsRepositoryImpl(newsApi = get(), gson = get()) as NewsRepository }
}

val viewModelModule = module(override = true) {
    viewModel { NewsViewModel(newsRepository = get()) }
}

fun createGson(): Gson = GsonBuilder()
    .setLenient()
    .create()

fun createConnectionCheckerInterceptor(context: Context): Interceptor {
    return Interceptor { chain: Interceptor.Chain ->
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        val isConnected = netInfo != null && netInfo.isConnected
        if (!isConnected) {
            throw NoConnectionException(context.getString(R.string.no_internet_connection))
        } else {
            chain.proceed(chain.request())
        }
    }
}

fun createHttpClient(
    connectionCheckerInterceptor: Interceptor
): OkHttpClient {

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
        .cache(null)
    okHttpClient.addInterceptor(connectionCheckerInterceptor)
    return okHttpClient.build()
}

fun createApiService(okHttpClient: OkHttpClient, gson: Gson): NewsApi {
    return Retrofit.Builder()
        .baseUrl("https://newsapi.org/v2/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(NewsApi::class.java)
}

fun <T> applySchedulersSingle(): SingleTransformer<T, T> {
    return object : SingleTransformer<T, T> {
        override fun apply(upstream: Single<T>): SingleSource<T> {
            return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}

val appModules = listOf(networkModule, viewModelModule)