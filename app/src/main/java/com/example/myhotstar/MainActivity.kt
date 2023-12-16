package com.example.myhotstar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.myhotstar.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.gtSignin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSingup.setOnClickListener {
            val username = binding.inputUname.text.toString()
            val password = binding.inputPass.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Lakukan validasi untuk username sesuai kebutuhan
                    firebaseAuth.createUserWithEmailAndPassword("$username@yourdomain.com", password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "User Created", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to create user: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                Log.e("TAG", "Failed to create user", task.exception)
                            }
                        }
            } else {
                Toast.makeText(this, "Please fill in both username and password", Toast.LENGTH_SHORT).show()
            }
        }


    }
}