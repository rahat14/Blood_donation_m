package com.metacoders.blood_donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val hander: Handler = Handler()
        hander.postDelayed(
            {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }, 500
        )
    }
}