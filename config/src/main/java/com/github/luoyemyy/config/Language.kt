package com.github.luoyemyy.config

import android.content.*
import android.os.Build
import android.os.LocaleList
import java.util.*


object Language {

    /**
     * Language Type
     */
    const val AUTO = "auto"
    const val CN = "CN"
    const val CN_HK = "HK"
    const val CN_TW = "TW"
    const val EN_US = "US"

    private const val LANGUAGE_CONFIG = "language_config"
    private const val LANGUAGE_KEY = "language_key"
    private val mInitSystemLocale = getInitSystemLocale()

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
     * Application#onCreate()
     */
    @JvmStatic
    fun listenerLanguageChange(context: Context) {
        context.applicationContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (getAppKey(context) == AUTO) {
                    System.exit(0)
                }
            }
        }, IntentFilter(Intent.ACTION_LOCALE_CHANGED))
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

    @JvmStatic
    fun getLanguageKey(context: Context):String = getAppKey(context)

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

    private fun getAppKey(context: Context): String {
        return spf(context).getString(LANGUAGE_KEY, null) ?: AUTO
    }

    private fun saveAppKey(context: Context, languageKey: String): Boolean {
        return spf(context).edit().putString(LANGUAGE_KEY, languageKey).commit()
    }

    private fun getAppLocal(context: Context): Locale {
        return getLocaleByKey(getAppKey(context))
    }

    private fun getLocaleByKey(languageKey: String): Locale {
        return when (languageKey) {
            AUTO -> mInitSystemLocale
            CN -> Locale.SIMPLIFIED_CHINESE
            CN_HK, CN_TW -> Locale.TRADITIONAL_CHINESE
            EN_US -> Locale.US
            else -> Locale.SIMPLIFIED_CHINESE
        }
    }

    private fun getInitSystemLocale(): Locale {
        return if (isGeApi24()) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
    }

    private fun spf(context: Context): SharedPreferences = context.getSharedPreferences(LANGUAGE_CONFIG, Context.MODE_PRIVATE)

    private fun isGeApi24() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    private fun isAuto(context: Context) = getAppKey(context) == AUTO

}

