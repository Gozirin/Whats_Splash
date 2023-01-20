package com.example.whatsappapp.whatsapp.chats

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Chats : Fragment() {
    private lateinit var chatsRecyclerview: RecyclerView
    private lateinit var chatsLayoutManager: RecyclerView.LayoutManager
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var fstore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var chatsInfo = arrayListOf<ChatModal>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        chatsRecyclerview = view.findViewById(R.id.chatContentRecyclerview)
        chatsLayoutManager = LinearLayoutManager(context as Activity)
        auth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()
        fstore.collection("chats")
            .whereArrayContains("uids", auth.currentUser!!.uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.d("", "")
                } else {
                    if (!snapshot?.isEmpty!!) {
                        chatsInfo.clear()
                        val list = snapshot.documents
                        for (doc in list) {
                            fstore.collection("chats")
                                .document(doc.id).collection("message")
                                .orderBy("id", Query.Direction.DESCENDING).addSnapshotListener {
                                        messagesnapshot, exception ->
                                    if (snapshot != null) {
                                        Log.d("error", "Some Error Occured")
                                    } else {
                                        val id = messagesnapshot!!.documents[0]
                                        val message = id.get("message").toString()
                                        val receiver = id.get("receiver").toString()
                                        val obj = ChatModal(receiver, message, id.getString("").toString())
                                        chatsInfo.add(obj)
                                    }
                                }
                        }
//                        val list = snapshot.documents
//                        for (doc in list) {
//                            val obj = User(
//                                doc.id,
//                                doc.getString("userName").toString(),
//                                doc.getString("userEmail").toString(),
//                                doc.getString("userStatus").toString(),
//                                doc.getString("userProfilePhoto").toString(),
//                                doc.getString("userProfilePhoto").toString(),
//                            )
//                            chatsInfo.add(obj)
                        chatsAdapter = ChatsAdapter(context as Activity, chatsInfo)
                        chatsRecyclerview.adapter = chatsAdapter
                        chatsRecyclerview.layoutManager = chatsLayoutManager
                        chatsRecyclerview.addItemDecoration(
                            DividerItemDecoration(
                                chatsRecyclerview.context,
                                (chatsLayoutManager as LinearLayoutManager).orientation
                            )
                        )
                    }
                }
            }
        return view
    }
}
