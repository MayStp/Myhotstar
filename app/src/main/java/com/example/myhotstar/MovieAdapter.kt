package com.example.myhotstar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MovieAdapter(options: FirestoreRecyclerOptions<MovieModel>) :
    FirestoreRecyclerAdapter<MovieModel, MovieAdapter.MovieViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int, model: MovieModel) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, Form_edit::class.java)
            intent.putExtra("judul", model.judul)
            intent.putExtra("genre", model.genre)
            intent.putExtra("region", model.region)
            intent.putExtra("director", model.director)
            intent.putExtra("deskripsi", model.deskripsi)
            intent.putExtra("imageUrl", model.imageUrl)
            intent.putExtra("movieId", snapshots.getSnapshot(holder.adapterPosition).id)
            // Tambahkan data lain yang ingin kamu kirim
            holder.itemView.context.startActivity(intent)
        }
    }


    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_judul: TextView = itemView.findViewById(R.id.tv_judul)
        private val tv_genre: TextView = itemView.findViewById(R.id.tv_genre)
        private val tv_director: TextView = itemView.findViewById(R.id.tv_director)
        private val tv_region: TextView = itemView.findViewById(R.id.tv_region)
        private val tv_deskripsi: TextView = itemView.findViewById(R.id.tv_durasi)
        private val iv_image: ImageView = itemView.findViewById(R.id.iv_poster)

        fun bind(movie: MovieModel) {
            tv_judul.text = movie.judul
            tv_genre.text = movie.genre
            tv_director.text = movie.director
            tv_region.text = movie.region
            tv_deskripsi.text = movie.deskripsi

            Glide.with(itemView)
                .load(movie.imageUrl) // Ganti dengan field URL gambar di model MovieModel
                .error(R.drawable.ic_launcher_background) // Tampilan jika terjadi kesalahan saat memuat gambar
                .into(iv_image)
        }
    }
}