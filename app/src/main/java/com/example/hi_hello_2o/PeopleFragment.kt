package com.example.hi_hello_2o

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hi_hello_2.models.User
import com.example.hi_hello_2o.adapters.UsersAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_people.*
import kotlinx.coroutines.*


class PeopleFragment : Fragment() {

    private var mAdapter: UsersAdapter? = null
    //private lateinit var viewManager: RecyclerView.LayoutManager
    private val database by lazy {
        FirebaseFirestore.getInstance().collection("users")

    }
    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        // https://www.youtube.com/watch?v=hOebaflo7Pk&list=PLUcsbZa0qzu3Mri2tL1FzZy-5SX75UJfb&index=19

        var recyclerViewOptions : FirestoreRecyclerOptions<User>

            //withContext(Dispatchers.IO) {
            val query = database.orderBy("name", Query.Direction.ASCENDING)

            recyclerViewOptions =
                FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
                    .build()

            mAdapter = UsersAdapter(recyclerViewOptions)
            recyclerView1.adapter = mAdapter
            recyclerView1.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onStart() {
        super.onStart()
        mAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter?.stopListening()
    }

}