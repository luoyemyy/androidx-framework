package com.github.luoyemyy.api

import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

abstract class AbstractApiManager {

    companion object {
        var mRetrofit: Retrofit? = null
    }

    abstract fun baseUrl(): String

    open fun client(): OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(LogInterceptor())

    open fun converter(): Converter.Factory? = GsonConverterFactory.create()

    open fun adapter(): CallAdapter.Factory? = RxJava2CallAdapterFactory.create()

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl()).client(client().build()).apply {
            converter()?.also {
                addConverterFactory(it)
            }
            adapter()?.also {
                addCallAdapterFactory(it)
            }
        }.build()
    }

    inline fun <reified T> getApi(): T {
        return (mRetrofit ?: getRetrofit().apply { mRetrofit = this }).create(T::class.java)
    }
}
