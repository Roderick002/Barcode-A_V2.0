package com.example.barcode_a

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ProductDetails : Fragment() {
    private var productName: String? = null
    private var ingredients: String? = null
    private var allergens: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productName = it.getString(ARG_PRODUCT_NAME)
            ingredients = it.getString(ARG_PRODUCT_INGREDIENTS)
            allergens = it.getString(ARG_PRODUCT_ALLERGENS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)
        val productNameTxtView = view.findViewById<TextView>(R.id.productName)
        val productIngredientsTxtView = view.findViewById<TextView>(R.id.productIngredients)
        val productAllergensTxtView = view.findViewById<TextView>(R.id.productAllergens)

        productNameTxtView.text = productName
        productIngredientsTxtView.text = ingredients
        productAllergensTxtView.text = allergens

        return view
    }

    companion object {
        private const val ARG_PRODUCT_NAME = "product_name"
        private const val ARG_PRODUCT_INGREDIENTS = "product_ingredients"
        private const val ARG_PRODUCT_ALLERGENS = "product_allergens"

        fun newInstance(
            productName: String,
            productIngredients: String,
            productAllergens: String
        ): ProductDetails {
            val fragment = ProductDetails()
            val args = Bundle()
            args.putString(ARG_PRODUCT_NAME, productName)
            args.putString(ARG_PRODUCT_INGREDIENTS, productIngredients)
            args.putString(ARG_PRODUCT_ALLERGENS, productAllergens)
            fragment.arguments = args
            return fragment
        }
    }
}