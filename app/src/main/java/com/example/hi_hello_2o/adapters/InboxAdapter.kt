package com.example.hi_hello_2o.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.hi_hello_2.models.Inbox
import com.example.hi_hello_2o.*
import com.example.hi_hello_2o.adapters.InboxAdapter.InboxViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class InboxAdapter(options: FirebaseRecyclerOptions<Inbox>): FirebaseRecyclerAdapter<Inbox, InboxViewHolder>(options) {

    class InboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Inbox, onClick: (name: String, photo: String, id: String) -> Unit) =
            with(itemView) {
                countTv.isVisible = item.count > 0
                countTv.text = item.count.toString()
                timeTv.text = item.time.formatAsListItem(context)

                titleTv.text = item.name
                subTitleTv.text = item.msg
                Picasso.get()
                    .load(item.image!!)
                    .placeholder(R.drawable.defaultavatar)
                    .error(R.drawable.defaultavatar)
                    .into(userImgView)
                setOnClickListener {
                    onClick.invoke(item.name, item.image, item.from)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InboxViewHolder {
        return InboxViewHolder(
            parent.context.getSystemService(LayoutInflater::class.java).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: InboxViewHolder, position: Int, model: Inbox) {
        holder.bind(model) { name: String, photo: String, id: String ->
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
            intent.putExtra(UID, id)
            intent.putExtra(NAME, name)
            intent.putExtra(IMAGE, photo)
            startActivity(holder.itemView.context, intent, null)

        }
    }
}