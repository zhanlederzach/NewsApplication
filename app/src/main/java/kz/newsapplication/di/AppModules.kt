package kz.newsapplication.di

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.newsapplication.R
import kz.newsapplication.api.NewsApi
import kz.newsapplication.database.DaoNews
import kz.newsapplication.database.NewsDatabase
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
import org.reactivestreams.Publisher
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { createGson() }
    single { createLoggingInterceptor() }
    single { createConnectionCheckerInterceptor(context = get()) }
    single { createHttpClient(connectionCheckerInterceptor = get(), httpLoggingInterceptor = get())}
    single { createApiService(okHttpClient = get(), gson = get())}
    single { createDataBase(context = get()) }
    single { createNewsDao(newsDatabase = get()) }

    // Repositories
    single { NewsRepositoryImpl(newsApi = get(), daoNews = get(), gson = get()) as NewsRepository }
}

val viewModelModule = module(override = true) {
    viewModel { NewsViewModel(newsRepository = get()) }
}

fun createGson(): Gson = GsonBuilder()
    .setLenient()
    .create()

fun createLoggingInterceptor(): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY
    return interceptor
}

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
    httpLoggingInterceptor: HttpLoggingInterceptor,
    connectionCheckerInterceptor: Interceptor
): OkHttpClient {

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
        .cache(null)
    okHttpClient.addInterceptor(connectionCheckerInterceptor)
    okHttpClient.addInterceptor(httpLoggingInterceptor)
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

fun createNewsDao(newsDatabase: NewsDatabase): DaoNews = newsDatabase.newsDao()

fun createDataBase(context: Context): NewsDatabase {
    return NewsDatabase.getDatabase(context = context)
}

fun <T> applySchedulersSingle(): SingleTransformer<T, T> {
    return object : SingleTransformer<T, T> {
        override fun apply(upstream: Single<T>): SingleSource<T> {
            return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}

fun <T> applySchedulersFlowable(): FlowableTransformer<T, T> {
    return object : FlowableTransformer<T, T> {
        override fun apply(upstream: Flowable<T>): Publisher<T> {
            return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}

val appModules = listOf(networkModule, viewModelModule)