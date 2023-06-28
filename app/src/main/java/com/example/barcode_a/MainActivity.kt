package com.example.barcode_a

import android.app.Activity
import android.graphics.Color
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.example.barcode_a.databinding.ActivityLoginTabBinding
import com.example.barcode_a.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val CAMERA_REQUEST_CODE = 101

    private val delay : Long = 3000 // 3 seconds delay
    var quit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupPermissions()

        firebaseAuth = FirebaseAuth.getInstance()


        replaceFragment(Home())
        binding.bottomNavigationView2.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(Home())
                R.id.scan -> replaceFragment(Scan())
                R.id.search -> replaceFragment(Search())

                else -> {

                }
            }
            true
        }

    }

//Camera Permission
    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this  , arrayOf( android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need the camera permission", Toast.LENGTH_SHORT).show()
                }else {
                    //successful
                }
            }
        }
    }

    //Toggle Fragments

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
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