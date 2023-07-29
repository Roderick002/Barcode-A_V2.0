package com.example.barcode_a

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.provider.ContactsContract.CommonDataKinds.Note
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
import androidx.core.content.ContextCompat.getSystemService
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
import androidx.core.app.NotificationCompat
import com.example.barcode_a.model.Notification


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

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101


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
    fun showNotification(Title: String, Description: String, Content: String) {
        val channelId = "my_channel_id"
        val channelName = "My Channel"
        val notificationId = 1

        // Create a pending intent for the notification
        val intent = Intent(requireContext(), MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_notification)



        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(bitmap)
            .setContentTitle(Title)
            .setContentText(Description)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setStyle(NotificationCompat.BigTextStyle().bigText(Content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Create the notification channel (for Android 8.0 Oreo or higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun getProductData(product: String, userName: String){

        database = FirebaseDatabase.getInstance().getReference("ProductInformation")
        database.child(product).get().addOnSuccessListener {

            var Note = ""

            if(it.exists()){

                val productName = it.child("productName").value.toString()
                val allergens = it.child("allergens").value.toString()
                val ingredients = it.child("ingredients").value.toString()


                //Check Allergen

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

                        var allergenNote = " ALLERGEN: "

                        if (nuts != "null" && allergen.contains(nuts) ){
                            allergenNote = allergenNote.plus("Nuts, ")
                        }
                        if (gluten != "null" && allergen.contains(gluten) ){
                            allergenNote = allergenNote.plus("Gluten, ")
                        }
                        if (eggs != "null" && allergen.contains(eggs) ){
                            allergenNote = allergenNote.plus("Eggs, ")
                        }
                        if (crustaceans != "null" && allergen.contains(crustaceans) ){
                            allergenNote = allergenNote.plus("Crustaceans, ")
                        }
                        if (dairyproducts != "null" && allergen.contains(dairyproducts.replace(Regex("[ ]"), "")) ){
                            allergenNote = allergenNote.plus("Dairy Products, ")
                        }
                        if (others != "null" && allergen.contains(others) ){
                            allergenNote = allergenNote.plus("$others, ")
                        }
                        if (allergenNote == " ALLERGEN: "){
                            allergenNote = ""
                        }
                        Note = Note.plus(allergenNote)


                    }else{
                        //Ignore
                    }

                    //Check Dietary Restrictions

                    database = FirebaseDatabase.getInstance().getReference("Dietaries")
                    database.child(userName).get().addOnSuccessListener {
                        if(it.exists()){
                            val ingredient = ingredients.replace(Regex("[,]"), " ")
                            val vegetarian = it.child("dietary1").value.toString()
                            val vegan = it.child("dietary2").value.toString()
                            val pollutarian = it.child("dietary3").value.toString()
                            val pescetarian = it.child("dietary4").value.toString()


                            var dietaryNote = " DIETARY RESTRICTIONS: "

                            if (vegetarian != "null"){
                                //Insert here all the ingredients that could trigger the condition
                                val vegetarianrestrict = listOf("vegetarian", "Natural Cheddar Cheese", "Buttermilk", "Natural Cheese", "Milk Solids", "Milk Solids: Buttermilk Powder",
                                "Natural Cheese", "Milk Proteins", "Cheese", "Buttermilk Powder", "Milk Powder", "Sodium Caseinate", "Caseinate", "Skim Milk Powder",
                                "Milk", "Skimmed Milk", "Milk Fat",)
                                checkStringsInParagraph(ingredient, vegetarianrestrict)
                                val note = context?.let { it1 -> readString(it1, "vegetarian") }
                                dietaryNote = dietaryNote.plus(note)
                            }
                            if (vegan != "null"){
                                //Insert here all the ingredients that could trigger the condition
                                val veganrestrict = listOf("vegan", "Natural Cheddar Cheese", "Buttermilk", "Natural Cheese", "Milk Solids", "Milk Solids: Buttermilk Powder",
                                    "Natural Cheese", "Milk Proteins", "Cheese", "Buttermilk Powder", "Milk Powder","Annato" ,"Sodium Caseinate", "Caseinate", "Skim Milk Powder",
                                    "Milk", "Skimmed Milk", "Milk Fat",)
                                checkStringsInParagraph(ingredient, veganrestrict)
                                val note = context?.let { it1 -> readString(it1, "vegan") }
                                dietaryNote = dietaryNote.plus(note)
                            }
                            if (dietaryNote == " DIETARY RESTRICTIONS: "){
                                dietaryNote = ""
                            }
                            Note = Note.plus(dietaryNote)

                        }else{
                            //Ignore
                        }

                        //Check Medical Diagnosis

                        database = FirebaseDatabase.getInstance().getReference("Dietaries")
                        database.child(userName).get().addOnSuccessListener {
                            if(it.exists()){
                                val ingredient = ingredients.replace(Regex("[,]"), " ")
                                val diabetic = it.child("diagnosis1").value.toString()
                                val lactose = it.child("diagnosis2").value.toString()
                                val gerd = it.child("diagnosis3").value.toString()
                                val hyperuricemia = it.child("diagnosis4").value.toString()
                                val others = it.child("diagnosis5").value.toString()


                                var diagnosisNote = " MEDICAL DIAGNOSIS: "

                                if (diabetic != "null"){
                                    //Insert here all the ingredients that could trigger the condition
                                    val diabeticrestrict = listOf("diabetic", "Sugar", "Sweeteners", "Sweetener: Sucralose", "Sweetener: Acesulfame potassium",
                                        "Cane Sugar", "Refined Sugar", "Palm Sugar", "Maltodextrine", "Cornstarch")
                                    checkStringsInParagraph(ingredient, diabeticrestrict)
                                    val note = context?.let { it1 -> readString(it1, "diabetic") }
                                    diagnosisNote = diagnosisNote.plus(note)
                                }
                                if (lactose != "null"){
                                    //Insert here all the ingredients that could trigger the condition
                                    val lactoserestrict = listOf("lactoseIntolerant", "Natural Cheddar Cheese", "Skimmed Milk", "Buttermilk", "Natural Cheese", "Milk Solids", "Milk Solids: Buttermilk Powder",
                                        "Natural Cheese", "Milk Proteins", "Cheese", "Buttermilk Powder", "Milk Powder","Whey Powder" ,"Sodium Caseinate", "Caseinate", "Skim Milk Powder",
                                        "Milk", "Skimmed Milk", "Milk Fat",)
                                    checkStringsInParagraph(ingredient, lactoserestrict)
                                    val note = context?.let { it1 -> readString(it1, "lactoseIntolerant") }
                                    diagnosisNote = diagnosisNote.plus(note)
                                }
                                if (gerd != "null"){
                                    //Insert here all the ingredients that could trigger the condition
                                    val gerdrestrict = listOf("GRD", "Cheddar Cheese", "Butter Milk", "Milk Solids", "Vinegar", "Citric Acid",
                                        "Cheese", "Milk Proteins", "Milk Fat", "Buttermilk Powder", "Carbonated Water", "Acidulant", "Caffeine"
                                        , "Coffee", "Instant Coffee", "Cocoa Powder", "Corn Oil", "Palm Oil", "Chocolate", "Peanut", "Stabilizer"
                                        , "Peanut Oil", "Vegetable Oil")
                                    checkStringsInParagraph(ingredient, gerdrestrict)
                                    val note = context?.let { it1 -> readString(it1, "GRD") }
                                    diagnosisNote = diagnosisNote.plus(note)
                                }
                                if (hyperuricemia != "null"){
                                    //Insert here all the ingredients that could trigger the condition
                                    val hyperuricemiarestrict = listOf("Hyperuricemia", "Palm Oil", "Cheddar Cheese", "Natural Cheese", "Milk Solids", "Milk Proteins",
                                        "Cheese", "Milk Proteins", "Milk Powder", "Buttermilk Powder", "Sweeteners", "Acidulant", "Caffeine"
                                        , "Coffee", "Instant Coffee", "Natural Flavors", "Sugar", "Salt")
                                    checkStringsInParagraph(ingredient, hyperuricemiarestrict)
                                    val note = context?.let { it1 -> readString(it1, "Hyperuricemia") }
                                    diagnosisNote = diagnosisNote.plus(note)
                                }
                                if (diagnosisNote == " MEDICAL DIAGNOSIS: "){
                                    diagnosisNote = ""
                                }
                                Note = Note.plus(diagnosisNote)

                            }else{
                                //Ignore
                            }

                            showPopup(productName, ingredients, Note, userName)

                        }.addOnFailureListener{
                            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
                        }

                    }.addOnFailureListener{
                        Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener{
                    Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity , "Product is not registered in the database" , Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
        }
    }

    fun checkStringsInParagraph(paragraph: String, stringsToCheck: List<String>){
        val presentStrings = mutableListOf<String>()

        for (stringToCheck in stringsToCheck) {
            if (paragraph.contains(stringToCheck, ignoreCase = true)) {
                presentStrings.add(stringToCheck)
            }
        }

        if (presentStrings.isNotEmpty()) {
            val toastMessage = presentStrings.joinToString(", ")
            if(stringsToCheck.contains("vegetarian")){
                context?.let { saveString(it, "vegetarian", toastMessage) }
            }
            if(stringsToCheck.contains("vegan")){
                context?.let { saveString(it, "vegan", toastMessage) }
            }
            if(stringsToCheck.contains("diabetic")){
                context?.let { saveString(it, "diabetic", toastMessage) }
            }
            if(stringsToCheck.contains("lactoseIntolerant")){
                context?.let { saveString(it, "lactoseIntolerant", toastMessage) }
            }
            if(stringsToCheck.contains("GRD")){
                context?.let { saveString(it, "GRD", toastMessage) }
            }
            if(stringsToCheck.contains("Hyperuricemia")){
                context?.let { saveString(it, "Hyperuricemia", toastMessage) }
            }

        } else {
            context?.let { saveString(it, "vegetarian", "") }
            context?.let { saveString(it, "vegan", "") }
            context?.let { saveString(it, "diabetic", "") }
            context?.let { saveString(it, "lactoseIntolerant", "") }
            context?.let { saveString(it, "GRD", "") }
            context?.let { saveString(it, "Hyperuricemia", "") }
        }

    }

    fun saveString(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun readString(context: Context, key: String): String? {
        val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }


    private fun showPopup(name: String, ingredients: String, note: String, userName: String){
        val inflater = requireActivity().layoutInflater
        val popupView = inflater.inflate(R.layout.popup_scan, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(popupView)
        val alertDialog = alertDialogBuilder.create()
        val drw_backScan = popupView.findViewById<ImageView>(R.id.drw_backScan)

        tvProductName = popupView.findViewById(R.id.tvProductName)
        tvIngredients = popupView.findViewById(R.id.tvIngredients)
        tvAllergens = popupView.findViewById(R.id.tvAllergens)
        tvNoteLabel = popupView.findViewById(R.id.tvNoteLabel)


        database = FirebaseDatabase.getInstance().getReference("AlarmsNotification")
        database.child(userName).get().addOnSuccessListener {
            if(it.exists()) {

                val allergen = it.child("allergies").value.toString()
                val dietary = it.child("dietary").value.toString()
                val medical = it.child("medical").value.toString()
                val warning = "Health Preference Warning!"
                val notification = "This product contains ingredients contrary to your HEALTH PREFERENCES.$note"

                if (note.contains("ALLERGEN:")){
                    showNotification(warning,"Ingredient/s found that may trigger your allergies" ,notification)
                    if(allergen == "Alarms and Notification"){
                        triggerAlarm()
                    }
                }else if (note.contains("DIETARY")){
                    if (dietary == "Notifications Only"){
                        showNotification(warning, "Ingredient/s found against your Dietary Restriction", notification)
                    }else if (dietary == "Alarms And Notification"){
                        showNotification(warning,"Ingredient/s found against your Dietary Restriction" ,notification)
                        triggerAlarm()
                    }

                }else if (note.contains("MEDICAL")){
                    if (medical == "Notifications Only"){
                        showNotification(warning,"Ingredient/s found against your Medical Diagnosis" ,notification)
                    }else if (medical == "Alarms And Notification"){
                        showNotification(warning,"Ingredient/s found against your Medical Diagnosis", notification)
                        triggerAlarm()
                    }
                }
            }else{
                //Ignore
            }
        }.addOnFailureListener{
            Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
        }

        alertDialog.show()

        tvProductName.text = name
        tvIngredients.text = ingredients

        if (note != ""){
            tvNoteLabel.text = "This product contains ingredients contrary to your HEALTH PREFERENCES.$note"
        }else{
            tvNoteLabel.text = "This product is SAFE for your consumption"
        }



        drw_backScan.setOnClickListener {
            alertDialog.dismiss()
        }
        val btnScan = view?.findViewById<Button>(R.id.btnScannerScan)
        // Set a dismiss listener to handle clean-up when the AlertDialog is dismissed
        alertDialog.setOnDismissListener {codeScanner.startPreview()
        btnScan?.isVisible = false
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
            val pattern = longArrayOf(0, 1000, 200, 1000, 200, 1000) // Vibration pattern (wait, vibrate, wait, vibrate)
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

        return view
    }


    //Scanner Function

}