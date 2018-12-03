package com.github.luoyemyy.picker.view

interface ImageViewListener {

    /**
     * 双击、缩放、移动都会触发
     */
    fun onChange()

    /**
     * 单击触发
     */
    fun onSingleTap()

}