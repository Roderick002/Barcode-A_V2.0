package com.example.barcode_a

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barcode_a.model.UserData
import com.example.barcode_a.view.UserAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ProductInformation : Fragment() {

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
            val fragment = ProductDetails.newInstance(user.productName, user.ingredients, user.allergens)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_layout, fragment)
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
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            val names = productName.text.toString()
            val ingredients = productIngre.text.toString()
            val allergens = productAller.text.toString()

            if (names.isNotBlank() && ingredients.isNotBlank() && allergens.isNotBlank()){
                if (isValidFormat(ingredients)){
                    userList.add(UserData("Name: $names", "Ingredients : $ingredients","Allergens: $allergens"))
                    userAdapter.notifyItemInserted(userList.size - 1)
                    Toast.makeText(requireContext(),"Adding User Information Success", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }else{
                    Toast.makeText(requireContext(),"Please enter ingredients in the format: Ingredient, Ingredient, Ingredient", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),"Product Information is not added", Toast.LENGTH_SHORT).show()
            }
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(requireContext(),"Cancel", Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }

    private fun isValidFormat(ingredients: String):Boolean{
        val regex = Regex("^[a-zA-Z]+(,\\s[a-zA-Z]+)*$")
        return regex.matches(ingredients)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Back Button Function
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val fragment = Home_Manufacturer()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }


}