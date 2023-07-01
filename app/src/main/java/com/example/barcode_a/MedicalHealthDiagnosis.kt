package com.example.barcode_a

import android.app.Dialog
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MedicalHealthDiagnosis : Fragment() {

    private lateinit var checkboxDiabetic: CheckBox
    private lateinit var checkboxLactose: CheckBox
    private lateinit var checkboxGastro: CheckBox
    private lateinit var checkboxHyper: CheckBox
    private lateinit var checkboxOther: CheckBox
    private lateinit var edittextOther: EditText
    private lateinit var buttonSave: Button

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  database : DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_medical_health_diagnosis, container, false)

        checkboxDiabetic = view.findViewById(R.id.checkBoxDiabetic)
        checkboxLactose = view.findViewById(R.id.checkBoxLactose)
        checkboxGastro = view.findViewById(R.id.checkBoxGastro)
        checkboxHyper = view.findViewById(R.id.checkBoxHyperuricemia)
        checkboxOther = view.findViewById(R.id.checkBoxOther)
        edittextOther = view.findViewById(R.id.editTextOther)
        buttonSave = view.findViewById(R.id.btnDone)

        val buttonSave = view.findViewById<Button>(R.id.btnDone)
        buttonSave.setOnClickListener {
            saveData()
        }
        return view
    }

    private fun saveData() {
        val selectedOptions = mutableListOf<String>()
        var diagnosis1: String? = null
        var diagnosis2: String? = null
        var diagnosis3: String? = null
        var diagnosis4: String? = null


        if (checkboxDiabetic.isChecked) {
            selectedOptions.add("Diabetic")
            diagnosis1 = "Diabetic"
        }
        if (checkboxLactose.isChecked) {
            selectedOptions.add("Lactose Intolerant")
            diagnosis2 = "Lactose Intolerant"
        }
        if (checkboxGastro.isChecked) {
            selectedOptions.add("GRD")
            diagnosis3 = "GRD"
        }
        if (checkboxHyper.isChecked) {
            selectedOptions.add("Hyperuricemia")
            diagnosis4 = "Hyperuricemia"
        }

        val checkboxOther = view?.findViewById<CheckBox>(R.id.checkBoxOther)
        val editTextOther = view?.findViewById<TextView>(R.id.editTextOther)
        if (checkboxOther?.isChecked == true && !editTextOther?.text.isNullOrBlank()) {
            selectedOptions.add(editTextOther?.text.toString())
        } else if (checkboxOther?.isChecked == true && editTextOther?.text.isNullOrBlank()) {
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

            database = FirebaseDatabase.getInstance().getReference("Diagnosis")

            val diagnosis = Diagnosis(diagnosis1, diagnosis2, diagnosis3, diagnosis4)

            database.child(userName).setValue(diagnosis).addOnSuccessListener {
                Toast.makeText(activity , "Details Updated!" , Toast.LENGTH_SHORT).show()
            }.addOnFailureListener(){
                Toast.makeText(activity , "Update Details Failed!" , Toast.LENGTH_SHORT).show()
            }

            val selectedOptionsString = selectedOptions.joinToString(", ")
            showDialog(selectedOptionsString)
        }
    }

    private fun readData(userName: String){
        database = FirebaseDatabase.getInstance().getReference("Diagnosis")
        database.child(userName).get().addOnSuccessListener {
            if(it.exists()){

                val diabetic = it.child("diagnosis1").value.toString()
                val lactose = it.child("diagnosis2").value.toString()
                val gastro = it.child("allergy3").value.toString()
                val hyper = it.child("diagnosis4").value.toString()
                val others = it.child("diagnosis5").value.toString()
                val editTextOther = view?.findViewById<TextView>(R.id.editTextOther)

                if(diabetic != "null"){
                    checkboxDiabetic.isChecked = true
                }
                if(lactose != "null"){
                    checkboxLactose.isChecked = true
                }
                if(gastro != "null"){
                    checkboxGastro.isChecked = true
                }
                if(hyper != "null"){
                    checkboxHyper.isChecked = true
                }
                if(others != "null"){
                    checkboxOther.isChecked = true
                }
                if(others != "null"){
                    checkboxOther.isChecked = true
                    if (editTextOther != null) {
                        editTextOther.text = others
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
        dialog.setContentView(R.layout.custom_dialog_medicalhealthdiagnosis)

        val selectedOptionsTextView = dialog.findViewById<TextView>(R.id.selectedOptionsTxtviewMHD)
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

        val backbutton = view.findViewById<ImageView>(R.id.drwbackMD)
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


}