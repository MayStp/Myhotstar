package com.example.myhotstar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myhotstar.database.MovEntity

class MovieLocalAdapter(private val movieList: List<MovEntity>) :
    RecyclerView.Adapter<MovieLocalAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_user, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie)
        holder.itemView.setOnClickListener {
            // Implementasi untuk menangani klik item jika diperlukan
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvJudul: TextView = itemView.findViewById(R.id.movie_name)
        private val ivImage: ImageView = itemView.findViewById(R.id.movie_poster)

        fun bind(movie: MovEntity) {
            tvJudul.text = movie.judul

            // Implementasi Glide untuk memuat gambar dari URL atau lokal jika ada
            Glide.with(itemView)
                .load(movie.imageUrl) // Ubah dengan URL atau path gambar yang sesuai
                .error(R.drawable.ic_launcher_background) // Tampilan jika terjadi kesalahan saat memuat gambar
                .into(ivImage)
        }
    }
}
