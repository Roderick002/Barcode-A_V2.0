package com.example.barcode_a

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScannerView
import com.example.barcode_a.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class Home : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnHomeSignOut = view.findViewById<Button>(R.id.btnHomeSignOut)
        val activity = requireActivity()

        firebaseAuth = FirebaseAuth.getInstance()

        //Sign Out Function
        btnHomeSignOut.setOnClickListener(){
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)

    }
}