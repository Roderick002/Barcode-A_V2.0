package com.example.barcode_a

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.barcode_a.databinding.ActivityLoginTabBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginTab : AppCompatActivity() {

    private lateinit var binding: ActivityLoginTabBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val delay : Long = 3000 // 3 seconds delay
    var quit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        firebaseAuth = FirebaseAuth.getInstance()
         binding.btnOpenSignUp.setOnClickListener{
             val intent = Intent(this, RegisterTab::class.java)
             startActivity(intent)
         }



        binding.btnSignIn.setOnClickListener{

            val email = binding.etSignInEmail.text.toString()
            val password = binding.etSignInPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){

                            val userName = email.replace(Regex("[@.]"), "")
                            readData(userName)

                        }else{
                            if (it.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted."){
                                Toast.makeText(this , "Invalid E-mail!" , Toast.LENGTH_SHORT).show()
                            }else if (it.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password."){
                                Toast.makeText(this , "Invalid Password!" , Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this , it.exception.toString() , Toast.LENGTH_SHORT).show()
                            }

                        }
                    }

                }else{
                    Toast.makeText(this , "Empty Fields Are Not Allowed!" , Toast.LENGTH_SHORT).show()
                }
            }

        val passwordEditText = binding.etSignInPassword
        passwordEditText.setOnClickListener {
            onPasswordVisibilityClicked(passwordEditText)
        }

    }
    private fun onPasswordVisibilityClicked(passwordEditText: EditText) {
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

    private fun readData(userName: String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(userName).get().addOnSuccessListener {

            if(it.exists()){
                val userType = it.child("userType").value.toString()
                if (userType == "Personal"){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this , "Logged In Successfully!" , Toast.LENGTH_SHORT).show()
                } else if (userType == "Manufacturer"){
                    val intent = Intent(this, MainActivityManufacturer::class.java)
                    startActivity(intent)
                    Toast.makeText(this , "Logged In Successfully!" , Toast.LENGTH_SHORT).show()
                }

            }else{
                Toast.makeText(this , "User Does Not Exist!" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this  , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }


    //Quit Application
    override fun onBackPressed() {

        if (quit == false){
            Toast.makeText(this, "Press Again To Quit", Toast.LENGTH_SHORT).show()
            quit = true

            val handler = Handler()
            handler.postDelayed({
                quit = false
            }, delay)
        }
        else{
            finishAffinity()
        }
    }
}