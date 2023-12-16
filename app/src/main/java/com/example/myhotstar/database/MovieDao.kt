package com.example.myhotstar.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface MovieDao {
    @Query("SELECT * FROM localmovies")
    fun getAllMovies(): List<MovEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(localmovies: List<MovEntity>): List<Long>

    // Pada DAO
    @Transaction
    fun insertMovies(localmovies: List<MovEntity>) {
        val existingMovies = getAllMovies()
        val moviesToInsert = localmovies.filter { newMovie ->
            existingMovies.none { it.judul == newMovie.judul && it.director == newMovie.director && it.deskripsi == newMovie.deskripsi } // Memeriksa kombinasi atribut untuk memastikan ketidakterduplikatan
        }
        val insertedRowIds = insertAll(moviesToInsert)
        Log.d("LocalDatabase", "Inserted ${insertedRowIds.size} rows into local database")
    }

}



