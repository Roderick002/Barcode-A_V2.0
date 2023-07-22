package com.example.barcode_a

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Camera
import android.nfc.tech.NfcBarcode
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.view.isVisible
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.barcode_a.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ScanManu : Fragment() {

    private lateinit var codeScanner : CodeScanner
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_manu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnScan = view.findViewById<Button>(R.id.btnScannerScan)

        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)



        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {

                addInfo(it.text)


                codeScanner.apply {
                    camera = CodeScanner.CAMERA_BACK
                    formats = CodeScanner.ALL_FORMATS

                    autoFocusMode = AutoFocusMode.SAFE
                    scanMode = ScanMode.SINGLE
                    isAutoFocusEnabled = true
                    isFlashEnabled = false
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
            val fragment = ProductInformation()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun addInfo(barcode: String) {
        val inflter = LayoutInflater.from(requireContext())
        val v = inflter.inflate(R.layout.add_item, null)
        val addDialog = AlertDialog.Builder(requireContext())
        /**set view*/
        val productName = v.findViewById<EditText>(R.id.productName)
        val productIngre = v.findViewById<EditText>(R.id.productIngredients)
        val productAller = v.findViewById<EditText>(R.id.productAllergens)
        val productBarcode = v.findViewById<EditText>(R.id.productBarcode)
        productBarcode.text.append(barcode)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok",null)

        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            codeScanner.startPreview()
            dialog.dismiss()
        }

        val dialog = addDialog.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val barcodee = productBarcode.text.toString()
            val names = productName.text.toString().trim()
            val ingredients = productIngre.text.toString().trim()
            val allergens = productAller.text.toString().trim()

            if (names.isNotBlank() && ingredients.isNotBlank() && allergens.isNotBlank() && barcodee.isNotBlank()){
                if (isValidFormat(ingredients)){
                    if(isValidFormat((allergens))){

                        database = FirebaseDatabase.getInstance().getReference("ProductInformation")
                        database.child(barcode).get().addOnSuccessListener {

                            if(it.exists()){
                                Toast.makeText(activity , "Product Already Exists!" , Toast.LENGTH_SHORT).show()
                            }else{
                                /**Add Product Info to Database*/
                                val productinfo = UserData(names, ingredients, allergens)
                                database = FirebaseDatabase.getInstance().getReference("ProductInformation")
                                database.child(barcode).setValue(productinfo).addOnSuccessListener {
                                    //success
                                }.addOnFailureListener(){
                                    Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
                                }

                                /**Bind Products to Manufacturers*/
                                firebaseAuth = FirebaseAuth.getInstance()
                                val manuProducts = UserData(names, ingredients, allergens, barcode)
                                //Get username
                                val email = firebaseAuth.currentUser?.email.toString()
                                val userName = email.replace(Regex("[@.]"), "")
                                val manufacturer = "Manufacturer$userName"

                                database = FirebaseDatabase.getInstance().getReference(manufacturer)
                                database.child(names).get().addOnSuccessListener {
                                    if(it.exists()){
                                        Toast.makeText(activity , "Product with same name exists" , Toast.LENGTH_SHORT).show()
                                    }else{
                                        database = FirebaseDatabase.getInstance().getReference(manufacturer)
                                        database.child(names).setValue(manuProducts).addOnSuccessListener {
                                            //success
                                        }.addOnFailureListener(){
                                            Toast.makeText(activity , "Database Error!" , Toast.LENGTH_SHORT).show()
                                        }

                                        Toast.makeText(requireContext(),"Product Added", Toast.LENGTH_SHORT).show()
                                        dialog.dismiss()
                                    }
                                }.addOnFailureListener{
                                    Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.addOnFailureListener{
                            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
                        }

                    }else{
                        Toast.makeText(requireContext(),"Please enter allergens in the format: Allergen, Allergen, Allergen", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(),"Please enter ingredients in the format: Ingredient, Ingredient, Ingredient", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),"Product Information is not added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidFormat(ingredients: String):Boolean{
        val regex = Regex("^[a-zA-Z]+(,\\s[a-zA-Z]+)*$")
        return regex.matches(ingredients)
    }


    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }



}