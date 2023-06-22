package com.example.barcode_a

import android.app.ApplicationExitInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.content.Intent
import android.os.Handler
import android.os.Looper



class SplashActivity : AppCompatActivity() {

        companion object {
            private const val SPLASH_SCREEN: Long = 5000
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val topAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        val bottomAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        val image: ImageView = findViewById(R.id.imageView);
        val logo: TextView = findViewById(R.id.textView3);

        image.startAnimation(topAnim);
        logo.startAnimation(bottomAnim);

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginTab::class.java)
            startActivity(intent)
            //finish()
        }, SPLASH_SCREEN)


    }

}