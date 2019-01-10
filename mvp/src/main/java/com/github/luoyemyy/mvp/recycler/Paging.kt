package com.github.luoyemyy.mvp.recycler

interface Paging {

    fun reset()

    fun next()

    fun current(): Long

    fun errorBack()

    class Page : Paging {

        private var currentPage: Long = 1
        private var prevPage: Long = 1

        override fun reset() {
            prevPage = currentPage
            currentPage = 1
        }

        override fun next() {
            prevPage = currentPage
            currentPage++
        }

        override fun errorBack() {
            currentPage = prevPage
        }

        override fun current(): Long {
            return currentPage
        }
    }

}