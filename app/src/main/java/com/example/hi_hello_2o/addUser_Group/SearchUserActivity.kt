package com.example.hi_hello_2o.addUser_Group

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import com.example.hi_hello_2.models.User
import com.example.hi_hello_2o.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.protobuf.Empty
import com.google.protobuf.NullValue
import kotlinx.android.synthetic.main.activity_search_user.*

class SearchUserActivity : AppCompatActivity() {

    private val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
    }

    var searched:User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

             database.whereEqualTo("phonenumber", query).get().addOnCompleteListener { documents->

                Log.i("User", " Data  ${documents.result}")

                 for(document in documents){

                 }

             }.addOnFailureListener {
                Toast.makeText(this@SearchUserActivity, "User Not Found",Toast.LENGTH_SHORT).show()
            }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
             return false
            }

        })
    }
}