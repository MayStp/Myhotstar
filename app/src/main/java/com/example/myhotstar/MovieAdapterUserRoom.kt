package com.example.myhotstar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class MovieAdapterUserRoom(private var movieList: List<MovieModel>, private val onItemClick: (MovieModel) -> Unit) :
    RecyclerView.Adapter<MovieAdapterUserRoom.RoomMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomMovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_user, parent, false)
        return RoomMovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomMovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie)
        holder.itemView.setOnClickListener {
            onItemClick(movie)
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    fun setMovies(movies: List<MovieModel>) {
        movieList = movies
        notifyDataSetChanged()
    }

    inner class RoomMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvJudul: TextView = itemView.findViewById(R.id.movie_name)
        private val ivImage: ImageView = itemView.findViewById(R.id.movie_poster)

        fun bind(movie: MovieModel) {
            tvJudul.text = movie.judul

            Glide.with(itemView)
                .load(movie.imageUrl)
                .error(R.drawable.ic_launcher_background)
                .into(ivImage)
        }
    }
}
