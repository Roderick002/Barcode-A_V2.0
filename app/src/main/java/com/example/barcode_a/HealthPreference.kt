package com.example.barcode_a

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class HealthPreference : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_preference, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnMHD = view.findViewById<Button>(R.id.btnMHD)
        btnMHD.setOnClickListener {
            val fragment = MedicalHealthDiagnosis()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        val btnDR = view.findViewById<Button>(R.id.btnDRP)
        btnDR.setOnClickListener {
            val fragment = DietaryRestriction()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        val btnAllergies = view.findViewById<Button>(R.id.btnAllergies)
        btnAllergies.setOnClickListener {
            val fragment = Allergies()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}