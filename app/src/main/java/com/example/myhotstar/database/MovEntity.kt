package com.example.myhotstar.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "localmovies")
data class MovEntity(
    val id: String,
    @PrimaryKey
    val judul: String,
    val deskripsi: String,
    val imageUrl: String,
    val genre: String,
    val director: String,
    val region: String,
)
