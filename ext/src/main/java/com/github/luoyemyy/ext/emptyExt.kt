@file:Suppress("unused")

package com.github.luoyemyy.ext

/**
 *  empty
 */
fun String?.empty(): Boolean = this == null || isEmpty()

fun String?.notEmpty(): Boolean = this != null && isNotEmpty()

fun Collection<*>?.empty(): Boolean = this == null || isEmpty()

fun Collection<*>?.notEmpty(): Boolean = this != null && isNotEmpty()

fun Map<*, *>?.empty(): Boolean = this == null || isEmpty()

fun Map<*, *>?.notEmpty(): Boolean = this != null && isNotEmpty()
