
package com.example.find_your_duo.chat

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.find_your_duo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

//
var mDatabaseUser: DatabaseReference? = null
var mDatabaseChat: DatabaseReference? = null
var mDatabaseChatChild: DatabaseReference? = null
var IdMatch: String? = null
var secundMatch : String = ""
class ChatActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null

    private var mChatAdapter: RecyclerView.Adapter<*>? = null
    private var mChatLayoutManager: RecyclerView.LayoutManager? = null
    private var mSendEditText: EditText? = null
    private var mSendButton: Button? = null
    private var currentUserID: String? = null
    private var matchId: String? = null
    private var chatId: String? = null

        get() {
            mDatabaseUser!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var field = dataSnapshot.value.toString()
                        mDatabaseChat = mDatabaseChat!!.child(field!!)

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
            return chatId
        }


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
        mRecyclerView!!.viewTreeObserver.addOnGlobalLayoutListener { scrollToEnd() }
        getChatID()
//        secundMatch = IdMatch!!
       // chatMessages
        gotchaMessages()
      //  lastchild()
        gotchachilds()
    }

    private fun sendMessage() {
        val sendMessageText = mSendEditText!!.text.toString()
        if (sendMessageText.isNotEmpty()) {
            val newMessageDb = mDatabaseChat!!.push()
            val newMessage: MutableMap<*, *> = HashMap<Any?, Any?>()
           // newMessage["createdByUser"] = currentUserID
           // newMessage["text"] = sendMessageText
            newMessageDb.child("createdByUser").setValue(currentUserID)
            newMessageDb.child("text").setValue(sendMessageText)

        }
        mSendEditText.run {
            if (sendMessageText.isNotEmpty()) {
                val newMessageDb = mDatabaseChat!!.push()
                val newMessage: MutableMap<*, *> = HashMap<Any?, Any?>()
                newMessage["createdByUser"] = currentUserID
                newMessage["text"] = sendMessageText
                newMessageDb.setValue(newMessage)

            }
           // mSendEditText!!.getText().clear()
            setText()
            lastchild()
            mSendEditText!!.text.clear();
            closeKeyBoard()
        }
    }
//asdasdasdasdasdasdasd

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    

    private fun gotchachilds() {
        mDatabaseChat!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                lastchild()
            }
        })
    }
    private fun scrollToEnd() =
            (mChatAdapter?.itemCount?.minus(1)).takeIf { it!! > 0 }?.let(recyclerView::smoothScrollToPosition)
    private fun gotchaMessages() {
        mDatabaseChat!!.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                var z_name = "a"
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
             //   thischildchat()
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val kakita = snapshot.key.toString()
                val kakota = IdMatch
                val f = 1
                if(kakita==kakota){
                    if (snapshot.exists()) {
                        var message: String? = null
                        var createdByUser: String? = null
                        var test = snapshot.value
                        var z_name = "a"
                        for (snapshot in snapshot.children) {
                            z_name = snapshot.child("text").value.toString()
                            message = snapshot.child("text").value.toString()
                            createdByUser = snapshot.child("createdByUser").value.toString()
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
             /*           val a = z_name
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
                        }*/
                    }
                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }


    private fun thischildchat(){
        mDatabaseChat!!.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
            /*    if (snapshot.exists()) {
                    var message: String? = null
                    var createdByUser: String? = null
                    var test = snapshot.value
                    var z_name = "a"
                    val a = z_name
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
                }*/
            }

        })
    }


    private fun lastchild(){
         mDatabaseChat!!.orderByKey().limitToLast(1).addChildEventListener(object : ChildEventListener {
             override fun onCancelled(error: DatabaseError) {

             }

             override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

             }

             override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
           /*      if (snapshot.exists()) {
                     var message: String? = null
                     var createdByUser: String? = null
                     var test = snapshot.value
                     var z_name = "a"
                     val a = z_name
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
                 }*/
             }

             override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                 if (snapshot.exists()) {
                     var message2: String? = null
                     var createdByUser: String? = null
                     var test = snapshot.value
                     var z_name = "a"
                     val a = z_name
                     if (snapshot.child("text").value != null) {
                         message2 = snapshot.child("text").value.toString()
                     }
                     if (snapshot.child("createdByUser").value != null) {
                         createdByUser = snapshot.child("createdByUser").value.toString()
                     }



                     if (message2 != null && createdByUser != null) {
                         var currentUserBoolean = false
                         if (createdByUser == currentUserID) {
                             currentUserBoolean = true
                         }
                         val newMessage = ChatObject(message2, currentUserBoolean)
                         resultsChat.add(newMessage)
                         mChatAdapter!!.notifyDataSetChanged()
                     }
                 }
             }

             override fun onChildRemoved(snapshot: DataSnapshot) {

             }


         })


    }

private val chatMessages: Unit
        private get() {


        }

    private val resultsChat = ArrayList<ChatObject>()
    private val dataSetChat: List<ChatObject>
        private get() = resultsChat
}



private fun getChatID(){

    mDatabaseUser!!.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
             ///  var quesito = mDatabaseUser!!.get().result.toString()
               var field = dataSnapshot.value.toString()
              //  if(mDatabaseUser.toString() == field)
                mDatabaseChat = mDatabaseChat!!.child(field!!)
                IdMatch= field
                mDatabaseChatChild = FirebaseDatabase.getInstance().reference.child("Chat").child(field!!)

            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    })
}
private fun EditText?.setText() {

}


private operator fun <K, V> MutableMap<K, V>.set(v: String, value: String?) {

}
