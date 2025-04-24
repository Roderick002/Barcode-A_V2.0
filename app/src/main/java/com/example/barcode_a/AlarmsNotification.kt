package com.example.barcode_a

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import com.example.barcode_a.databinding.FragmentAlarmsNotificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AlarmsNotification : Fragment() {

    private lateinit var binding: FragmentAlarmsNotificationBinding
    private lateinit var medicalSelection: String
    private lateinit var dietarySelection: String
    private lateinit var allergiesSelection: String

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlarmsNotificationBinding.inflate(
            inflater,
            container,
            false
        )
        readData()

        binding.rgMedical.setOnCheckedChangeListener { _, checkedId ->
            medicalSelection = when (checkedId){
                R.id.rb_rgm_alarmnotif -> "Alarms and Notification"
                R.id.rb_rgm_notif -> "Notifications Only"
                R.id.rb_rgm_disable -> "Disable Alarms and Notifications"
                else -> ""
            }
            addInfo("medical", medicalSelection)
        }

        binding.rgDiertary.setOnCheckedChangeListener { _, checkedId ->
            dietarySelection = when (checkedId){
                R.id.rb_rgd_alarmsnotif -> "Alarms and Notification"
                R.id.rb_rgd_notif -> "Notifications Only"
                R.id.rb_rgd_disable -> "Disable Alarms and Notifications"
                else -> ""
            }
            addInfo("dietary", dietarySelection)
        }

        binding.rgAllergies.setOnCheckedChangeListener { _, checkedId ->
            allergiesSelection = when(checkedId){
                R.id.rb_rga_alarmsnotif -> "Alarms and Notification"
                R.id.rb_rga_notif -> "Notifications Only"
                else -> ""
            }
            addInfo("allergies", allergiesSelection)
        }
        return binding.root

    }

    private fun addInfo(type: String, value: String){
        firebaseAuth = FirebaseAuth.getInstance()
        val userName = firebaseAuth.currentUser?.uid.toString()

        database = FirebaseDatabase.getInstance().getReference("AlarmsNotification/$userName")
        database.child(type).setValue(value).addOnSuccessListener {
            //success
        }.addOnFailureListener(){
            Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun readData(){
        firebaseAuth = FirebaseAuth.getInstance()
        val userName = firebaseAuth.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance().getReference("AlarmsNotification")
        database.child(userName).get().addOnSuccessListener {
            if(it.exists()){

                val medical = it.child("medical").value.toString()
                    if (medical == "Alarms and Notification"){
                        binding.rbRgmAlarmnotif.isChecked = true
                    }else if (medical == "Notifications Only"){
                        binding.rbRgmNotif.isChecked = true
                    }else if (medical == "Disable Alarms and Notifications" ){
                        binding.rbRgmDisable.isChecked = true
                    }

                val dietary = it.child("dietary").value.toString()
                    if (dietary == "Alarms and Notification"){
                        binding.rbRgdAlarmsnotif.isChecked = true
                    }else if (dietary == "Notifications Only"){
                        binding.rbRgdNotif.isChecked = true
                    }else if (dietary == "Disable Alarms and Notifications" ){
                        binding.rbRgdDisable.isChecked = true
                    }

                val allergies = it.child("allergies").value.toString()
                    if (allergies == "Alarms and Notification"){
                        binding.rbRgaAlarmsnotif.isChecked = true
                    }else if (allergies == "Notifications Only") {
                        binding.rbRgaNotif.isChecked = true
                    }

            }else{
                Toast.makeText(activity , "User Does Not Exist!" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backbutton = view.findViewById<ImageView>(R.id.drwback)
        backbutton.setOnClickListener {
            val fragment = Home()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
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

}