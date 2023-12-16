package com.example.myhotstar.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mmovies")
data class MovieEntity(
    @PrimaryKey val movid: String,
    val judul: String,
    val deskripsi: String,
    val imageUrl: String,
    val genre: String,
    val director: String,
    val region: String,
)

