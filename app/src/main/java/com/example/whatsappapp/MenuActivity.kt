package com.example.whatsappapp

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappapp.adapters.SearchAdapter
import com.example.whatsappapp.messages.Messaging
import com.example.whatsappapp.selectcontact.About
import com.example.whatsappapp.selectcontact.Profile
import com.example.whatsappapp.selectcontact.contacts.Contacts
import com.example.whatsappapp.selectcontact.contacts.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MenuActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var toolbarMenu: androidx.appcompat.widget.Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var optionValue: String
    private lateinit var queryTerm: String
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchLayoutManager: RecyclerView.LayoutManager
    private lateinit var searchAdapter: SearchAdapter
    private val searchInfo = arrayListOf<User>()
    private val menu = menuInflater
    private var register: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        toolbarMenu = findViewById(R.id.toolbarMenu)
        frameLayout = findViewById(R.id.frameLayout)
        // frameLayout.visibility = View.GONE

        if (intent != null) {
            optionValue = intent.getStringExtra("OptionName").toString()
            when (optionValue) {
                "profile" -> {
                    frameLayout.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, Profile())
                        .commit()
                    toolbarMenu.title = "Profile"
                }
                "about" -> {
                    frameLayout.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, About())
                        .commit()
                    toolbarMenu.title = "About"
                }
                "search" -> {
                    searchRecyclerView = findViewById(R.id.RecyclerviewSearch)
                    searchLayoutManager = LinearLayoutManager(this)
                    searchRecyclerView.visibility = View.VISIBLE
//                       supportFragmentManager.beginTransaction().replace(R.id.frameLayout, Contacts()).commit()
//                        toolbarMenu.title = "Contacts"
//                        toolbarMenu.visibility = View.GONE
                    toolbarMenu.title = "Search Users"
                    setSupportActionBar(toolbarMenu)
                    searchRecyclerView.addItemDecoration(
                        DividerItemDecoration(
                            searchRecyclerView.context,
                            (searchLayoutManager as LinearLayoutManager).orientation
                        )
                    )

                }
                "friends"-> {
                    frameLayout.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, Contacts())
                        .commit()
                    toolbarMenu.title = "FriendsList"
                }
                "chatMessaging" -> {
                    frameLayout.visibility = View.VISIBLE
                    toolbarMenu.title = intent.getStringExtra("receiverName")
                    val fragmentName = Messaging()
                    val transaction = supportFragmentManager.beginTransaction()
                    val bundle = Bundle()
                    bundle.putString("documentID",intent.getStringExtra("chatroom"))
                    bundle.putString("friendName",intent.getStringExtra("receiverName"))
                    fragmentName.arguments = bundle
                    transaction.replace(R.id.frameLayout,fragmentName).commit()
                }
                "contactMessaging" -> {
                    frameLayout.visibility = View.VISIBLE
                    toolbarMenu.title = intent.getStringExtra("receiverName")
                    val fragmentName = Messaging()
                    val transaction = supportFragmentManager.beginTransaction()
                    val contactBundle = Bundle()
                    contactBundle.putString("chatRoomID",intent.getStringExtra("chatroomID"))
                    contactBundle.putString("friendUID",intent.getStringExtra("friendUID"))
                    fragmentName.arguments  =    contactBundle
                    transaction.replace(R.id.frameLayout,fragmentName).commit()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search,menu)
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            queryTerm = query
            if (queryTerm.isNotEmpty()) {
                searchUsers()
            }
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            queryTerm = newText
            if (queryTerm.isNotEmpty()) {
                searchUsers()
            }
        }
        return true
    }

    // FUNCTION FOR SEARCH USERS
    private fun searchUsers() {
        // Toast.makeText(this,"Clicked on $queryTerm",Toast.LENGTH_LONG).show()
        register = FirebaseFirestore.getInstance()
            .collection("users")
            .orderBy("userName").startAt(queryTerm).addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("onError", "Some error Occurred")
                } else {
                    if (!snapshot?.isEmpty!!) {
                        searchInfo.clear()
                        val searchList = snapshot.documents
                        for (doc in searchList)
                        {

                            if(FirebaseAuth.getInstance().currentUser!!.uid==doc.id)
                            {
                                Log.d("onSuccess","User Running The App")

                            }

                            else {
//                                val obj = User(
//                                    doc.id,
//                                    doc.getString("userName").toString(),
//                                    doc.getString("userEmail").toString(),
//                                    doc.getString("userStatus").toString(),
//                                    doc.getString("userProfilePhoto").toString(),
//                                    doc.getString("userProfilePhoto").toString(),
//                                    "0"
//                                )
//
//                                searchInfo.add(obj)
//                                searchAdapter = SearchAdapter(this, searchInfo)
//                                searchRecyclerView.adapter = searchAdapter
//                                searchRecyclerView.layoutManager = searchLayoutManager






                                //searchRecyclerView.addItemDecoration(
//                                   DividerItemDecoration(
//                                        searchRecyclerView.context,
//                                        (searchLayoutManager as LinearLayoutManager).orientation
//                                   )
//                               )
                            }
                        }
                    }
                }
            }
    }
    override fun onDestroy() {
        register?.remove()
        super.onDestroy()
    }
}


