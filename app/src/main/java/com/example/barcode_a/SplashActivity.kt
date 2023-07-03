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
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

        companion object {
            private const val Loading : Long = 1000
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val topAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        val bottomAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        val image: ImageView = findViewById(R.id.imageView);
        val logo: TextView = findViewById(R.id.textView3);

        image.startAnimation(topAnim);
        logo.startAnimation(bottomAnim);

    }

    override fun onStart() {
        super.onStart()

        Handler(Looper.getMainLooper()).postDelayed({
            firebaseAuth = FirebaseAuth.getInstance()

            if(firebaseAuth.currentUser != null){
                val email = firebaseAuth.currentUser?.email.toString()
                val userName = email.replace(Regex("[@.]"), "")
                readData(userName)
            }else{
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, LoginTab::class.java)
                    startActivity(intent)
                }, Loading)
            }
        }, Loading)

    }

    private fun readData(userName: String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(userName).get().addOnSuccessListener {

            if(it.exists()){
                val userType = it.child("userType").value.toString()
                if (userType == "Personal"){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this , "Logged In Successfully!" , Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, MainActivityManufacturer::class.java)
                    startActivity(intent)
                    Toast.makeText(this , "Logged In Successfully!" , Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this , "User Does Not Exist!" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this  , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }

}