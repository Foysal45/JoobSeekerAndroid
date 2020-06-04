package com.bdjobs.app.videoInterview.data.remote

import android.content.Context
import com.bdjobs.app.videoInterview.util.NetworkConnectionInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// TODO: 6/4/20 create the base url
private const val LOGIN_BASE_URL = ""

private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

interface VideoInterviewApiService {

    companion object Factory{
        @Volatile
        private var retrofit: Retrofit? = null


        @Synchronized
        fun create(context: Context): VideoInterviewApiService {

            retrofit ?: synchronized(this) {
                retrofit = buildRetrofit(context)
            }

            return retrofit?.create(VideoInterviewApiService::class.java)!!
        }

        private fun buildRetrofit(context: Context) : Retrofit{

            val loginInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val loginOkHttpClient = OkHttpClient.Builder()
                    .addInterceptor(loginInterceptor)
                    .addInterceptor(NetworkConnectionInterceptor(context))
                    .build()

            return Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .baseUrl(LOGIN_BASE_URL)
                    .client(loginOkHttpClient)
                    .build()
        }

    }

}
