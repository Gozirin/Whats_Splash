package com.example.whatsappapp.selectcontact.contacts

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappapp.R
import com.example.whatsappapp.adapters.SearchAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Contacts : Fragment() {
    // VARIABLES
    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactLayoutManager: RecyclerView.LayoutManager
    private lateinit var contactsAdapter: ContactsAdapter
    private val contactInfo = arrayListOf<User>()
    private lateinit var fstore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        userid = auth.currentUser!!.uid
        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerview)
        contactLayoutManager = LinearLayoutManager(context as Activity)
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        fstore.collection("users")
            .document(userid).collection("friends").get().addOnSuccessListener {
            if (!it.isEmpty) {
                contactInfo.clear()
                val list = it.documents
                for (doc in list) {
                    val friendsID = doc.id
                    val chatRoomID = doc.getString("chatroomid")
                    fstore.collection("users").document(friendsID).addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.d("", "")
                        } else {
//                            val obj = User(
//                                friendsID,
//                                value!!.getString("userName").toString(),
//                                value.getString("userEmail").toString(),
//                                value.getString("userStatus").toString(),
//                                value.getString("userProfilePhoto").toString(),
//                                chatRoomID.toString()
//                            )
//
//
//                        contactInfo.add(obj)
//                        contactsAdapter = ContactsAdapter(context as Activity, contactInfo)
//                        contactsRecyclerView.adapter = contactsAdapter
//                        contactsRecyclerView.layoutManager = contactLayoutManager
//                        contactsRecyclerView.addItemDecoration(
//                            DividerItemDecoration(
//                                contactsRecyclerView.context,
//                                (contactLayoutManager as LinearLayoutManager).orientation
//                            )
//                        )

                        }
                    }
                }
            }
        }
        return view
    }
}
