package com.example.myhotstar

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myhotstar.databinding.ActivityAccountactBinding

class Accountact : AppCompatActivity() {
    private lateinit var binding: ActivityAccountactBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var btn_logout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAccountactBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accountact)

        sharedPref = getSharedPreferences("Login", MODE_PRIVATE)

        btn_logout = findViewById(R.id.btnout)

        btn_logout.setOnClickListener {
            // Set nilai isLoggedIn menjadi false
            sharedPref.edit().putBoolean("isLogin", false).apply()

            // Redirect ke layar login
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Membersihkan stack activity
            startActivity(intent)
            finish()
        }
    }
}