package com.elementary.mualijpro.networks

import android.content.Context
import com.elementary.mualijpro.models.posts.user.User
import com.elementary.mualijpro.utils.*
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit
import com.google.gson.GsonBuilder
import com.google.gson.Gson


class ApiClient(mContext: Context) {

    private var retrofit: Retrofit? = null
    private val requestTimeOut = 60
    private var okHttpClient: OkHttpClient? = null
    private val mContext: Context = mContext
    private var userdata: User? = null

    fun getClient(): Retrofit? {

        if (okHttpClient == null)
            initOkHttp()

        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(AppUtils.decryptValue(Constants.MAIN_HTTP_BASE_URL))
                .client(okHttpClient!!)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }

    // Initialize HTTP Request
    private fun initOkHttp() {

        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(requestTimeOut.toLong(), TimeUnit.SECONDS)
            .readTimeout(requestTimeOut.toLong(), TimeUnit.SECONDS)
            .writeTimeout(requestTimeOut.toLong(), TimeUnit.SECONDS)

/*     val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(interceptor)*/


        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val request = prepareRequestWithHeader(original)
            chain.proceed(request!!)
        }

        okHttpClient = httpClient.build()

    }

    // Prepare  Headers
    private fun prepareRequestWithHeader(original: Request?): Request? {

        if (original != null) {

            val session = RandomString()
            Constants.appToken = session.nextString()
            val builder = original.newBuilder()
            builder.header("Accept", "application/json")
            builder.header("Content-Type", "application/json")
            builder.header("Cache-Control", "no-cache")
            builder.header("X-Requested-With", Constants.appToken)
            val userSP: UserDataPref = UserDataPref.getInstance(mContext)
            try {
                if (userSP.getUserData() != null) {
                    userdata = userSP.getUserData()
                    if (userdata != null) {
                        builder.header(
                            "Authorization",
                            "Bearer ${AppUtils.decryptValue(userdata?.getToken()!!)}"
                        )
                    }
                }
            } catch (e: Exception) {

            }
            builder.method(original.method(), original.body())
            return builder.build()
        }

        return null
    }

}
