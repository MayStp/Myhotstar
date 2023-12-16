package com.example.myhotstar

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myhotstar.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        sharedPref = getSharedPreferences("Login", MODE_PRIVATE)

        // Cek apakah pengguna sudah login sebelumnya
        if (sharedPref.getBoolean("isLogin", false)) {
            redirectToAppropriateScreen()
            finish()
        }

        binding.gtSignup.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.btnSingin.setOnClickListener {
            val username = binding.inputUname.text.toString()
            val password = binding.inputPass.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                    // Melakukan autentikasi pengguna
                    firebaseAuth.signInWithEmailAndPassword("$username@yourdomain.com", password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                if (user != null) {
                                    if (user.email == "admin@yourdomain.com") { // Ganti dengan username admin
                                        val intent = Intent(this, Admincrud::class.java)
                                        startActivity(intent)
                                    } else {
                                        val intent = Intent(this, Hero::class.java)
                                        startActivity(intent)
                                    }
                                    // Simpan data login ke shared preferences
                                    sharedPref.edit()
                                        .putBoolean("isLogin", true)
                                        .putString("username", user.email)
                                        .apply()
                                    redirectToAppropriateScreen()
                                    finish()
                                }
                            } else {
                                Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                Log.e("TAG", "Login failed", task.exception)
                            }
                        }

            } else {
                Toast.makeText(this, "Please fill in both username and password", Toast.LENGTH_SHORT).show()
            }

        }




    }

    private fun redirectToAppropriateScreen() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            if (user.email == "admin@yourdomain.com") { // Ganti dengan username admin
                val intent = Intent(this, Admincrud::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, Hero::class.java)
                startActivity(intent)
            }
        }
    }
}