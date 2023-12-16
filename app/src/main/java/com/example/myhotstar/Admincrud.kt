package com.example.myhotstar

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

class Admincrud : AppCompatActivity() {

    private  lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    lateinit var adapter: FragmentPageAdapter
    private lateinit var sharedPref: SharedPreferences
    private lateinit var btn_logout: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admincrud)

        sharedPref = getSharedPreferences("Login", MODE_PRIVATE)

        btn_logout = findViewById(R.id.btn_logout)

        btn_logout.setOnClickListener {
            // Set nilai isLoggedIn menjadi false
            sharedPref.edit().putBoolean("isLogin", false).apply()

            // Redirect ke layar login
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Membersihkan stack activity
            startActivity(intent)
            finish()
        }

        tabLayout = findViewById(R.id.tablayout)
        viewPager2 = findViewById(R.id.viewpager2)

        adapter = FragmentPageAdapter(supportFragmentManager, lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("CRUD"))
        tabLayout.addTab(tabLayout.newTab().setText("VIEW"))

        viewPager2.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

    }



}



