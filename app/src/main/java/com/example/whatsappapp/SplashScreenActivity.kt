package com.example.whatsappapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    private val splashScreenTime = 4000
    private lateinit var imageGif: ImageView
    private lateinit var topAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        imageGif = findViewById(R.id.splashGif)
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim)
        imageGif.animation = topAnim
        Handler(Looper.getMainLooper()).postDelayed(
            {
                //  FROM SPLASH SCREEN TO ANY ACTIVITY
                val intent = Intent(this@SplashScreenActivity, AuthenticationActivity::class.java)
                startActivity(intent)
                finish()
            },
            splashScreenTime.toLong()
        )
    }
}
