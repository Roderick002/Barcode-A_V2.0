package com.example.barcode_a

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EdgeEffect
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat.finishAffinity
import com.budiyev.android.codescanner.CodeScannerView
import com.example.barcode_a.databinding.FragmentHomeBinding
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

        //Sign Out Function
        binding.btnHomeSignOut.setOnClickListener(){
            firebaseAuth.signOut()
            Toast.makeText(activity , "Account Signed Out!" , Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginTab::class.java)
            startActivity(intent)
        }

        val layoutHealth = view.findViewById<LinearLayout>(R.id.layoutHealth)
        layoutHealth.setOnClickListener {
            val fragment = HealthPreference()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        val layoutNotification = view.findViewById<LinearLayout>(R.id.layoutNotification)
        layoutNotification.setOnClickListener {
            val fragment = AlarmsNotification()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        val layoutEdit = view.findViewById<LinearLayout>(R.id.layoutEdit)
        layoutEdit.setOnClickListener {
            val fragment = EditProfile()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }


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