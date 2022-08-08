package com.metacoders.blood_donation

import android.app.Application



class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPrefManager.with(this)

    }
}
