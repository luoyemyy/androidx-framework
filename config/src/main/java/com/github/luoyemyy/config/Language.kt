package com.github.luoyemyy.config

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.LocaleList
import java.util.*


object Language {

    /**
     * Language Type
     */
    val AUTO = "auto"
    val CN = "CN"
    val CN_HK = "HK"
    val CN_TW = "TW"
    val EN_US = "US"

    private const val LANGUAGE_CONFIG = "language_config"
    private const val LANGUAGE_KEY = "language_key"
    private val mSystemLocaleDesc = if (isGeApi24()) LocaleList.getDefault().toString() else ""


    /**
     * Application#attachBaseContext(Context)
     * Activity#attachBaseContext(Context)
     * Service#attachBaseContext(Context)
     */
    @JvmStatic
    fun attachBaseContext(context: Context): Context {
        if (isAuto(context)) {
            return context
        }
        val config = context.resources.configuration
        val local = getAppLocal(context)
        return if (isGeApi24()) {
            config.setLocale(local)
            context.createConfigurationContext(config)
        } else {
            config.locale = local
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    /**
     * Application#onConfigurationChanged(Configuration)
     */
    @JvmStatic
    fun systemLanguageChanged(context: Context) {
        if (isAuto(context)) {
            System.exit(0)
        }
    }

    /**
     * after invoke Activity#recreate()
     */
    @JvmStatic
    fun changeLanguage(context: Context, languageKey: String) {
        saveAppKey(context, languageKey)
        updateLanguage(context.applicationContext)
        updateLanguage(context)
    }

    private fun updateLanguage(context: Context) {
        val config = context.resources.configuration
        val locale = getAppLocal(context)
        if (isGeApi24()) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun saveAppKey(context: Context, languageKey: String): Boolean {
        return spf(context).edit().putString(LANGUAGE_KEY, languageKey).commit()
    }

    private fun getAppKey(context: Context): String {
        return spf(context).getString(LANGUAGE_KEY, null) ?: AUTO
    }

    private fun getAppLocal(context: Context): Locale {
        return getLocale(getAppKey(context))
    }

    private fun getLocale(languageKey: String): Locale {
        return when (languageKey) {
            AUTO -> getSystemLocale()
            CN -> Locale.SIMPLIFIED_CHINESE
            CN_HK, CN_TW -> Locale.TRADITIONAL_CHINESE
            EN_US -> Locale.US
            else -> Locale.SIMPLIFIED_CHINESE
        }
    }

    private fun getSystemLocale(): Locale {
        return if (isGeApi24()) {
            val defaultLocale = LocaleList.getDefault()
            val defaultLocaleDesc = defaultLocale.toString()
            val localeList = (0 until defaultLocale.size()).map { defaultLocale[it] }
            when {
                defaultLocaleDesc == mSystemLocaleDesc -> localeList[0]
                localeList.size > 1 -> localeList[1]
                else -> localeList[0]
            }
        } else {
            Locale.getDefault()
        }
    }

    private fun spf(context: Context): SharedPreferences = context.getSharedPreferences(LANGUAGE_CONFIG, Context.MODE_PRIVATE)

    private fun isGeApi24() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    private fun isAuto(context: Context) = getAppKey(context) == AUTO

}

