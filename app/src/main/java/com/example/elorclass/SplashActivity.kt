package com.example.elorclass

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide

class SplashActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashImage = findViewById<ImageView>(R.id.splash_image)

        Glide.with(this)
            .load(R.drawable.birrete)
            .into(splashImage)

        val splashScreenDuration: Long = 3000
        android.os.Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, splashScreenDuration)
    }
}