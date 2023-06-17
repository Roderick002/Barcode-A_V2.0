package com.example.barcode_a

import android.graphics.Color
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.databinding.DataBindingUtil






class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnTestSignOut.setOnClickListener(){
            firebaseAuth.signOut()
            Toast.makeText(this , "Account Signed Out!" , Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginTab::class.java)
            startActivity(intent)
        }
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

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}