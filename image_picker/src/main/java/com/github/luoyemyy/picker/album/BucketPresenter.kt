package com.github.luoyemyy.picker.album

import android.app.Application
import android.os.Bundle
import com.github.luoyemyy.mvp.recycler.AbstractRecyclerPresenter
import com.github.luoyemyy.mvp.recycler.LoadType
import com.github.luoyemyy.mvp.recycler.Paging
import com.github.luoyemyy.picker.entity.Bucket

class BucketPresenter(val app: Application) : AbstractRecyclerPresenter<Bucket>(app) {

    var buckets: List<Bucket>? = null

    override fun loadData(loadType: LoadType, paging: Paging, bundle: Bundle?, search: String?): List<Bucket>? {
        return buckets
    }
}