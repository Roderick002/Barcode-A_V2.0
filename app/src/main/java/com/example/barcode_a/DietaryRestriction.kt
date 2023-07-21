package com.example.barcode_a

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DietaryRestriction : Fragment() {

    private lateinit var checkboxVegetarian: CheckBox
    private lateinit var checkboxVegan: CheckBox
    private lateinit var checkboxPollutarian: CheckBox
    private lateinit var checkboxPescetarian: CheckBox
    private lateinit var checkboxNone: CheckBox
    private lateinit var buttonSave: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  database : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dietary_restrcition, container, false)

        checkboxVegetarian = view.findViewById(R.id.checkBoxVegetarian)
        checkboxVegan = view.findViewById(R.id.checkBoxVegan)
        checkboxPollutarian = view.findViewById(R.id.checkBoxPollutarian)
        checkboxPescetarian = view.findViewById(R.id.checkBoxPescetarian)
        checkboxNone = view.findViewById(R.id.checkboxNone)
        buttonSave = view.findViewById(R.id.btnDoneDR)

        val buttonSave = view.findViewById<Button>(R.id.btnDoneDR)
        buttonSave.setOnClickListener {
            saveData()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = Home()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        val backbutton = view.findViewById<ImageView>(R.id.drwbackDR)
        backbutton.setOnClickListener {
            val fragment = Home()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

        val email = firebaseAuth.currentUser?.email.toString()
        val userName = email.replace(Regex("[@.]"), "")
        readData(userName)
    }

    private fun saveData() {
        val selectedOptions = mutableListOf<String>()
        var dietary1: String? = null
        var dietary2: String? = null
        var dietary3: String? = null
        var dietary4: String? = null

        if (checkboxNone.isChecked){
            selectedOptions.add("None")
            checkboxVegetarian.isChecked = false
            checkboxVegan.isChecked = false
            checkboxPollutarian.isChecked = false
            checkboxPescetarian.isChecked = false
        }
        if (checkboxVegetarian.isChecked) {
            selectedOptions.add("Vegetarian")
            dietary1 = "Vegetarian"
        }
        if (checkboxVegan.isChecked) {
            selectedOptions.add("Vegan")
            dietary2 = "Vegan"
        }
        if (checkboxPollutarian.isChecked) {
            selectedOptions.add("Pollutarian")
            dietary3 = "Pollutarian"
        }
        if (checkboxPescetarian.isChecked) {
            selectedOptions.add("Pescetarian")
            dietary4 = "Pescetarian"
        }

        if (selectedOptions.isEmpty()) {
            Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
        } else {
            firebaseAuth = FirebaseAuth.getInstance()

            val email = firebaseAuth.currentUser?.email.toString()
            val userName = email.replace(Regex("[@.]"), "")

            database = FirebaseDatabase.getInstance().getReference("Dietaries")

            val dietary = Dietary(dietary1, dietary2, dietary3, dietary4)

            database.child(userName).setValue(dietary).addOnSuccessListener {
                Toast.makeText(activity , "Details Updated!" , Toast.LENGTH_SHORT).show()
            }.addOnFailureListener(){
                Toast.makeText(activity , "Update Details Failed!" , Toast.LENGTH_SHORT).show()
            }

            val selectedOptionsString = selectedOptions.joinToString(", ")
            showDialog(selectedOptionsString)
        }
    }

    private fun readData(userName: String){
        database = FirebaseDatabase.getInstance().getReference("Dietaries")
        database.child(userName).get().addOnSuccessListener {
            if(it.exists()){

                val vegetarian = it.child("dietary1").value.toString()
                val vegan = it.child("dietary2").value.toString()
                val pollutarian = it.child("dietary3").value.toString()
                val pescetarian = it.child("dietary4").value.toString()

                if(vegetarian != "null"){
                    checkboxVegetarian.isChecked = true
                }
                if(vegan != "null"){
                    checkboxVegan.isChecked = true
                }
                if(pollutarian != "null"){
                    checkboxPollutarian.isChecked = true
                }
                if(pescetarian != "null"){
                    checkboxPescetarian.isChecked = true
                }

            }else{
                checkboxNone.isChecked = true
            }
        }.addOnFailureListener{
            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialog(selectedOptions: String){
        val dialog = createDialog(selectedOptions)
        dialog.show()
    }

    private fun createDialog(selectedOptions: String): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_dialog_dietaryrestriction)

        val selectedOptionsTextView = dialog.findViewById<TextView>(R.id.selectedOptionsTxtviewDR)
        selectedOptionsTextView.text =  selectedOptions

        val okayButton = dialog.findViewById<Button>(R.id.btnOkay)
        okayButton.setOnClickListener {
            val fragment = Home()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
            dialog.dismiss()
        }
        return dialog
    }



}