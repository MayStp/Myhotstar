package com.example.myhotstar

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.example.myhotstar.databinding.ActivityComingSoonBinding
import com.example.myhotstar.databinding.FragmentFragCrudBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask



class ComingSoon : AppCompatActivity() {

    private lateinit var binding: ActivityComingSoonBinding
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private val channelId = "i.apps.notifications"
    private val notifId = 90

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComingSoonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageReference = FirebaseStorage.getInstance().reference.child("uploads")

        binding.btnPilihgambar.setOnClickListener {
            openFileChooser()
        }

        binding.btnAddnewmov.setOnClickListener {
            uploadDataWithFile()
        }

        binding.btnBcktoadmincrud.setOnClickListener {
            val intent = Intent(this, Admincrud::class.java)
            startActivity(intent)
            finish()
        }
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

    private fun uploadDataWithFile() {
        // Mendapatkan data dari EditText atau TextInputLayout
        val judul = binding.inputJudul.text.toString().trim()
        val genre = binding.inputGenre.text.toString().trim()
        val region = binding.inputRegion.text.toString().trim()
        val deskripsi = binding.inputDeskripsi.text.toString().trim()
        val director = binding.inputDirector.text.toString().trim()

        // Cek apakah semua data sudah diisi dan gambar sudah dipilih
        if (judul.isNotEmpty() && deskripsi.isNotEmpty() && genre.isNotEmpty() && region.isNotEmpty() && director.isNotEmpty() && imageUri != null) {
            // Upload gambar terlebih dahulu
            uploadFile { imageUrl ->
                // Setelah gambar terupload, simpan data ke Firestore
                val data = hashMapOf(
                    "judul" to judul,
                    "genre" to genre,
                    "region" to region,
                    "deskripsi" to deskripsi,
                    "director" to director,
                    "imageUrl" to imageUrl // URL dari gambar yang sudah diupload
                )

                val db = Firebase.firestore
                db.collection("movies")
                    .add(data)
                    .addOnSuccessListener { documentReference ->
// Membuat notifikasi
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val name = "Channel Name" // Ganti dengan nama saluran notifikasi Anda
                            val descriptionText = "Deskripsi saluran notifikasi"
                            val importance = NotificationManager.IMPORTANCE_DEFAULT
                            val channel = NotificationChannel(channelId, name, importance).apply {
                                description = descriptionText
                            }
                            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            notificationManager.createNotificationChannel(channel)
                        }

                        // Mengatur aksi saat notifikasi diklik
                        val intent = Intent(this, Admincrud::class.java)
                        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                        val builder = NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.drawable.ic_bell)
                            .setContentTitle("Data berhasil disimpan")
                            .setContentText("Klik untuk melihat")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setCategory(NotificationCompat.CATEGORY_SOCIAL)

                        builder.setContentIntent(pendingIntent)

                        Glide.with(this)
                            .asBitmap()
                            .load(imageUrl)
                            .into(object : SimpleTarget<Bitmap>() {
                                @SuppressLint("MissingPermission")
                                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(resource))
                                    val notificationManager = NotificationManagerCompat.from(this@ComingSoon)
                                    notificationManager.notify(notifId, builder.build())

                                    // Memulai aktivitas setelah notifikasi selesai ditampilkan
                                    val intent = Intent(this@ComingSoon, Admincrud::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            })

                        Toast.makeText(this, "Data berhasil disimpan di Firestore", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Pastikan semua kolom terisi dan gambar sudah dipilih", Toast.LENGTH_SHORT).show()
        }
    }

}