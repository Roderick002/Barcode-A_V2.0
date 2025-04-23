package com.example.barcode_a

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.barcode_a.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Set default fragment
        replaceFragment(Home())

        // FAB click behavior
        binding.fabScan.setOnClickListener {
            replaceFragment(Scan())
            binding.fabText.visibility = View.VISIBLE
            binding.fabScan.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimaryDark)

            // Clear selection of BottomNav
            clearBottomNavSelection()
        }

        // Bottom navigation listener
        binding.bottomNavigationView2.setOnItemSelectedListener {
            resetFabState() // Reset FAB to neutral

            when (it.itemId) {
                R.id.home -> replaceFragment(Home())
                R.id.search -> replaceFragment(Search())
            }
            true
        }

        // Insets padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.frameLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom
            )
            insets
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }

    private fun resetFabState() {
        binding.fabText.visibility = View.GONE
        binding.fabScan.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
    }

    private fun clearBottomNavSelection() {
        // Programmatically clear bottom nav selection
        binding.bottomNavigationView2.menu.setGroupCheckable(0, true, false)
        for (i in 0 until binding.bottomNavigationView2.menu.size()) {
            binding.bottomNavigationView2.menu.getItem(i).isChecked = false
        }
        binding.bottomNavigationView2.menu.setGroupCheckable(0, true, true)
    }
}
