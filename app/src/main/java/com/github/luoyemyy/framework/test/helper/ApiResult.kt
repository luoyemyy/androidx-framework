package com.github.luoyemyy.framework.test.helper

import com.github.luoyemyy.async.AsyncRun

class ApiResult<T> : AsyncRun.Result {

    override var isSuccess: Boolean = false
    var data: T? = null
    var list: List<T>? = null

    fun <R> map(callback: (T) -> R): ApiResult<R> {
        return ApiResult<R>().also { result ->
            result.isSuccess = isSuccess
            result.data = data?.let { callback(it) }
            result.list = list?.map { callback(it) }
        }
    }
}