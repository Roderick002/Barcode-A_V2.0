package com.example.barcode_a

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ContainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LogInOptions())
            .commit()
    }

}