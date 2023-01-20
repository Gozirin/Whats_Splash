package com.example.whatsappapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.whatsappapp.whatsapp.Calls
import com.example.whatsappapp.whatsapp.Status
import com.example.whatsappapp.whatsapp.chats.Chats
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var appPageAdapter: AppPagerAdapter
    private val titles = arrayListOf("Chats", "Status", "Calls")
    private lateinit var auth: FirebaseAuth
    private lateinit var showContacts: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbarMain)
        tabLayout = findViewById(R.id.tabLayoutMain)
        viewPager2 = findViewById(R.id.viewPager2Main)
        showContacts = findViewById(R.id.btContacts)
        auth = FirebaseAuth.getInstance()
        toolbar.title = "WhatsappApp"
        setSupportActionBar(toolbar)
        appPageAdapter = AppPagerAdapter(this)
        viewPager2.adapter = appPageAdapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = titles[position]
        }
            .attach()

        // FLOATING ACTION BUTTON
        showContacts.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            intent.putExtra("OptionName", "friends")
            startActivity(intent)
        }
    }
    class AppPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 3
        }

        // 0,2,3
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Chats()
                1 -> Status()
                2 -> Calls()
                else -> Chats()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile ->
                {
                    val intent = Intent(this, MenuActivity::class.java)
                    intent.putExtra("OptionName", "Profile")
                    startActivity(intent)
                }
            R.id.about_us ->
                {
                    val intent = Intent(this, MenuActivity::class.java)
                    intent.putExtra("OptionName", "About")
                    startActivity(intent)
                }
            R.id.logout ->
                {
                    auth.signOut()
                    val intent = Intent(this, AuthenticationActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            R.id.search_Contacts ->
                {
                    val intent = Intent(this, MenuActivity::class.java)
                    intent.putExtra("OptionName", "Profile")
                    startActivity(intent)
                }
        }
        return super.onOptionsItemSelected(item)
    }
}
