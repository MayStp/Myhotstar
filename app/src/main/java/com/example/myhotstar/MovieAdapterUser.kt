package com.example.myhotstar

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myhotstar.database.MovieEntity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MovieAdapterUser(options: FirestoreRecyclerOptions<MovieModel>) :
    FirestoreRecyclerAdapter<MovieModel, MovieAdapterUser.MovieViewHolderz>(options) {


    private var movieList: List<MovieModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolderz {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_user, parent, false)
        return MovieViewHolderz(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolderz, position: Int, model: MovieModel) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, Detailmov::class.java)
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

    inner class MovieViewHolderz(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tv_judul: TextView = itemView.findViewById(R.id.movie_name)
        private val iv_image: ImageView = itemView.findViewById(R.id.movie_poster)

        fun bind(movie: MovieModel) {
            tv_judul.text = movie.judul

            Glide.with(itemView)
                .load(movie.imageUrl) // Ganti dengan field URL gambar di model MovieModel
                .error(R.drawable.ic_launcher_background) // Tampilan jika terjadi kesalahan saat memuat gambar
                .into(iv_image)
        }
    }
}



