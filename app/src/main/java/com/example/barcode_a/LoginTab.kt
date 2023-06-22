package com.example.barcode_a

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.barcode_a.databinding.ActivityLoginTabBinding
import com.example.barcode_a.databinding.ActivityRegisterTabBinding
import com.google.firebase.auth.FirebaseAuth

class LoginTab : AppCompatActivity() {

    private lateinit var binding: ActivityLoginTabBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginTabBinding.inflate(layoutInflater)
        setContentView(binding.root)



        firebaseAuth = FirebaseAuth.getInstance()
         binding.tvSignInRegister.setOnClickListener{
             val intent = Intent(this, RegisterTab::class.java)
             startActivity(intent)
         }

        binding.btnSignIn.setOnClickListener{

            val email = binding.etSignInEmail.text.toString()
            val password = binding.etSignInPassword.text.toString()


            if (email.isNotEmpty() && password.isNotEmpty()){

                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(this, ContainerActivity::class.java)
                            startActivity(intent)
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

    }
    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }
    override fun onBackPressed() {
        finishAffinity()
    }



}