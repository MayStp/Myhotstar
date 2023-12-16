package com.example.myhotstar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.myhotstar.databinding.ActivityDetailMovBinding

class Detailmov : AppCompatActivity() {
    private lateinit var binding: ActivityDetailMovBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMovBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeDetails.setOnClickListener {
            val intent = Intent(this@Detailmov, Hero::class.java)
            startActivity(intent)
        }

        binding.btnWatchnow.setOnClickListener {
            val intent = Intent(this@Detailmov, Watchnow::class.java)
            startActivity(intent)
        }

        val judul = intent.getStringExtra("judul")
        val genre = intent.getStringExtra("genre")
        val region = intent.getStringExtra("region")
        val director = intent.getStringExtra("director")
        val deskripsi = intent.getStringExtra("deskripsi")
        val imageUrl = intent.getStringExtra("imageUrl")
        // Ambil data lain jika ada

        // Isi form dengan data yang diterima dari intent
        binding.dtMovieName.setText(judul)
        binding.dtGenre.setText(genre)
        binding.dtRegion.setText(region)
        binding.dtDirector.setText(director+"  |")
        binding.deskripsiFilm.setText(deskripsi)
        // Isi form dengan data lain jika ada
        Glide.with(this)
            .load(imageUrl) // Ganti dengan field URL gambar di model MovieModel
            .error(R.drawable.ic_launcher_background) // Tampilan jika terjadi kesalahan saat memuat gambar
            .into(binding.movPosterLandscape)
    }
}