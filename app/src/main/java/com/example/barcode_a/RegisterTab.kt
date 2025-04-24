package com.example.barcode_a

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.WindowManager
import android.widget.Button
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
import androidx.core.graphics.toColorInt

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

        // For custom toggle button
        val btnManufacturer = binding.btnManufacturer;
        val btnPersonal = binding.btnPersonal;

        firebaseAuth = FirebaseAuth.getInstance()

        binding.backButton.setOnClickListener {
            val intent = Intent(this, LoginTab::class.java)
            startActivity(intent)
            finish() // Optional: prevents user from returning to this screen via back
        }

        userType = "Manufacturer";

        btnManufacturer.setOnClickListener {
            btnManufacturer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0)
            btnPersonal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.uncheck, 0)
            userType = "Manufacturer"
        }

        btnPersonal.setOnClickListener {
            btnPersonal.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check, 0)
            btnManufacturer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.uncheck, 0)
            userType = "Personal"
        }

        // For viewing password
        var isPasswordVisible = false
        val togglePasswordVisibility = binding.btnTogglePassword

        togglePasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                binding.etSignUpPassword.transformationMethod = null
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            } else {
                binding.etSignUpPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
            // Keep cursor at the end after changing input type
            binding.etSignUpPassword.setSelection(binding.etSignUpPassword.text?.length ?: 0)
        }

        var isConfirmPasswordVisible = false
        val toggleConfirmPasswordVisibility = binding.btnToggleCfPassword;

        toggleConfirmPasswordVisibility.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible

            if (isConfirmPasswordVisible) {
                binding.etSignUpConfirmPassword.transformationMethod = null
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
            } else {
                binding.etSignUpConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
            }
            // Keep cursor at the end after changing input type
            binding.etSignUpPassword.setSelection(binding.etSignUpPassword.text?.length ?: 0)
        }

        binding.btnSignUp.setOnClickListener() {

            val errorColorInt = "#FF4D4D".toColorInt()
            val firstName = binding.etSignUpFirstname.text.toString().trim()
            val lastName = binding.etSignUpLastname.text.toString().trim()
            val email = binding.etSignUpEmail.text.toString().trim()
            val password = binding.etSignUpPassword.text.toString()
            val confirmpass = binding.etSignUpConfirmPassword.text.toString()

            val passwordEditText = binding.etSignUpPassword
            val confirmPasswordEditText = binding.etSignUpConfirmPassword

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
                    binding.etSignUpEmail.background.setTint(errorColorInt)
                    binding.etSignUpEmail.setHintTextColor(errorColorInt)
                }
                if (password.isEmpty()) {
                    binding.etSignUpPassword.background.setTint(errorColorInt)
                    binding.etSignUpPassword.setHintTextColor(errorColorInt)
                }
                if (password.length < 6){
                    passwordEditText.background.setTint(errorColorInt);
                    passwordEditText.setHintTextColor(errorColorInt);
                    passwordEditText.setText("");
                    passwordEditText.hint="Must be at least 6 characters";
                }
                if (firstName.isEmpty()) {
                    binding.etSignUpFirstname.background.setTint(errorColorInt)
                    binding.etSignUpFirstname.setHintTextColor(errorColorInt)
                }
                if (lastName.isEmpty()) {
                    binding.etSignUpLastname.background.setTint(errorColorInt)
                    binding.etSignUpLastname.setHintTextColor(errorColorInt)
                }
                if (confirmpass.isEmpty()) {
                    confirmPasswordEditText.background.setTint(errorColorInt)
                    confirmPasswordEditText.setHintTextColor(errorColorInt)
                }
                if (confirmpass != password) {
                    confirmPasswordEditText.hint = "Passwords do not match";
                    confirmPasswordEditText.setHintTextColor(errorColorInt);
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
