package com.example.myhotstar.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("SELECT * FROM mmovies")
    fun getAllMovies(): List<MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(mmovies: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(moviesFromFirestore: List<MovieEntity>)

    // ... tambahkan metode lain sesuai kebutuhan seperti menghapus, memperbarui, atau mencari data
}
