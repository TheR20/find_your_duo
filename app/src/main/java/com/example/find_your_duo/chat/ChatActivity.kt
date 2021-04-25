
package com.example.find_your_duo.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.find_your_duo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null
    private var mChatAdapter: RecyclerView.Adapter<*>? = null
    private var mChatLayoutManager: RecyclerView.LayoutManager? = null
    private var mSendEditText: EditText? = null
    private var mSendButton: Button? = null
    private var currentUserID: String? = null
    private var matchId: String? = null
    private var chatId: String? = null
        private get() {
            mDatabaseUser!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        field = dataSnapshot.value.toString()
                        mDatabaseChat = mDatabaseChat!!.child(field!!)
                        chatMessages
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            return chatId
        }
    var mDatabaseUser: DatabaseReference? = null
    var mDatabaseChat: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        matchId = intent.extras!!.getString("matchId")
        currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        mDatabaseUser = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserID!!).child("connections").child("matches").child(matchId!!).child("ChatId")
        mDatabaseChat = FirebaseDatabase.getInstance().reference.child("Chat")
        mRecyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        mRecyclerView!!.isNestedScrollingEnabled = false
        mRecyclerView!!.setHasFixedSize(false)
        mChatLayoutManager = LinearLayoutManager(this@ChatActivity)
        mRecyclerView!!.layoutManager = mChatLayoutManager
        mChatAdapter = ChatAdapter(dataSetChat, this@ChatActivity)
        mRecyclerView!!.adapter = mChatAdapter
        mSendEditText = findViewById(R.id.message)
        mSendButton = findViewById(R.id.send)
        mSendButton?.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText!!.text.toString()
        if (sendMessageText.isNotEmpty()) {
            val newMessageDb = mDatabaseChat!!.push()
            val newMessage: MutableMap<*, *> = HashMap<Any?, Any?>()
            newMessage["createdByUser"] = currentUserID
            newMessage["text"] = sendMessageText
            newMessageDb.setValue(newMessage)
        }
        mSendEditText.run {
            if (sendMessageText.isNotEmpty()) {
                val newMessageDb = mDatabaseChat!!.push()
                val newMessage: MutableMap<*, *> = HashMap<Any?, Any?>()
                newMessage["createdByUser"] = currentUserID
                newMessage["text"] = sendMessageText
                newMessageDb.setValue(newMessage)
            }
            setText()
        }
    }

    private val chatMessages: Unit
        private get() {
            mDatabaseChat!!.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()) {
                        var message: String? = null
                        var createdByUser: String? = null
                        if (snapshot.child("text").value != null) {
                            message = snapshot.child("text").value.toString()
                        }
                        if (snapshot.child("createdByUser").value != null) {
                            createdByUser = snapshot.child("createdByUser").value.toString()
                        }
                        if (message != null && createdByUser != null) {
                            var currentUserBoolean = false
                            if (createdByUser == currentUserID) {
                                currentUserBoolean = true
                            }
                            val newMessage = ChatObject(message, currentUserBoolean)
                            resultsChat.add(newMessage)
                            mChatAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

            })
        }

    private val resultsChat = ArrayList<ChatObject>()
    private val dataSetChat: List<ChatObject>
        private get() = resultsChat
}

private fun EditText?.setText() {

}


private operator fun <K, V> MutableMap<K, V>.set(v: String, value: String?) {

}
