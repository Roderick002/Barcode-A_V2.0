package com.example.barcode_a

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScannerView
import com.example.barcode_a.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Home : Fragment() {



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

        val btnHomeSignOut = view.findViewById<Button>(R.id.btnHomeSignOut)
        val activity = requireActivity()

        firebaseAuth = FirebaseAuth.getInstance()

        //Sign Out Function
        btnHomeSignOut.setOnClickListener(){
            firebaseAuth.signOut()
            Toast.makeText(activity , "Account Signed Out!" , Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginTab::class.java)
            startActivity(intent)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}