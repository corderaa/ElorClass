package com.example.elorclass

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class SplashActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashImage = findViewById<ImageView>(R.id.splash_image)
        // Cargar el GIF con Glide en el ImageView
        Glide.with(this)
            .load(R.drawable.birrete)
            .into(splashImage)

        // Esperar unos segundos antes de abrir la MainActivity
        val splashScreenDuration: Long = 3000 // Duración del splash screen (3 segundos)
        android.os.Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la SplashActivity para evitar volver atrás
        }, splashScreenDuration)
    }
}