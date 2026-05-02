package com.colman.reread

import android.app.Application
import android.content.Context

class ReRead: Application() {
    companion object Globals{
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}