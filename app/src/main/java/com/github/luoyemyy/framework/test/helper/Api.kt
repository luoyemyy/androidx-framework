package com.github.luoyemyy.framework.test.helper

import retrofit2.Call

interface Api {
    fun login(): Call<ApiResult<String>>
}