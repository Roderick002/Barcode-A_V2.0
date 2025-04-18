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
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DietaryRestriction : Fragment() {


    private lateinit var buttonSave: Button
    private lateinit var rbVegetarian: RadioButton
    private lateinit var rbVegan: RadioButton
    private lateinit var rbPollutarian: RadioButton
    private lateinit var rbPescetarian: RadioButton
    private lateinit var rbNone: RadioButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  database : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dietary_restrcition, container, false)

        buttonSave = view.findViewById(R.id.btnDoneDR)

        rbVegetarian = view.findViewById(R.id.rbVegetarian)
        rbVegan =  view.findViewById(R.id.rbVegan)
        rbPollutarian = view.findViewById(R.id.rbPollutarian)
        rbPescetarian = view.findViewById(R.id.rbPescetarian)
        rbNone = view.findViewById(R.id.rbNone)

        val buttonSave = view.findViewById<Button>(R.id.btnDoneDR)
        buttonSave.setOnClickListener {
            //saveData()
            saveDataRB()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val userName = firebaseAuth.currentUser?.uid.toString()
        readData(userName)

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
    }

    //for radio buttons
    private fun saveDataRB(){
        val selectedOption: String?
        var dietary1: String? = null
        var dietary2: String? = null
        var dietary3: String? = null
        var dietary4: String? = null

        when {
            rbNone.isChecked -> {
                selectedOption = "None"
            }
            rbVegetarian.isChecked -> {
                selectedOption = "Vegetarian"
                dietary1 = "Vegetarian"
            }
            rbVegan.isChecked -> {
                selectedOption = "Vegan"
                dietary2 = "Vegan"
            }
            rbPollutarian.isChecked -> {
                selectedOption = "Pollutarian"
                dietary3 = "Pollutarian"
            }
            rbPescetarian.isChecked -> {
                selectedOption = "Pescetarian"
                dietary4 = "Pescetarian"
            }
            else -> {
                Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
                return
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val userName = firebaseAuth.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance().getReference("Dietaries")
        val dietary = Dietary(dietary1, dietary2, dietary3, dietary4)
        database.child(userName).setValue(dietary).addOnSuccessListener {
            Toast.makeText(activity, "Dietary Restriction Updated!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener() {
            Toast.makeText(activity, "Dietary Restriction Update Failed!", Toast.LENGTH_SHORT).show()
        }
        showDialog(selectedOption)
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
                    rbVegetarian.isChecked = true
                }
                if(vegan != "null"){
                    rbVegan.isChecked = true
                }
                if(pollutarian != "null"){
                    rbPollutarian.isChecked = true
                }
                if(pescetarian != "null"){
                    rbPescetarian.isChecked = true
                }

            }else{
                rbNone.isChecked = true
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