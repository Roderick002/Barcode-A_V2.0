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

class DietaryRestriction : Fragment() {

    private lateinit var checkboxVegetarian: CheckBox
    private lateinit var checkboxVegan: CheckBox
    private lateinit var checkboxPollutarian: CheckBox
    private lateinit var checkboxPescetarian: CheckBox
    private lateinit var checkboxNone: CheckBox
    private lateinit var buttonSave: Button

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
    private fun saveData() {
        val selectedOptions = mutableListOf<String>()

        if (checkboxVegetarian.isChecked) {
            selectedOptions.add("Vegetarian")
        }
        if (checkboxVegan.isChecked) {
            selectedOptions.add("Vegan")
        }
        if (checkboxPollutarian.isChecked) {
            selectedOptions.add("Pollutarian")
        }
        if (checkboxPescetarian.isChecked) {
            selectedOptions.add("Pescetarian")
        }
        if (checkboxNone.isChecked){
            selectedOptions.add("None")
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
        dialog.setContentView(R.layout.custom_dialog_dietaryrestriction)

        val selectedOptionsTextView = dialog.findViewById<TextView>(R.id.selectedOptionsTxtviewDR)
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
        val backbutton = view.findViewById<ImageView>(R.id.drwbackDR)
        backbutton.setOnClickListener {
            val fragment = HealthPreference()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

}