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

    inner class UserViewHolder(val v:View):RecyclerView.ViewHolder(v){
        lateinit var productname:TextView
        lateinit var ingredients:TextView
        lateinit var allergens:TextView
        lateinit var mMenus:ImageView

        init {
            productname = v.findViewById<TextView>(R.id.mTitle)
            ingredients = v.findViewById<TextView>(R.id.mSubTitle2)
            allergens = v.findViewById<TextView>(R.id.mSubTitle)
            mMenus = v.findViewById<ImageView>(R.id.mMenus)
            mMenus.setOnClickListener { popupMenus(it) }
        }

        private fun popupMenus(v: View) {
            val position = userList[adapterPosition]
            val popupMenus = PopupMenu(c,v)
            popupMenus.inflate(R.menu.show_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.editText->{
                        val v = LayoutInflater.from(c).inflate(R.layout.add_item, null)
                        val Name = v.findViewById<TextView>(R.id.productName)
                        val productIngredients = v.findViewById<TextView>(R.id.productIngredients)
                        val productAllergens = v.findViewById<TextView>(R.id.productAllergens)
                        AlertDialog.Builder(c)
                            .setView(v)
                            .setPositiveButton("Ok"){
                                    dialog,_->
                                position.productName = Name.text.toString()
                                position.ingredients = productIngredients.text.toString()
                                position.allergens = productAllergens.text.toString()
                                notifyDataSetChanged()
                                Toast.makeText(c,"product Information is Edited", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()

                            }
                            .setNegativeButton("Cancel"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    R.id.delete->{
                        /**set delete*/
                        AlertDialog.Builder(c)
                            .setTitle("Delete")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("Deletion of information confirmation")
                            .setPositiveButton("Yes"){
                                    dialog,_->
                                userList.removeAt(adapterPosition)
                                notifyDataSetChanged()
                                Toast.makeText(c,"Deleted", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                        true
                    }
                    else -> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)

        }
        init{
            v.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    val user = userList[position]
                    onItemClick(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_item,parent,false)
        return UserViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val newList = userList[position]
        holder.productname.text = newList.productName
        holder.allergens.text = newList.allergens
        holder.ingredients.text = newList.ingredients
    }
}