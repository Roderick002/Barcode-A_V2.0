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


class Allergies : Fragment() {

    private lateinit var checkboxNuts: CheckBox
    private lateinit var checkboxGluten: CheckBox
    private lateinit var checkboxEggs: CheckBox
    private lateinit var checkboxCrustaceans: CheckBox
    private lateinit var checkboxDairy: CheckBox
    private lateinit var checkboxOtherA: CheckBox
    private lateinit var edittextOtherA: EditText
    private lateinit var buttonSave: Button

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

    private fun saveData(){
        val selectedOptions = mutableListOf<String>()

        if (checkboxNuts.isChecked) {
            selectedOptions.add("Nuts")
        }
        if (checkboxGluten.isChecked) {
            selectedOptions.add("Gluten")
        }
        if (checkboxEggs.isChecked) {
            selectedOptions.add("Eggs")
        }
        if (checkboxCrustaceans.isChecked) {
            selectedOptions.add("Crutaceans")
        }
        if(checkboxDairy.isChecked){
            selectedOptions.add("Dairy Products")
        }

        val checkboxOtherA = view?.findViewById<CheckBox>(R.id.checkBoxOtherA)
        val editTextOtherA = view?.findViewById<TextView>(R.id.editTextOtherA)
        if (checkboxOtherA?.isChecked == true && !editTextOtherA?.text.isNullOrBlank()) {
            selectedOptions.add(editTextOtherA?.text.toString())
        } else if (checkboxOtherA?.isChecked == true && editTextOtherA?.text.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Please enter a value for 'Other'", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (selectedOptions.isEmpty()) {
            Toast.makeText(requireContext(), "Please select an option", Toast.LENGTH_SHORT).show()
        } else {
            val selectedOptionsString = selectedOptions.joinToString(", ")
            showDialog(selectedOptionsString)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    }

}