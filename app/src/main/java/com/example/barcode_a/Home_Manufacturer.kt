package com.example.barcode_a

import android.os.Bundle
import android.content.Intent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Home_Manufacturer : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val signOut = view.findViewById<Button>(R.id.btnSignout)
        val activity = requireActivity()

        //Sign Out Function
        signOut.setOnClickListener(){
            firebaseAuth.signOut()
            Toast.makeText(activity , "Account Signed Out!" , Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginTab::class.java)
            startActivity(intent)
        }

        val layoutProductInfo = view.findViewById<LinearLayout>(R.id.layoutProductInfo)
        layoutProductInfo.setOnClickListener {
            val fragment = ProductInformation()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
        val layoutAccSettings = view.findViewById<LinearLayout>(R.id.layoutAccSetting)
        layoutAccSettings.setOnClickListener {
            val fragment = AccountSettings()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
        val layoutQR = view.findViewById<LinearLayout>(R.id.layoutQR)
        layoutQR.setOnClickListener {
            val fragment = GenerateQR()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home__manufacturer, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home_Manufacturer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home_Manufacturer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}