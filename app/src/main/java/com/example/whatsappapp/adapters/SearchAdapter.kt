package com.example.whatsappapp.adapters

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter(val context: Context, private val searchList: ArrayList<com.example.whatsappapp.selectcontact.contacts.User>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val email: TextView = view.findViewById(R.id.txtEmail)
        val status: TextView = view.findViewById(R.id.txtStatus)
        val image: ImageView = view.findViewById(R.id.contactPicture)
        val addFriend: Button = view.findViewById(R.id.btAddFriend)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val contactView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_contact, parent, false)
        return SearchAdapter.SearchViewHolder(contactView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val list = searchList[position]
        holder.name.text = list.profileName
        holder.email.text = list.profileEmail
        holder.status.text = list.profileStatus
        holder.addFriend.visibility = View.VISIBLE
        Picasso.get().load(list.profilePicture).error(R.drawable.passport).into(holder.image)
        holder.addFriend.setOnClickListener {
            // VARIABLES FOR CALENDER
            val c = Calendar.getInstance(Locale.getDefault())
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timeStamp = "$hour:$minute"
            val obj = mutableMapOf<String, String>().also {
                it ["time"] = timeStamp
            }
            val uid1 = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val uid2 = list.profileUid

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid1)
                .collection("friends").document(uid2).set(obj)
                .addOnSuccessListener {
                    Log.d("onSuccess", "Successfully Added With ${list.profileUid}")
                }
            val obj1 = mutableMapOf<String, ArrayList<String>>().also {
                it["uids"] = arrayListOf(uid1, uid2)
            }
            FirebaseFirestore.getInstance().collection("chats").document()
                .set(obj1)
                .addOnSuccessListener {
                    Log.d("onSuccess", "Successfully Chat Created With ${list.profileUid}")
                }
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }
}
