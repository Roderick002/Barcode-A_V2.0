package com.example.barcode_a

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EdgeEffect
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.budiyev.android.codescanner.CodeScannerView
import com.example.barcode_a.databinding.FragmentHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Home : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private  val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    private val delay : Long = 3000 // 3 seconds delay
    var quit = false

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity()

        firebaseAuth = FirebaseAuth.getInstance()

        //Get username
        val email = firebaseAuth.currentUser?.email.toString()
        val userName = email.replace(Regex("[@.]"), "")
        readData(userName)

        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (quit == false){
                Toast.makeText(activity, "Press Again To Quit", Toast.LENGTH_SHORT).show()
                quit = true

                val handler = Handler()
                handler.postDelayed({
                    quit = false
                }, delay)
            }
            else{
                finishAffinity(activity)
            }
        }

        val layoutNotification = view.findViewById<LinearLayout>(R.id.layoutNotification)
        layoutNotification.setOnClickListener {
            val fragment = AlarmsNotification()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

        val btnMHD = view.findViewById<LinearLayout>(R.id.btnMHD)
        btnMHD.setOnClickListener {
            val fragment = MedicalHealthDiagnosis()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        val btnDR = view.findViewById<LinearLayout>(R.id.btnDRP)
        btnDR.setOnClickListener {
            val fragment = DietaryRestriction()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        val btnAllergies = view.findViewById<LinearLayout>(R.id.btnAllergies)
        btnAllergies.setOnClickListener {
            val fragment = Allergies()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

        //side navigation
        drawerLayout = view.findViewById(R.id.drawerLayout)
        val navView: NavigationView = view.findViewById(R.id.nav_viewside)

        toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_edit-> {
                    val fragment = EditProfile()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                R.id.nav_aboutus->{
                    val aboutUsFragment = AboutsUs()
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, aboutUsFragment)
                        .commit()
                }
                R.id.nav_logout->{
                    firebaseAuth.signOut()
                    Toast.makeText(activity , "Account Signed Out!" , Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, LoginTab::class.java)
                    startActivity(intent)}

                R.id.toggleDarkMode -> {
                    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)
                    val currentNightMode = AppCompatDelegate.getDefaultNightMode()
                    val newNightMode = if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                        AppCompatDelegate.MODE_NIGHT_NO
                    } else {
                        AppCompatDelegate.MODE_NIGHT_YES
                    }
                    sharedPrefs.edit().putInt("night_mode", newNightMode).apply()
                    AppCompatDelegate.setDefaultNightMode(newNightMode)
                }
                R.id.nav_tutorial ->{
                    val tutorialFragment = TutorialPersonal()
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, tutorialFragment)
                        .commit()
                }
            }
            true
        }

        val menuImageView: ImageView = view.findViewById(R.id.imageMenu)
        menuImageView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

    }

    private fun recreateActivity() {
        TODO("Not yet implemented")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun readData(userName: String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(userName).get().addOnSuccessListener {

            if(it.exists()){

                val firstname = it.child("firstName").value.toString()
                binding.userName.text = firstname



            }else{
                Toast.makeText(activity , "User Does Not Exist!" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

}