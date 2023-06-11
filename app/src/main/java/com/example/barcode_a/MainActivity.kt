package com.example.barcode_a

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nextButton = findViewById<Button>(R.id.btnNextTab)

        nextButton.setOnClickListener(){
            val intent = Intent(this, LoginTab::class.java)
            startActivity(intent)

        }
    }
}