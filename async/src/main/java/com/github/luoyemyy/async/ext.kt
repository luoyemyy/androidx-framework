package com.github.luoyemyy.async

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


fun <T> single(create: () -> T): Single<T> {
    return Single.create<T> {
        it.onSuccess(create())
    }
}

fun <T> Single<T>.result(result: (ok: Boolean, value: T?) -> Unit): Disposable {
    return subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe { value, error ->
        result(error == null, value)
    }
}