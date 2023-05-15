package com.kust.ermsmanager

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }
}