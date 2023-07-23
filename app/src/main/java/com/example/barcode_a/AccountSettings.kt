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
import com.example.barcode_a.databinding.FragmentAccountSettingsBinding
import com.example.barcode_a.databinding.FragmentHomeBinding
import com.example.barcode_a.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AccountSettings : Fragment() {

    private var _binding : FragmentAccountSettingsBinding? = null
    private  val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edit = view.findViewById<ImageView>(R.id.iv_edit)
        val update = view.findViewById<Button>(R.id.btn_update)
        val firstName = view.findViewById<EditText>(R.id.et_firstName)
        val surname = view.findViewById<EditText>(R.id.et_surname)

        firebaseAuth = FirebaseAuth.getInstance()
        val emaiL = firebaseAuth.currentUser?.email.toString()
        val userName = emaiL.replace(Regex("[@.]"), "")
        readData(userName)

        //edit button function
        edit.setOnClickListener {
            firstName.isEnabled = true
            surname.isEnabled = true
            update.isEnabled = true
            update.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
            Toast.makeText(requireContext(), "The fields are now enabled",
                Toast.LENGTH_SHORT).show()
        }

        //update button
        update.setOnClickListener {
            update.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_gray))
            val updatedFN = firstName.text.toString()
            val updatedSN = surname.text.toString()
            val userinfo = UserProfile(updatedFN, updatedSN, userName, "Manufacturer")


            database = FirebaseDatabase.getInstance().getReference("Users")
            database.child(userName).setValue(userinfo).addOnSuccessListener {
                //success
            }.addOnFailureListener(){
                Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(activity , "User Profile Updated Successfully!" , Toast.LENGTH_SHORT).show()


            // Disable the edit mode and update the UI accordingly
            firstName.isEnabled = false
            surname.isEnabled = false
            update.isEnabled = false
        }

        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = Home_Manufacturer()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun readData(userName: String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(userName).get().addOnSuccessListener {

            if(it.exists()){

                val firstname = it.child("firstName").value.toString()
                val lastname = it.child("lastName").value.toString()

                binding.etFirstName.text.append(firstname)
                binding.etSurname.text.append(lastname)
                binding.tvDisplay.text = "$firstname $lastname"



            }else{
                Toast.makeText(activity , "User Does Not Exist!" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }

}