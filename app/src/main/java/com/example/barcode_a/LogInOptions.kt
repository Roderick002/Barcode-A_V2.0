package com.example.barcode_a

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.barcode_a.R
import com.google.firebase.auth.FirebaseAuth


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LogInOptions : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_log_in_options, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnPersonal = view.findViewById<Button>(R.id.btn_personal)
        val btnManufacturer = view.findViewById<Button>(R.id.btn_manu)
        btnPersonal.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            Toast.makeText(activity, "Logged In Successfully!", Toast.LENGTH_SHORT).show()
        }

        //Sign Out Function
        btnManufacturer.setOnClickListener(){
            val intent = Intent(activity, MainActivityManufacturer::class.java)
            startActivity(intent)
        }
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LogInOptions().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}