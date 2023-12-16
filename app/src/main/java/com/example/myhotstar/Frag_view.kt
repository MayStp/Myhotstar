package com.example.myhotstar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class Frag_view : Fragment() {
    // Deklarasi variabel lain yang diperlukan
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var query: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_frag_view, container, false)

        recyclerView = view.findViewById(R.id.rv_movie)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        firestore = FirebaseFirestore.getInstance()

        query = firestore.collection("movies")

        val options = FirestoreRecyclerOptions.Builder<MovieModel>()
            .setQuery(query, MovieModel::class.java)
            .build()

        adapter = MovieAdapter(options) // Inisialisasi adapter dengan options
        recyclerView.adapter = adapter // Set adapter ke RecyclerView

        return view
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening() // Mulai mendengarkan perubahan data dari Firestore
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening() // Hentikan mendengarkan saat Fragment dihentikan
    }
}
