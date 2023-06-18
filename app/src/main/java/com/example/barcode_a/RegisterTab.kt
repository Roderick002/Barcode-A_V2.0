package com.example.barcode_a

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.barcode_a.databinding.ActivityRegisterTabBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.AuthProvider

class RegisterTab : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterTabBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.btnSignUp.setOnClickListener(){

            val firstName = binding.etSignUpFirstname.text.toString()
            val lastName = binding.etSignUpLastname.text.toString()
            val email = binding.etSignUpEmail.text.toString()
            val userName = binding.etSignUpUsername.text.toString()
            val password = binding.etSignUpPassword.text.toString()
            val confirmpass = binding.etSignUpConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmpass.isNotEmpty()){
                if (password == confirmpass){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            database = FirebaseDatabase.getInstance().getReference("Users")
                            val User = User(firstName, lastName, email)

                            database.child(userName).setValue(User).addOnSuccessListener {
                                Toast.makeText(this , "Account Created Successfully!" , Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener(){
                                Toast.makeText(this , "Account Creation Failed!" , Toast.LENGTH_SHORT).show()
                            }

                            firebaseAuth.signOut()
                            val intent = Intent(this, LoginTab::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this , it.exception.toString() , Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this , "Password doesn't match" , Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this , "Empty Fields Are Not Allowed!" , Toast.LENGTH_SHORT).show()
            }
        }
    }
}