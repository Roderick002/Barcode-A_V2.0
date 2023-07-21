package com.example.barcode_a

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barcode_a.model.UserData
import com.example.barcode_a.view.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProductInformation : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    private lateinit var addBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var userList:ArrayList<UserData>
    private lateinit var userAdapter:UserAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_product_information, container, false)

        /**set List*/
        userList = ArrayList()
        /**set find Id*/
        addBtn = view.findViewById(R.id.btn_add)
        recv = view.findViewById(R.id.mReclycler)
        /**set adapter*/
        userAdapter = UserAdapter(requireContext(),userList){
                user ->
            val fragment =
                user.productName?.let { user.ingredients?.let { it1 ->
                    user.allergens?.let { it2 ->
                        ProductDetails.newInstance(it,
                            it1, it2
                        )
                    }
                } }
            val transaction = parentFragmentManager.beginTransaction()
            fragment?.let { transaction.replace(R.id.frame_layout, it) }
            transaction.addToBackStack(null)
            transaction.commit()
        }
        /**setRecycler view Adapter*/
        recv.layoutManager = LinearLayoutManager(requireContext())
        recv.adapter = userAdapter
        /**set Dialog*/
        addBtn.setOnClickListener {
            addInfo()
        }
        return view
    }

    private fun addInfo() {
        val inflter = LayoutInflater.from(requireContext())
        val v = inflter.inflate(R.layout.add_item, null)
        val addDialog = AlertDialog.Builder(requireContext())
        /**set view*/
        val productName = v.findViewById<EditText>(R.id.productName)
        val productIngre = v.findViewById<EditText>(R.id.productIngredients)
        val productAller = v.findViewById<EditText>(R.id.productAllergens)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok",null)

        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }

        val dialog = addDialog.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val names = productName.text.toString()
            val ingredients = productIngre.text.toString()
            val allergens = productAller.text.toString()

            if (names.isNotBlank() && ingredients.isNotBlank() && allergens.isNotBlank()){
                if (isValidFormat(ingredients)){
                    if(isValidFormat((allergens))){

                        database = FirebaseDatabase.getInstance().getReference("ProductInformation")
                        database.child(names).get().addOnSuccessListener {

                            if(it.exists()){
                                Toast.makeText(activity , "Product Already Exists!" , Toast.LENGTH_SHORT).show()
                            }else{
                                /**Add Product Info to Database*/
                                val productinfo = UserData(names, ingredients, allergens)
                                database = FirebaseDatabase.getInstance().getReference("ProductInformation")
                                database.child(names).setValue(productinfo).addOnSuccessListener {
                                    //success
                                }.addOnFailureListener(){
                                    Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
                                }

                                /**Bind Products to Manufacturers*/
                                firebaseAuth = FirebaseAuth.getInstance()
                                val manuProducts = UserData("Name: $names", "Ingredients: $ingredients", "Allergens: $allergens")
                                //Get username
                                val email = firebaseAuth.currentUser?.email.toString()
                                val userName = email.replace(Regex("[@.]"), "")
                                val manufacturer = "Manufacturer$userName"

                                database = FirebaseDatabase.getInstance().getReference(manufacturer)
                                database.child(names).setValue(manuProducts).addOnSuccessListener {
                                    //success
                                }.addOnFailureListener(){
                                    Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
                                }

                                userList.clear() /**Reset List to prevent items' duplicate*/
                                Toast.makeText(requireContext(),"Product Added", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recv = view.findViewById(R.id.mReclycler)
        recv.layoutManager = LinearLayoutManager(activity)
        recv.setHasFixedSize(true)

        getUserData()
        userList = arrayListOf<UserData>()


        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = Home_Manufacturer()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout2, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getUserData() {
        firebaseAuth = FirebaseAuth.getInstance()
        val email = firebaseAuth.currentUser?.email.toString()
        val userName = email.replace(Regex("[@.]"), "")
        val manufacturer = "Manufacturer$userName"

        database = FirebaseDatabase.getInstance().getReference(manufacturer)
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for (productSnapshot in snapshot.children){
                        val product = productSnapshot.getValue(UserData::class.java)
                        userList.add(product!!)
                    }
                    var adapter = UserAdapter(requireContext(),userList) { user ->
                        val fragment =
                            user.productName?.let {
                                user.ingredients?.let { it1 ->
                                    user.allergens?.let { it2 ->
                                        ProductDetails.newInstance(
                                            it,
                                            it1, it2
                                        )
                                    }
                                }
                            }

                        val transaction = parentFragmentManager.beginTransaction()
                        fragment?.let { transaction.replace(R.id.frame_layout, it) }
                        transaction.addToBackStack(null)
                        transaction.commit()
                        userAdapter.notifyItemInserted(userList.size - 1)
                    }
                    recv.adapter = adapter
                    /**Recycler View On Item Click Listener*/
                    adapter.setOnItemClickListener(object : UserAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            /**Setup product name for data retrieval*/
                            val item = userList.get(position).toString()
                            fun getStringsBetweenWords(source: String, startWord: String, endWord: String): List<String> {
                                val pattern = "\\b$startWord\\b(.*?)\\b$endWord\\b".toRegex(RegexOption.IGNORE_CASE)
                                val matches = pattern.findAll(source)
                                return matches.map { it.groupValues[1] }.toList()
                            }
                            val source = item.replace(Regex("[=(),:]"), "")
                            val extractedStrings = getStringsBetweenWords(source, "UserDataproductNameName", "ingredientsIngredients")
                                val product = extractedStrings.toString().replace("[\\[\\]]".toRegex(), "").trim()

                                val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.details_product, null)
                                val edit = dialogView.findViewById<TextView>(R.id.tv_edit)
                                val save = dialogView.findViewById<TextView>(R.id.tv_save)
                                val delete = dialogView.findViewById<TextView>(R.id.tv_delete)
                                val productName = dialogView.findViewById<TextView>(R.id.tv_productName)
                                val productIngredients = dialogView.findViewById<EditText>(R.id.et_productIngredients)
                                val productAllergens = dialogView.findViewById<EditText>(R.id.et_productAllergens)
                                val tvBack = dialogView.findViewById<TextView>(R.id.tv_back)


                                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                                alertDialogBuilder.setView(dialogView)
                                val alertDialog = alertDialogBuilder.create()
                                alertDialog.show()

                                database = FirebaseDatabase.getInstance().getReference("ProductInformation")
                                database.child(product).get().addOnSuccessListener {
                                    if(it.exists()){

                                        val name = it.child("productName").value.toString()
                                        val allergen = it.child("allergens").value.toString()
                                        val ingredient = it.child("ingredients").value.toString()

                                        /**productName.text.append(name)*/
                                        productName.text = name
                                        productAllergens.text.append(allergen)
                                        productIngredients.text.append(ingredient)
                                    }else{
                                        Toast.makeText(activity , "Product Does Not Exist! $product" , Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener{
                                    Toast.makeText(activity , "Failed" , Toast.LENGTH_SHORT).show()
                                }

                                //Back function
                                tvBack.setOnClickListener {
                                    alertDialog.dismiss()
                                }

                                //Edit button click listener
                                edit.setOnClickListener {
                                    productIngredients.isEnabled = true
                                    productAllergens.isEnabled = true
                                    save.isEnabled = true
                                    save.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                                    Toast.makeText(requireContext(), "The fields are now enabled",
                                        Toast.LENGTH_SHORT).show()
                                }

                                //Save button click listener
                                /**Update Product*/
                                save.setOnClickListener {
                                    val updatedName = productName.text.toString()
                                    val updatedIngredients = productIngredients.text.toString()
                                    val updatedAllergens = productAllergens.text.toString()

                                    if (updatedName.isNotBlank() && updatedIngredients.isNotBlank() && updatedAllergens.isNotBlank()){
                                        if (isValidFormat(updatedIngredients)){
                                            if (isValidFormat(updatedAllergens)){
                                                database = FirebaseDatabase.getInstance().getReference("ProductInformation")
                                                database.child(product).get().addOnSuccessListener {
                                                    if(it.exists()){

                                                        val allergen = it.child("allergens").value.toString()
                                                        val ingredient = it.child("ingredients").value.toString()

                                                        if (updatedAllergens == allergen && updatedIngredients == ingredient){
                                                            Toast.makeText(activity , "Product Information Isn't Updated" , Toast.LENGTH_SHORT).show()
                                                        }else{
                                                            /**Update Product Info to Database*/
                                                            val productinfo = UserData(updatedName, updatedIngredients, updatedAllergens)
                                                            database = FirebaseDatabase.getInstance().getReference("ProductInformation")
                                                            database.child(product).setValue(productinfo).addOnSuccessListener {
                                                                //success
                                                            }.addOnFailureListener(){
                                                                Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
                                                            }

                                                            /**Update Products to Manufacturers*/
                                                            firebaseAuth = FirebaseAuth.getInstance()
                                                            val manuProducts = UserData("Name: $updatedName", "Ingredients: $updatedIngredients", "Allergens: $updatedAllergens")
                                                            //Get username
                                                            val email = firebaseAuth.currentUser?.email.toString()
                                                            val userName = email.replace(Regex("[@.]"), "")
                                                            val manufacturer = "Manufacturer$userName"

                                                            database = FirebaseDatabase.getInstance().getReference(manufacturer)
                                                            database.child(product).setValue(manuProducts).addOnSuccessListener {
                                                                //success
                                                            }.addOnFailureListener(){
                                                                Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
                                                            }
                                                            userList.clear() /**Reset List to prevent items' duplicate*/
                                                            Toast.makeText(activity , "$product product details updated!" , Toast.LENGTH_SHORT).show()
                                                        }
                                                    }else{
                                                        Toast.makeText(activity , "Product Does Not Exist! $product" , Toast.LENGTH_SHORT).show()
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

                                    // Disable the edit mode and update the UI accordingly
                                    productIngredients.isEnabled = false
                                    productAllergens.isEnabled = false
                                    save.isEnabled = false
                                    alertDialog.dismiss()
                                    }

                                //Delete button click listener
                                /**Delete Product Data From Database*/
                                delete.setOnClickListener {
                                    showDeleteConfirmation(product)
                                    alertDialog.dismiss()
                                }

                            }

                    })
                }
            }
            //Function to show the delete confirmation pop-up
            private fun showDeleteConfirmation(product: String){
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Delete Product")
                alertDialogBuilder.setMessage("Are you sure you want to delete $product?")
                alertDialogBuilder.setPositiveButton("Ok"){
                    dialog, which ->
                    deleteProduct(product)
                }
                alertDialogBuilder.setNegativeButton("Cancel"){
                    dialog, which ->
                    dialog.dismiss()
                }
                alertDialogBuilder.show()
            }

            private fun deleteProduct(product: String){
                database = FirebaseDatabase.getInstance().getReference("ProductInformation")
                database.child(product).removeValue().addOnSuccessListener {

                }.addOnFailureListener {
                    Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
                }
                firebaseAuth = FirebaseAuth.getInstance()
                //Get username
                val email = firebaseAuth.currentUser?.email.toString()
                val userName = email.replace(Regex("[@.]"), "")
                val manufacturer = "Manufacturer$userName"
                database = FirebaseDatabase.getInstance().getReference(manufacturer)
                database.child(product).removeValue().addOnSuccessListener {
                    //success
                }.addOnFailureListener(){
                    Toast.makeText(activity , "Database ERROR!" , Toast.LENGTH_SHORT).show()
                }
                userList.clear() /**Reset List to prevent items' duplicate*/
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }



}