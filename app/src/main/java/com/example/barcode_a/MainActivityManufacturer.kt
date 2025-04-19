package com.example.barcode_a

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.barcode_a.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.example.barcode_a.databinding.ActivityMainManufacturerBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivityManufacturer : AppCompatActivity() {
    private lateinit var binding: ActivityMainManufacturerBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var quit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_manufacturer)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        binding = ActivityMainManufacturerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home_Manufacturer())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(Home_Manufacturer())
                else -> {
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout2,fragment)
        fragmentTransaction.commit()
    }
}