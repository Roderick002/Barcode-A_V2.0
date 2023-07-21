package com.example.barcode_a

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat

class EditProfile : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edit = view.findViewById<ImageView>(R.id.iv_edit)
        val update = view.findViewById<Button>(R.id.btn_update)
        val firstName = view.findViewById<EditText>(R.id.et_firstName)
        val surname = view.findViewById<EditText>(R.id.et_surname)
        val email = view.findViewById<EditText>(R.id.et_email)
        val password = view.findViewById<EditText>(R.id.et_password)
        val confirmPassword = view.findViewById<EditText>(R.id.et_confirmPassword)

        //edit button function
        edit.setOnClickListener {
            firstName.isEnabled = true
            surname.isEnabled = true
            email.isEnabled = true
            password.isEnabled = true
            confirmPassword.isEnabled = true
            update.isEnabled = true
            update.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            Toast.makeText(requireContext(), "The fields are now enabled",
                Toast.LENGTH_SHORT).show()
        }

        //update button
        update.setOnClickListener {
            val updatedFN = firstName.text.toString()
            val updatedSN = surname.text.toString()
            val updatedEmail = email.text.toString()
            val updatedPass = password.text.toString()
            update.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_gray))
            Toast.makeText(requireContext(), "Profile updated: $updatedFN, $updatedSN, $updatedEmail, $updatedPass",
                Toast.LENGTH_SHORT).show()

            // Disable the edit mode and update the UI accordingly
            firstName.isEnabled = false
            surname.isEnabled = false
            email.isEnabled = false
            password.isEnabled = false
            confirmPassword.isEnabled = false
            update.isEnabled = false
        }

        val passwordEditText = view.findViewById<EditText>(R.id.et_password)
        passwordEditText.setOnClickListener {
            onPasswordVisibilityClicked(passwordEditText)
        }
        val confirmEditText = view.findViewById<EditText>(R.id.et_confirmPassword)
        confirmEditText.setOnClickListener {
            onPasswordVisibilityClicked(confirmEditText)
        }




        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = Home()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun onPasswordVisibilityClicked(passwordEditText: EditText){
        val visibilityToggleDrawable = if (passwordEditText.transformationMethod == null) {
            // Password is currently visible, so hide it
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            R.drawable.ic_visibility_off // Set drawable for hiding the password
        } else {
            // Password is currently hidden, so show it
            passwordEditText.transformationMethod = null
            R.drawable.ic_visibility_on // Set drawable for showing the password
        }
        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, visibilityToggleDrawable, 0)
    }


}