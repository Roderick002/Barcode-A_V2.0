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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Allergies : Fragment() {

    private lateinit var checkboxNuts: CheckBox
    private lateinit var checkboxGluten: CheckBox
    private lateinit var checkboxEggs: CheckBox
    private lateinit var checkboxCrustaceans: CheckBox
    private lateinit var checkboxDairy: CheckBox
    private lateinit var checkboxOtherA: CheckBox
    private lateinit var edittextOtherA: EditText
    private lateinit var buttonSave: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  database : DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_allergies, container, false)

        checkboxNuts = view.findViewById(R.id.checkBoxNuts)
        checkboxGluten = view.findViewById(R.id.checkBoxGluten)
        checkboxEggs = view.findViewById(R.id.checkBoxEggs)
        checkboxCrustaceans = view.findViewById(R.id.checkBoxCrustaceans)
        checkboxDairy = view.findViewById(R.id.checkboxDairy)
        checkboxOtherA = view.findViewById(R.id.checkBoxOtherA)
        edittextOtherA = view.findViewById(R.id.editTextOtherA)
        buttonSave = view.findViewById(R.id.btnDoneAllergies)

        val buttonSave = view.findViewById<Button>(R.id.btnDoneAllergies)
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
            val fragment = HealthPreference()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
        val backbutton = view.findViewById<ImageView>(R.id.drwbackAllergies)
        backbutton.setOnClickListener {
            val fragment = HealthPreference()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

        val email = firebaseAuth.currentUser?.email.toString()
        val userName = email.replace(Regex("[@.]"), "")
        readData(userName)
    }

    private fun saveData(){
        val selectedOptions = mutableListOf<String>()
        var allergy1: String? = null
        var allergy2: String? = null
        var allergy3: String? = null
        var allergy4: String? = null
        var allergy5: String? = null
        var allergy6: String? = null


        if (checkboxNuts.isChecked) {
            selectedOptions.add("Nuts")
            allergy1 = "Nuts"
        }
        if (checkboxGluten.isChecked) {
            selectedOptions.add("Gluten")
            allergy2 = "Gluten"
        }
        if (checkboxEggs.isChecked) {
            selectedOptions.add("Eggs")
            allergy3 = "Eggs"
        }
        if (checkboxCrustaceans.isChecked) {
            selectedOptions.add("Crustaceans")
            allergy4 = "Crustaceans"
        }
        if(checkboxDairy.isChecked){
            selectedOptions.add("Dairy Products")
            allergy5 = "Dairy Products"
        }

        val checkboxOtherA = view?.findViewById<CheckBox>(R.id.checkBoxOtherA)
        val editTextOtherA = view?.findViewById<TextView>(R.id.editTextOtherA)
        if (checkboxOtherA?.isChecked == true && !editTextOtherA?.text.isNullOrBlank()) {
            selectedOptions.add(editTextOtherA?.text.toString())
            allergy6 = editTextOtherA?.text.toString()
        } else if (checkboxOtherA?.isChecked == true && editTextOtherA?.text.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Please enter a value for 'Other'", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (selectedOptions.isEmpty()) {
            Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
        } else {
            firebaseAuth = FirebaseAuth.getInstance()

            val email = firebaseAuth.currentUser?.email.toString()
            val userName = email.replace(Regex("[@.]"), "")

            database = FirebaseDatabase.getInstance().getReference("Allergens")

            val allergy = Allergy(allergy1, allergy2, allergy3, allergy4, allergy5, allergy6)

            database.child(userName).setValue(allergy).addOnSuccessListener {
                Toast.makeText(activity , "Details Updated!" , Toast.LENGTH_SHORT).show()
            }.addOnFailureListener(){
                Toast.makeText(activity , "Update Details Failed!" , Toast.LENGTH_SHORT).show()
            }

            val selectedOptionsString = selectedOptions.joinToString(", ")
            showDialog(selectedOptionsString)
        }
    }
    private fun readData(userName: String){
        database = FirebaseDatabase.getInstance().getReference("Allergens")
        database.child(userName).get().addOnSuccessListener {
            if(it.exists()){

                val nuts = it.child("allergy1").value.toString()
                val glutten = it.child("allergy2").value.toString()
                val eggs = it.child("allergy3").value.toString()
                val crustaceans = it.child("allergy4").value.toString()
                val dairyProducts = it.child("allergy5").value.toString()
                val others = it.child("allergy6").value.toString()
                val editTextOtherA = view?.findViewById<TextView>(R.id.editTextOtherA)

                if(nuts != "null"){
                    checkboxNuts.isChecked = true
                }
                if(glutten != "null"){
                    checkboxGluten.isChecked = true
                }
                if(eggs != "null"){
                    checkboxEggs.isChecked = true
                }
                if(crustaceans != "null"){
                    checkboxCrustaceans.isChecked = true
                }
                if(dairyProducts != "null"){
                    checkboxDairy.isChecked = true
                }
                if(others != "null"){
                    checkboxOtherA.isChecked = true
                    if (editTextOtherA != null) {
                        editTextOtherA.text = others
                    }
                }

            }else{
                //Initally There Is No Record...
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
        dialog.setContentView(R.layout.custom_dialog_allergies)

        val selectedOptionsTextView = dialog.findViewById<TextView>(R.id.selectedOptionsTxtview)
        selectedOptionsTextView.text =  selectedOptions

        val okayButton = dialog.findViewById<Button>(R.id.btnOkay)
        okayButton.setOnClickListener {
            val fragment = HealthPreference()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
            dialog.dismiss()
        }
        return dialog
    }


}