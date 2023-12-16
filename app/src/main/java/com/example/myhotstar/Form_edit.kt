package com.example.myhotstar

import android.app.Activity
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myhotstar.databinding.ActivityFormEditBinding
import com.example.myhotstar.databinding.FragmentFragCrudBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class Form_edit : AppCompatActivity() {
    // Deklarasi variabel lain yang diperlukan
    private lateinit var binding: ActivityFormEditBinding
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityFormEditBinding.inflate(layoutInflater)
        setContentView(binding.root) // Ganti dengan layout activity edit

        storageReference = FirebaseStorage.getInstance().reference.child("uploads")

        binding.btnPilihgambar.setOnClickListener {
            openFileChooser()
        }

        binding.btnUpdatemov.setOnClickListener {
            val judul = binding.inputJudul.text.toString().trim()
            val genre = binding.inputGenre.text.toString().trim()
            val region = binding.inputRegion.text.toString().trim()
            val deskripsi = binding.inputDeskripsi.text.toString().trim()
            val director = binding.inputDirector.text.toString().trim()
            val movieId = intent.getStringExtra("movieId") ?: ""

            // Cek apakah semua data sudah diisi
            if (judul.isNotEmpty() && deskripsi.isNotEmpty() && genre.isNotEmpty() && region.isNotEmpty() && director.isNotEmpty()) {
                if (imageUri != null) {
                    // Upload gambar terlebih dahulu jika ada perubahan gambar
                    uploadFile { newImageUrl ->
                        // Setelah gambar terupload, simpan data ke Firestore bersama URL gambar baru
                        val data = hashMapOf(
                            "judul" to judul,
                            "genre" to genre,
                            "region" to region,
                            "deskripsi" to deskripsi,
                            "director" to director,
                            "imageUrl" to newImageUrl // Gunakan URL baru jika ada perubahan gambar
                        )

                        val db = Firebase.firestore
                        db.collection("movies").document(movieId)
                            .set(data, SetOptions.merge()) // Gunakan merge untuk memperbarui data
                            .addOnSuccessListener {
                                Toast.makeText(this, "Data berhasil diperbarui di Firestore", Toast.LENGTH_SHORT).show()
                                val fragmentManager = supportFragmentManager
                                val fragView = Frag_view() // Ganti dengan nama Fragment yang menampilkan daftar film
                                val fragmentTransaction = fragmentManager.beginTransaction()
                                fragmentTransaction.replace(R.id.rv_movie, fragView) // Ganti dengan ID container Fragment yang ingin digantikan
                                fragmentTransaction.commit()
                            }.addOnFailureListener { e ->
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Jika tidak ada perubahan gambar, update data kecuali URL gambar
                    val data = hashMapOf(
                        "judul" to judul,
                        "genre" to genre,
                        "region" to region,
                        "deskripsi" to deskripsi,
                        "director" to director
                    )

                    val db = Firebase.firestore
                    db.collection("movies").document(movieId)
                        .set(data, SetOptions.merge()) // Gunakan merge untuk memperbarui data
                        .addOnSuccessListener {
                            Toast.makeText(this, "Data berhasil diperbarui di Firestore", Toast.LENGTH_SHORT).show()
                            val fragmentManager = supportFragmentManager
                            fragmentManager.popBackStack()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Pastikan semua kolom terisi", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDelete.setOnClickListener {
            val movieId = intent.getStringExtra("movieId") ?: ""
            val db = Firebase.firestore
            db.collection("movies").document(movieId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil dihapus di Firestore", Toast.LENGTH_SHORT).show()
                    val fragmentManager = supportFragmentManager
                    fragmentManager.popBackStack()
                    intent = Intent(this, Admincrud::class.java)
                    startActivity(intent)
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        sharedPref = getSharedPreferences("Login", MODE_PRIVATE)

//        binding.btnsignout.setOnClickListener {
//            // Set nilai isLoggedIn menjadi false
//            sharedPref.edit().putBoolean("isLogin", false).apply()
//
//            // Redirect ke layar login
//            val intent = Intent(this, Login::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Membersihkan stack activity
//            startActivity(intent)
//            finish()
//        }
//
//        binding.btnbcktoadmincrud.setOnClickListener {
//            val intent = Intent(this, Admincrud::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Membersihkan stack activity
//            startActivity(intent)
//            finish()
//        }



        // Dapatkan data yang dikirim melalui intent
        val judul = intent.getStringExtra("judul")
        val genre = intent.getStringExtra("genre")
        val region = intent.getStringExtra("region")
        val director = intent.getStringExtra("director")
        val deskripsi = intent.getStringExtra("deskripsi")
        val imageUrl = intent.getStringExtra("imageUrl")
        // Ambil data lain jika ada

        // Isi form dengan data yang diterima dari intent
        binding.inputJudul.setText(judul)
        binding.inputGenre.setText(genre)
        binding.inputRegion.setText(region)
        binding.inputDirector.setText(director)
        binding.inputDeskripsi.setText(deskripsi)
        // Isi form dengan data lain jika ada
        Glide.with(this)
            .load(imageUrl) // Ganti dengan field URL gambar di model MovieModel
            .error(R.drawable.ic_launcher_background) // Tampilan jika terjadi kesalahan saat memuat gambar
            .into(binding.imageView)


    }
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            imageUri = data.data
            binding.imageView.setImageURI(imageUri)
        }
    }

    private fun uploadFile(callback: (String) -> Unit) {
        imageUri?.let { uri ->
            val fileReference = storageReference.child("${System.currentTimeMillis()}.${getFileExtension(uri)}")
            fileReference.putFile(uri)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    // Get the download URL from the task snapshot
                    fileReference.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString() // Retrieve the URL as a string
                        callback(imageUrl) // Pass the URL through the callback
                        Toast.makeText(this, "Upload berhasil", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        // Handle failure to get download URL
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e: Exception ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "Tidak ada file yang dipilih", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }


}
