package com.example.myhotstar

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myhotstar.database.MovEntity
import com.example.myhotstar.database.MovieDao
import com.example.myhotstar.database.MovieDatabase
import com.example.myhotstar.database.MovieEntity
import com.example.myhotstar.databinding.ActivityHeroBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Hero : AppCompatActivity() {
    private lateinit var binding: ActivityHeroBinding
    private lateinit var adapter: MovieAdapterUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var movieDao: MovieDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = Firebase.auth.currentUser
        val userEmail = currentUser?.email

        val userName = userEmail?.substringBefore('@')
        binding.tvUser.text = userName

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, Hero::class.java))
                    true
                }
                R.id.navigation_search -> {
                    startActivity(Intent(this, Search::class.java))
                    true
                }
                R.id.navigation_account -> {
                    startActivity(Intent(this, Accountact::class.java))
                    true
                }
                else -> false
            }
        }

        recyclerView = binding.rvMoviesUser
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        firestore = FirebaseFirestore.getInstance()
        movieDao = MovieDatabase.getDatabase(this).movieDao()

        if (isOnline()) {
            fetchDataFromFirestoreAndSaveToLocal()
        } else {
            displayLocalMovies()
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun fetchDataFromFirestoreAndSaveToLocal() {
        Log.d("FirebaseToLocal", "Mulai penyalinan data dari Firestore ke lokal")

        val firestoreMovies = firestore.collection("movies")
        firestoreMovies.get().addOnSuccessListener { documents ->
            val movieModels = mutableListOf<MovieModel>()
            for (document in documents) {
                val movie = document.toObject<MovieModel>()
                movieModels.add(movie)
            }

            Log.d("FirebaseToLocal", "Jumlah data terambil dari Firestore: ${movieModels.size}")

            val movieEntities = convertToMovieEntity(movieModels)
            Log.d("FirebaseToLocal", "Jumlah data yang akan disimpan: ${movieEntities.size}")

            CoroutineScope(Dispatchers.IO).launch {
                // Menambah log sebelum pemanggilan insertMovies
                Log.d("FirebaseToLocal", "Memanggil insertMovies pada DAO")

//                val insertedRowIds = movieDao.insertMovies(movieEntities)
//                val insertedRowsCount = insertedRowIds.size

                movieDao.insertMovies(movieEntities)
                withContext(Dispatchers.Main) {
                    Log.d("FirebaseToLocal", "Penyalinan data selesai")
                    setupFirestoreAdapter()
                }
            }

        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: $exception")
        }
    }








    private fun convertToMovieEntity(movieModels: List<MovieModel>): List<MovEntity> {
        val movieEntities = mutableListOf<MovEntity>()
        for (movieModel in movieModels) {
            val movsEntity = MovEntity(
                movieModel.id,
                movieModel.judul,
                movieModel.deskripsi,
                movieModel.imageUrl,
                movieModel.genre,
                movieModel.director,
                movieModel.region
            )
            Log.d("PenyalinanDataFilm", "Menyimpan data film ${movieModel.judul} ${movieModel.genre},${movieModel.id}, dll")
            movieEntities.add(movsEntity)
        }
        Log.d("FirebaseToLocal", "Jumlah data yang berhasil dikonversi: ${movieEntities.size}")
        return movieEntities
    }


    private fun setupFirestoreAdapter() {
        val query = firestore.collection("movies")
        val options = FirestoreRecyclerOptions.Builder<MovieModel>()
            .setQuery(query, MovieModel::class.java)
            .build()
        adapter = MovieAdapterUser(options)
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    private fun displayLocalMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            val movieList = movieDao.getAllMovies()
            Log.d("LocalDatabase", "Retrieved ${movieList.size} rows from local database")
            withContext(Dispatchers.Main) {
                val localAdapter = MovieLocalAdapter(movieList)
                recyclerView.adapter = localAdapter
            }
        }
    }


}







