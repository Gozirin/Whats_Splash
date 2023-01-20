package com.example.whatsappapp.messages

import android.annotation.SuppressLint
import android.app.Activity
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class Messaging : Fragment() {
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton
    private lateinit var fstore: FirebaseFirestore
    private lateinit var fauth: FirebaseAuth
    private lateinit var messageLayoutManager: RecyclerView.LayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var db: DocumentReference
    private lateinit var userid: String
    private lateinit var friendID: String
    private lateinit var chatroomId: String
    private lateinit var chatroomUID: String
    private lateinit var chatID: String
    private lateinit var db1: DocumentReference
    private val messageInfo = arrayListOf<MessageModal>()
    private var register: ListenerRegistration? = null
    private var register1: ListenerRegistration? = null

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messaging, container, false)
        val values = arguments
        if (values != null) {
            friendID = values!!.getString("friendName").toString()
            chatroomId = values.getString("documentID").toString()
            initialization()
            recyclerViewBuild()
            fetchingMessages()
            fetchMessageID()
        }

        val contactBundle = arguments
        if (contactBundle != null) {
            friendID = values!!.getString("friendUID").toString()
            chatroomId = values.getString("chatroomID").toString()
            fetchChatRoomUID()
            initialization()
            recyclerViewBuild()
            fetchingMessages()
            fetchMessageID()
        }

//        db = fstore.collection("chats").document(chatroomId).collection("message").document()
//        db = fstore.collection("chats").document(chatroomId).collection("count")
//            .document("chatId")
//        sendMessageButton.setOnClickListener {
//            register = db.addSnapshotListener { value, error ->
//                if (error != null) {
//                    Log.d("", "")
//                } else {
//                    chatID = value?.getString("chatId").toString()
//                    sendMessage()
//                }
//            }
//        }

//        register1 = fstore.collection("chats").document(chatroomId)
//            .collection("message")
//            .orderBy("id", Query.Direction.ASCENDING)
//            .addSnapshotListener { chatSnapshot, exception ->
//                if (exception != null) {
//                    Log.d("", "")
//                } else {
//                    if (!chatSnapshot?.isEmpty!!) {
//                        val listChat = chatSnapshot.documents
//                        for (chat in listChat) {
//                            val chatObj = MessageModal(
//                                chat.getString("sender").toString(),
//                                chat.getString("message").toString(),
//                                chat.getString("timeStamp").toString()
//                            )
//                            messageInfo.add(chatObj)
//                        }
//                        messageRecyclerView.scrollToPosition(chatSnapshot.size() - 1)
//                        messageAdapter.notifyDataSetChanged()
//                    }
//                }
//            }

        return view
    }
    private fun fetchChatRoomUID() {
        fstore.collection("chats").whereEqualTo("chatroomid", chatroomId).get().addOnSuccessListener { query ->
            if (!query.isEmpty) {
                chatroomUID = query.documents[0].id
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchingMessages() {
        register1 = fstore.collection("chats").document(chatroomId)
            .collection("message")
            .orderBy("id", Query.Direction.ASCENDING)
            .addSnapshotListener { chatSnapshot, exception ->
                if (exception != null) {
                    Log.d("", "")
                } else {
                    if (!chatSnapshot?.isEmpty!!) {
                        val listChat = chatSnapshot.documents
                        for (chat in listChat) {
                            val chatObj = MessageModal(
                                chat.getString("sender").toString(),
                                chat.getString("message").toString(),
                                chat.getString("timeStamp").toString()
                            )
                            messageInfo.add(chatObj)
                        }
                        messageRecyclerView.scrollToPosition(chatSnapshot.size() - 1)
                        messageAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("NewApi")
    private fun fetchMessageID() {
        db = fstore.collection("chats").document(chatroomId).collection("message").document()
        db = fstore.collection("chats").document(chatroomId).collection("count")
            .document("chatId")
        sendMessageButton.setOnClickListener {
            register = db.addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("", "")
                } else {
                    chatID = value?.getString("chatId").toString()
                    sendMessage()
                }
            }
        }
    }

    private fun recyclerViewBuild() {
        fstore = FirebaseFirestore.getInstance()
        fauth = FirebaseAuth.getInstance()
        userid = fauth.currentUser?.uid.toString()
    }

    private fun initialization() {
        messageAdapter = MessageAdapter(context as Activity, messageInfo)
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = messageLayoutManager
        db = fstore.collection("chats").document(chatroomId).collection("message").document()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendMessage() {
        register!!.remove()
        val message = sendMessageEditText.text.toString()
        if (TextUtils.isEmpty(message)) {
            sendMessageEditText.error = "Enter some Message to Send"
        } else {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timeStamp = " $hour:$minute"
            val messageObject = mutableMapOf<String, Any>().also {
                it["message"] = message
                it["messageId"] = chatID
                it["userID"] = userid
                it ["messageSender"] = userid
                it ["messageReceiver"] = friendID
                it ["messageTime"] = timeStamp
            }
            db.set(messageObject).addOnSuccessListener {
                Log.d("onSuccess", "Successfully Send Message")
            }
        }
    }

    override fun onDestroy() {
        register1!!.remove()
        super.onDestroy()
    }
}

//
//
//
//
//        // FIREBASE $ VIEWS BY ID
//        fstore = FirebaseFirestore.getInstance()
//        fauth = FirebaseAuth.getInstance()
//        userid = fauth.currentUser?.uid.toString()
//        fstore.collection("chats").whereArrayContains("uids", userid)
//            .addSnapshotListener { snapshot, exception ->
//                if (exception != null) {
//                    Log.d("onError", "Error in fetching Data")
//                } else {
//                    val list = snapshot?.documents
//                    if (list != null) {
//                        for (doc in list) {
//                            db = fstore
//                                .collection("chats")
//                                .document(doc.id)
//                                .collection("message")
//                                .document()
//                            fstore
//                                .collection("chats")
//                                .document(doc.id).collection("message")
//                                .orderBy("id", Query.Direction.ASCENDING)
//                                .addSnapshotListener { snapshot, exception ->
//                                    if (exception != null) {
//                                        messageInfo.clear()
//                                        val list = snapshot?.documents
//                                        if (list != null) {
//                                            for (documents in list) {
//                                                val obj = MessageModal(
//                                                    doc.getString("messageSender").toString(),
//                                                    doc.getString("message").toString(),
//                                                    doc.getString("messageTime").toString()
//                                                )
//                                                messageInfo.add(obj)
//                                                messageAdapter = MessageAdapter(context as Activity, messageInfo)
//                                                messageRecyclerView.adapter = messageAdapter
//                                                messageRecyclerView.layoutManager = messageLayoutManager
//                                                messageRecyclerView.scrollToPosition(list.size - 1)
//                                                messageRecyclerView.adapter!!.notifyDataSetChanged()
//                                            }
//                                        }
//                                    }
//                                }
//                        }
//                    }
//                }
//            }
//        // VIEWS BY ID
//        messageRecyclerView = view.findViewById(R.id.messageRecyclerview)
//        sendMessageEditText = view.findViewById(R.id.etSendMessage)
//        sendMessageButton = view.findViewById(R.id.btSendMessage)
//        messageLayoutManager = LinearLayoutManager(context)
//
//        //  SEND-MESSAGE-BUTTON
//        sendMessageButton.setOnClickListener {
//            sendMessage()
//        }
//        return view
//    }
//
//
//
//    // FUNCTION FOR SENDMAIL
//    @RequiresApi(Build.VERSION_CODES.N)
//    private fun sendMessage() {
//        val message = sendMessageEditText.text.toString()
//        if (TextUtils.isEmpty(message)) {
//            sendMessageEditText.error = "Enter some Message to Send"
//        } else {
//            val c = Calendar.getInstance()
//            val hour = c.get(Calendar.HOUR_OF_DAY)
//            val minute = c.get(Calendar.MINUTE)
//            val timeStamp = "$hour:$minute"
//            val messageObject = mutableMapOf<String, Any>().also {
//                it["message"] = message
//                it["messageId"] = 1
//                it["messageSender"] = userid
//                // it["messageReceiver"] = "sfwYK4LnQwSYY06laJSvteirKsc2"
//                it["messageTime"] = timeStamp
//            }
//            db.set(messageObject).addOnSuccessListener {
//                Log.d("onSuccess", "Successfully Send Message")
//            }
//        }
//    }
// }
