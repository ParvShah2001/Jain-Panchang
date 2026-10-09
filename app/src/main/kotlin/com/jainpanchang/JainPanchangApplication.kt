package com.jainpanchang

import android.app.Application

import com.jainpanchang.di.AppContainer
import com.jainpanchang.di.DefaultAppContainer

class JainPanchangApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
    }
}
