package com.example.barcode_a.view


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.barcode_a.ProductDetails
import com.example.barcode_a.R
import com.example.barcode_a.model.UserData

class UserAdapter(val c:Context,val userList:ArrayList<UserData>, val onItemClick: (UserData)->Unit)
    :RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{
    private lateinit var mListener : onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    inner class UserViewHolder(val v:View, listener: onItemClickListener):RecyclerView.ViewHolder(v){
        lateinit var productname:TextView
        lateinit var ingredients:TextView
        lateinit var allergens:TextView

        init {
            productname = v.findViewById<TextView>(R.id.mTitle)
            ingredients = v.findViewById<TextView>(R.id.mSubTitle)
            allergens = v.findViewById<TextView>(R.id.mSubTitle2)
            //mMenus = v.findViewById<ImageView>(R.id.mMenus)
            //mMenus.setOnClickListener { popupMenus(it) }
        }


        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_item,parent,false)
        return UserViewHolder(v,mListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = userList[position]
        holder.productname.text = newList.productName
        holder.ingredients.text = newList.ingredients
        holder.allergens.text = newList.allergens

    }
}