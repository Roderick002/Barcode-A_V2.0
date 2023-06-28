package com.example.barcode_a

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import com.example.barcode_a.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.example.barcode_a.databinding.ActivityMainManufacturerBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivityManufacturer : AppCompatActivity() {

    private lateinit var binding: ActivityMainManufacturerBinding

    private val delay : Long = 3000 // 3 seconds delay
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

    //Quit Application
    override fun onBackPressed() {

        if (quit == false){
            Toast.makeText(this, "Press Again To Quit", Toast.LENGTH_SHORT).show()
            quit = true

            val handler = Handler()
            handler.postDelayed({
                quit = false
            }, delay)
        }
        else{
            finishAffinity()
        }
    }

}