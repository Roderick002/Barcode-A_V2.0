package com.example.barcode_a

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.barcode_a.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment




class MainActivityManufacturer : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_manufacturer)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Home_Manufacturer())
        binding.bottomNavigationView2.setOnItemSelectedListener {

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