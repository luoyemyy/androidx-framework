package com.github.luoyemyy.framework.test.helper

import com.github.luoyemyy.async.AsyncRun
import retrofit2.Call

fun getApi(): Api = ApiManager().getApi()

fun <T, R> Call<ApiResult<T>>.create(map: (T) -> R): AsyncRun.ResultCall<ApiResult<R>> {
    return AsyncRun.newResultCall<ApiResult<R>>().create { _ ->
        val result = this.execute()
        if (result.isSuccessful) {
            result.body()?.map(map) ?: ApiResult()
        } else {
            ApiResult()
        }
    }
}

fun <T> Call<ApiResult<T>>.create(): AsyncRun.ResultCall<ApiResult<T>> {
    val error = ApiResult<T>()
    return AsyncRun.newResultCall<ApiResult<T>>().create {
        val result = this.execute()
        if (result.isSuccessful) {
            result.body() ?: error
        } else {
            error
        }
    }
}