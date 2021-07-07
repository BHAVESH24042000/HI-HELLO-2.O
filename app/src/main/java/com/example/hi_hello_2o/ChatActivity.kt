package com.example.hi_hello_2o

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hi_hello.KeyboardVisibilityUtil
import com.example.hi_hello_2.models.Inbox
import com.example.hi_hello_2.models.User
import com.example.hi_hello_2o.adapters.ChatAdapter
import com.example.hi_hello_2o.models.ChatEvent
import com.example.hi_hello_2o.models.DateHeader
import com.example.hi_hello_2o.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.*


const val UID: String="uid"
const val NAME="name"
const val IMAGE="photo"

class ChatActivity : AppCompatActivity() {

    private val friendId: String by lazy {
        intent.getStringExtra(UID)!!
    }

    private val name: String by lazy {
        intent.getStringExtra(NAME)!!
    }

    private val image: String by lazy {
        intent.getStringExtra(IMAGE)!!
    }

    private val mCurrentUid: String by lazy {
        FirebaseAuth.getInstance().uid!!
    }

    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    lateinit var currentUser: User
    lateinit var chatAdapter: ChatAdapter

    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val mutableItems: MutableList<ChatEvent> = mutableListOf()
    private val mLinearLayout: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiManager.install(GoogleEmojiProvider()) // to set emojis, use this before set content view
        setContentView(R.layout.activity_chat)

        keyboardVisibilityHelper = KeyboardVisibilityUtil(rootView) {
            msgRv.scrollToPosition(mutableItems.size - 1)
        }

        val emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(msgEdtv)
        smileBtn.setOnClickListener {
            emojiPopup.toggle()
        }

        nameTv.text = name
        Picasso.get().load(image).into(userImgView)

        // To get current user from firestore
        lifecycleScope.launch(Dispatchers.IO) {
            FirebaseFirestore.getInstance().collection("users").document(mCurrentUid).get()
                .addOnSuccessListener {
                    currentUser = it.toObject(User::class.java)!!
                }
        }

        chatAdapter = ChatAdapter(mutableItems, mCurrentUid)

        msgRv.apply {
            layoutManager = mLinearLayout
            adapter = chatAdapter
        }

        listenMessages()

        sendBtn.setOnClickListener {
            msgEdtv.text?.let {
                if (it.isNotEmpty()) {
                    sendMessage(it.toString())
                    it.clear()
                }
            }
        }

       // updateReadCount()
    }

    private fun updateReadCount() {
        getInbox(mCurrentUid, friendId).child("count").setValue(0)
    }
    private fun sendMessage(msg: String) {
        val id = getMessages(friendId).push().key // generate a unique key for every message to send for the specified path
        checkNotNull(id) { "Cannot be null" }
        val msgMap = Message(msg, mCurrentUid , id) // arguments( message, senderId, messageId)

        lifecycleScope.launch(Dispatchers.IO) {
            getMessages(friendId).child(id).setValue(msgMap).addOnSuccessListener {
            // to send meesage
                // Log.i("Chats", "stored successfully on realtimeDb")
            }
            updateLastMessage(msgMap, mCurrentUid)
        }
    }
    private fun getMessages(friendId: String) =
        db.reference.child("messages/${getId(friendId)}")

    private fun getInbox(toUser: String, fromUser: String) =
        db.reference.child("chats/$toUser/$fromUser")

    private fun getId(friendId: String): String {  // unique ID for messages between two persons
        return if (friendId > mCurrentUid) {
            mCurrentUid + friendId
        } else {
            friendId + mCurrentUid
        }
    }

    private fun updateLastMessage(message: Message, mCurrentUid: String) { // to read the count of unread messages from a particular user
        val inboxMap = Inbox(
            message.msg,
            friendId,
            name,
            image,
            message.sentAt,
            0
        )

        lifecycleScope.launch(Dispatchers.IO) {
            getInbox(mCurrentUid, friendId).setValue(inboxMap)
        }

        getInbox(friendId, mCurrentUid).addListenerForSingleValueEvent(object : // listerner to get value for once
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val value = snapshot.getValue(Inbox::class.java)
                    inboxMap.apply {
                        from = message.senderId
                        name = currentUser.name
                        image = currentUser.thumbImage
                        count = 1
                    }
                    if (value?.from == message.senderId) {
                        inboxMap.count = value.count + 1 // to add the count of unread messages by 1
                    }

                    lifecycleScope.launch(Dispatchers.IO) {
                    getInbox(friendId, mCurrentUid).setValue(inboxMap)
                        }
                }

            }

        })
    }

    private fun listenMessages (){   //  (newMsg: (msg: Message, update: Boolean) -> Unit) {
        getMessages(friendId)
            .orderByKey()
            .addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(data: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(data: DataSnapshot, p1: String?) {
                    //var msg : Message? = null
                  /*  GlobalScope.launch(Dispatchers.IO) {
                        CoroutineScope(Dispatchers.IO).launch {
                          CoroutineScope(Dispatchers.Main).launch {
                          }
                      }
                    }*/


                    val msg = data.getValue(Message::class.java)!!
                    addMessage(msg)
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

            })

    }
    private fun addMessage(event: Message) {
        val eventBefore = mutableItems.lastOrNull()
        // Add date header if it's a different day
        if ((eventBefore != null
                    && !eventBefore.sentAt.isSameDayAs(event!!.sentAt))
            || eventBefore == null
        ) {
            if (event != null) {
                mutableItems.add(
                    DateHeader(
                        event.sentAt, this
                    )
                )
            }
        }

        mutableItems.add(event)
        chatAdapter.notifyItemInserted(mutableItems.size)
        msgRv.scrollToPosition(mutableItems.size - 1)
    }
}