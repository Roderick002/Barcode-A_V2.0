package com.example.barcode_a

import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.barcode_a.databinding.ActivityLoginTabBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginTab : AppCompatActivity() {

    private lateinit var binding: ActivityLoginTabBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val CAMERA_REQUEST_CODE = 101

    private val delay : Long = 3000 // 3 seconds delay
    var quit = false

    private lateinit var loginTimer: CountDownTimer
    private var loginAttempts = 0
    private var isLoginBlocked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginTabBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupPermissions()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        // Start the timer before calling the signInWithEmailAndPassword method
        startLoginTimer()

        firebaseAuth = FirebaseAuth.getInstance()
         binding.btnOpenSignUp.setOnClickListener{
             cancelLoginTimer()
             val intent = Intent(this, RegisterTab::class.java)
             startActivity(intent)
         }

        binding.btnSignIn.setOnClickListener{

            startLoading(100)

            val email = binding.etSignInEmail.text.toString()
            val password = binding.etSignInPassword.text.toString()

            if (!isLoginBlocked) {
                if (loginAttempts < 5) {
                    if (email.isNotEmpty() && password.isNotEmpty()){
                        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                            if (it.isSuccessful){
                                cancelLoginTimer()
                                val uID = firebaseAuth.currentUser?.uid;
                                if (uID != null) {
                                    readData(uID)
                                }
                                loginAttempts = 0
                                startLoading(2000)
                            }else{
                                loginAttempts++
                                val remaining = 5 - loginAttempts;
                                Toast.makeText(this , "You have " + remaining + " remaining login attempt/s" , Toast.LENGTH_SHORT).show()
                            }
                        }

                    }else{
                        if (email.isEmpty()){
                            binding.etSignInEmail.setError("Email is Required");
                        }
                        if (password.isEmpty()){
                            binding.etSignInPassword.setError("Password is Required");
                        }
                    }
                } else {
                    blockLoginAttempts() // Block login attempts and set a waiting period of 30 seconds before allowing login again
                    Toast.makeText(this, "You have exceeded the maximum number of login attempts. Please wait...", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "You have exceeded the maximum number of login attempts. Please wait...", Toast.LENGTH_SHORT).show()
            }

        }

        val passwordEditText = binding.etSignInPassword
        val btnTogglePass = binding.btnTogglePassword
        var passwordVisible = false

        btnTogglePass.setOnClickListener {
            passwordVisible = !passwordVisible

            if (passwordVisible){
                //show password
                passwordEditText.transformationMethod = null
                btnTogglePass.setImageResource(R.drawable.ic_visibility_on)
            }else{
                passwordEditText.transformationMethod = PasswordTransformationMethod()
                btnTogglePass.setImageResource(R.drawable.ic_visibility_off)
            }
            passwordEditText.setSelection(passwordEditText.text.length)
        }
    }

    private fun startLoginTimer() {
        loginTimer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do something on each tick if needed (e.g., show a progress indicator)
            }

            override fun onFinish() {
                // Cancel the authentication process if the timer finishes before authentication is complete
                firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.signOut()
                cancelLoginTimer()

                Toast.makeText(this@LoginTab, "Login timed out.", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
        }
        loginTimer.start()
    }
    private fun cancelLoginTimer() {
        if (::loginTimer.isInitialized) {
            loginTimer.cancel()
        }
    }

    private fun blockLoginAttempts() {
        isLoginBlocked = true
        loginAttempts = 0 // Reset the login attempts counter when blocking login attempts
        val blockTimer = object : CountDownTimer(15000, 1000) { // 30 seconds waiting period
            override fun onTick(millisUntilFinished: Long) {
                // Do something on each tick if needed (e.g., show a countdown)
            }
            override fun onFinish() {
                isLoginBlocked = false // Unblock login attempts after the waiting period
            }
        }
        blockTimer.start()
    }

    private fun readData(uID: String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(uID).get().addOnSuccessListener {
            if(it.exists()){
                val userType = it.child("userType").value.toString()
                if (userType == "Personal"){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else if (userType == "Manufacturer"){
                    val intent = Intent(this, MainActivityManufacturer::class.java)
                    startActivity(intent)
                }
                cancelLoginTimer()
            }else{
                Toast.makeText(this , "User Does Not Exist!" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this  , "User Record Cannot Be Found" , Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {

        if (quit == false){
            Toast.makeText(this, "Press/Slide Again To Quit", Toast.LENGTH_SHORT).show()
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

    //Camera Permission
    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this  , arrayOf( android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "You need the camera permission", Toast.LENGTH_SHORT).show()
                }else {
                    //successful
                }
            }
        }
    }

    private fun startLoading(duration : Long){
        //Progress Bar
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val targetWidth = progressBar.width
        val currentWidth = 0

        progressBar.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE

        val valueAnimator = ValueAnimator.ofInt(currentWidth, targetWidth)
        valueAnimator.addUpdateListener { animation ->
            val layoutParams = progressBar.layoutParams
            layoutParams.width = animation.animatedValue as Int
            progressBar.layoutParams = layoutParams
        }
        valueAnimator.duration = duration
        valueAnimator.start()
        //End of Progress Bar
    }
}