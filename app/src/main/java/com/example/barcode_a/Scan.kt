package com.example.barcode_a

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.barcode_a.view.AlarmReceiver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Scan : Fragment() {


    private lateinit var codeScanner : CodeScanner
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    private lateinit var alarmManager: AlarmManager
    private lateinit var vibrator: Vibrator

    private lateinit var tvProductName: TextView
    private lateinit var tvIngredients: TextView
    private lateinit var tvAllergens: TextView
    private lateinit var tvNoteLabel: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnScan = view.findViewById<Button>(R.id.btnScannerScan)

        //For alarms and notification
        alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)

        btnScan.isVisible = false
        firebaseAuth = FirebaseAuth.getInstance()

        //Get username
        val email = firebaseAuth.currentUser?.email.toString()
        val userName = email.replace(Regex("[@.]"), "")


        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {

                Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
                getProductData(it.text, userName)
                btnScan.isVisible = true

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }
        btnScan.setOnClickListener {

            codeScanner.startPreview()
            btnScan.isVisible = false

                }
            }
            codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                activity.runOnUiThread {
                    Toast.makeText(activity, "Camera initialization error: ${it.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
        }

        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = Home()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getProductData(product: String, userName: String){

        database = FirebaseDatabase.getInstance().getReference("Product Information")
        database.child(product).get().addOnSuccessListener {

            if(it.exists()){

                val productName = it.child("productName").value.toString()
                val allergens = it.child("allergens").value.toString()
                val ingredients = it.child("ingredients").value.toString()

                checkAllergens(allergens, userName)


            }else{
                Toast.makeText(activity , "User Does Not Exist!" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAllergens(allergens: String, userName: String){
        database = FirebaseDatabase.getInstance().getReference("Allergens")
        database.child(userName).get().addOnSuccessListener {
            if(it.exists()){
                val allergen = allergens.replace(Regex("[,]"), " ")
                val nuts = it.child("allergy1").value.toString()
                val gluten = it.child("allergy2").value.toString()
                val eggs = it.child("allergy3").value.toString()
                val crustaceans = it.child("allergy4").value.toString()
                val dairyproducts = it.child("allergy5").value.toString()
                val others = it.child("allergy6").value.toString()

                var alarm = ""

                if (nuts == "Nuts" && allergen.contains(nuts) ){
                    triggerAlarm()
                }

            }else{
                Toast.makeText(activity , "User Does Not Exist!" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }
    private fun triggerAlarm(){
        val delayInMilliseconds = 5000 // 5 seconds

        setAlarm(delayInMilliseconds)
        vibrateDevice()
    }
    private fun setAlarm(delayInMilliseconds: Int) {
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerTime = SystemClock.elapsedRealtime() + delayInMilliseconds
        alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)
    }

    private fun vibrateDevice() {
        if (vibrator.hasVibrator()) {
            val pattern = longArrayOf(0, 500, 200, 500) // Vibration pattern (wait, vibrate, wait, vibrate)
            vibrator.vibrate(pattern, -1)
        }
    }



    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        vibrator.cancel()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_scan, container, false)

        val btnOpenPopup:Button = view.findViewById(R.id.btn_openPopup)
        btnOpenPopup.setOnClickListener {
            showPopup()
        }
        return view
    }

    private fun showPopup(){
        val inflater = requireActivity().layoutInflater
        val popupView = inflater.inflate(R.layout.popup_scan, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(popupView)
        val alertDialog = alertDialogBuilder.create()
        val drw_backScan = popupView.findViewById<ImageView>(R.id.drw_backScan)
        drw_backScan.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()


        tvProductName = popupView.findViewById(R.id.tvProductName)
        tvIngredients = popupView.findViewById(R.id.tvIngredients)
        tvAllergens = popupView.findViewById(R.id.tvAllergens)
        tvNoteLabel = popupView.findViewById(R.id.tvNoteLabel)

        tvIngredients.text = getString(R.string.ingredients_sample)
        tvAllergens.text = getString(R.string.allergens_sample)
        tvNoteLabel.text = "Note: " + getString(R.string.note_sample)


        // Set a dismiss listener to handle clean-up when the AlertDialog is dismissed
        alertDialog.setOnDismissListener { }
    }

    //Scanner Function

}