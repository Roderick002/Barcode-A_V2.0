package com.example.barcode_a

import android.os.Bundle
import android.content.Intent
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Home_Manufacturer : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    private var param1: String? = null
    private var param2: String? = null


    private val delay : Long = 3000 // 3 seconds delay
    var quit = false

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val signOut = view.findViewById<Button>(R.id.btnSignout)
        val activity = requireActivity()

        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (quit == false){
                Toast.makeText(activity, "Press Again To Quit", Toast.LENGTH_SHORT).show()
                quit = true

                val handler = Handler()
                handler.postDelayed({
                    quit = false
                }, delay)
            }
            else{
                ActivityCompat.finishAffinity(activity)
            }
        }

        //Sign Out Function
        signOut.setOnClickListener(){
            firebaseAuth.signOut()
            Toast.makeText(activity , "Account Signed Out!" , Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginTab::class.java)
            startActivity(intent)
        }

        val layoutProductInfo = view.findViewById<LinearLayout>(R.id.layoutProductInfo)
        layoutProductInfo.setOnClickListener {
            val fragment = ProductInformation()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
        val layoutAccSettings = view.findViewById<LinearLayout>(R.id.layoutAccSetting)
        layoutAccSettings.setOnClickListener {
            val fragment = AccountSettings()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
        val layoutQR = view.findViewById<LinearLayout>(R.id.layoutQR)
        layoutQR.setOnClickListener {
            val fragment = GenerateQR()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }

        //side navigation
        drawerLayout = view.findViewById(R.id.drawer_layout_manu)
        val navView: NavigationView = view.findViewById(R.id.nav_viewside_manu)

        toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_aboutus->Toast.makeText(requireContext(), "About Us", Toast.LENGTH_SHORT).show()
                R.id.nav_logout->{
                    firebaseAuth.signOut()
                    Toast.makeText(activity , "Account Signed Out!" , Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, LoginTab::class.java)
                    startActivity(intent)}
            }
            true
        }
        val menuImageView: ImageView = view.findViewById(R.id.imageMenu_manu)
        menuImageView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home__manufacturer, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home_Manufacturer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home_Manufacturer().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}