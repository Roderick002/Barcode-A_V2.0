package com.example.barcode_a

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

class AlarmsNotification : Fragment() {

    private lateinit var binding: FragmentAlarmsNotificationBinding
    private lateinit var medicalSelection: String
    private lateinit var dietarySelection: String
    private lateinit var allergiesSelection: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_alarms_notification, container, false)
        binding = FragmentAlarmsNotificationBinding.inflate(
            inflater,
            container,
            false
        )

        binding.rgMedical.setOnCheckedChangeListener { _, checkedId ->
            medicalSelection = when (checkedId){
                R.id.rb_rgm_alarmnotif -> "Alarms and Notification"
                R.id.rb_rgm_notif -> "Notifications Only"
                R.id.rb_rgm_disable -> "Disable alarms and Notifications"
                else -> ""
            }
            showToast("Medical: $medicalSelection")
        }

        binding.rgDiertary.setOnCheckedChangeListener { _, checkedId ->
            dietarySelection = when (checkedId){
                R.id.rb_rgd_alarmsnotif -> "Alarms and Notification"
                R.id.rb_rgd_notif -> "Notifications Only"
                R.id.rb_rgd_disable -> "Disable alarms and Notifications"
                else -> ""
            }
            showToast("Dietary: $dietarySelection")
        }

        binding.rgAllergies.setOnCheckedChangeListener { _, checkedId ->
            allergiesSelection = when(checkedId){
                R.id.rb_rga_alarmsnotif -> "Alarms and Notification"
                R.id.rb_rga_notif -> "Notifications Only"
                else -> ""
            }
            showToast("Allergies: $allergiesSelection")
        }
        return binding.root
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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