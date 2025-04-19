package com.example.barcode_a

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.barcode_a.databinding.ActivityRegisterTabBinding
import com.example.barcode_a.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import java.security.AuthProvider

class RegisterTab : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterTabBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        binding = ActivityRegisterTabBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        userType = ""
        binding.rdgUserType.setOnCheckedChangeListener { _, checkedId ->
            userType = when (checkedId){
                R.id.rbPersonal -> "Personal"
                R.id.rbManufacturer -> "Manufacturer"
                else -> ""
            }
        }

        binding.btnSignUp.setOnClickListener() {

            val firstName = binding.etSignUpFirstname.text.toString().trim()
            val lastName = binding.etSignUpLastname.text.toString().trim()
            val email = binding.etSignUpEmail.text.toString().trim()
            val password = binding.etSignUpPassword.text.toString()
            val confirmpass = binding.etSignUpConfirmPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmpass.isNotEmpty() && userType.isNotBlank() && password.length >= 6 && password == confirmpass) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            sendVerificationEmail()
                        } else {
                            Toast.makeText(
                                this,
                                it.exception.toString(),
                                Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }else{
                if (email.isEmpty()) {
                    binding.etSignUpEmail.setError("Email is Required");
                }
                if (password.isEmpty()) {
                    binding.etSignUpPassword.setError("Password is Required");
                }
                if (password.length < 6){
                    binding.etSignUpPassword.setError("Password Must be >= 6 Characters");
                }
                if (firstName.isEmpty()) {
                    binding.etSignUpFirstname.setError("This Field is Required");
                }
                if (lastName.isEmpty()) {
                    binding.etSignUpLastname.setError("This Field is Required");
                }
                if (confirmpass.isEmpty()) {
                    binding.etSignUpConfirmPassword.setError("This Field is Required");
                }
                if (confirmpass != password) {
                    binding.etSignUpConfirmPassword.setError("Password Mismatch");
                }
                if (userType.isBlank()) {
                    Toast.makeText(
                        this,
                        "Select a User Type",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                return@setOnClickListener;
            }
        }
    }

    private fun sendVerificationEmail() {
        val user = firebaseAuth.currentUser

        user?.let {
            it.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firstName = binding.etSignUpFirstname.text.toString().trim()
                        val lastName = binding.etSignUpLastname.text.toString().trim()
                        val email = binding.etSignUpEmail.text.toString().trim()
                        val uID = user.uid
                        val user = User(firstName, lastName, email, userType)

                        database = FirebaseDatabase.getInstance().getReference("Users")
                        database.child(uID).setValue(user).addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Account Created Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            if(userType == "Personal"){
                                val notification = Notification("None", "None", "None")
                                database = FirebaseDatabase.getInstance().getReference("AlarmsNotification")
                                database.child(uID).setValue(notification).addOnSuccessListener {
                                    //success
                                }.addOnFailureListener(){
                                    Toast.makeText(this , "Database ERROR!" , Toast.LENGTH_SHORT).show()
                                }
                            }

                        }.addOnFailureListener() {
                            Toast.makeText(
                                this,
                                "Account Creation Failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        firebaseAuth.signOut()
                        val intent = Intent(this, LoginTab::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to send verification email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this, LoginTab::class.java)
        startActivity(intent)
    }
}
