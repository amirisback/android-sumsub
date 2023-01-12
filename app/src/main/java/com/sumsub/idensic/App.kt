package com.sumsub.idensic

import android.content.Context
import android.os.Build
import androidx.multidex.MultiDexApplication
import com.sumsub.idensic.manager.PrefManager
import java.util.*

class App: MultiDexApplication() {

    lateinit var prefManager: PrefManager

    companion object {
        val TAG: String = App::class.java.simpleName

        lateinit var instance: App

        fun getContext(): Context = instance.applicationContext

        fun getCurrentLocale(): Locale? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                instance.resources.configuration.locales[0]
            } else {
                instance.resources.configuration.locale
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        prefManager = PrefManager(this)
    }

}