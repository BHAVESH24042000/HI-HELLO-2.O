package com.example.hi_hello_2o.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.hi_hello_2.models.User
import com.example.hi_hello_2o.*
import com.example.hi_hello_2o.adapters.UsersAdapter.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UsersAdapter(options: FirestoreRecyclerOptions<User>) : FirestoreRecyclerAdapter<User, UserViewHolder>(
    options
) {
    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(user: User, onClick: (name: String, photo: String, id: String)->Unit)= with(itemView){

            countTv.isVisible=false;
            timeTv.isVisible=false

            titleTv.text=user.name
            subTitleTv.text=user.status

            Picasso.get()
                .load(user.thumbImage)
                .placeholder(R.drawable.defaultavatar)
                .error(R.drawable.defaultavatar)
                .into(userImgView)

            setOnClickListener{
                onClick.invoke(user.name, user.thumbImage, user.uid)
            }
        }

        class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
       return UserViewHolder( parent.context.getSystemService(LayoutInflater::class.java).inflate(
           R.layout.list_item,
           parent,
           false
       ))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {

        holder.bind(model) { name: String, photo: String, id: String ->
            val intent = Intent( holder.itemView.context, ChatActivity::class.java)
            intent.putExtra(UID, id)
            intent.putExtra(NAME, name)
            intent.putExtra(IMAGE, photo)
            startActivity(holder.itemView.context , intent, null)
        }
    }
}