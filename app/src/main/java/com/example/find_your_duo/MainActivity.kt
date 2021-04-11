package com.example.find_your_duo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.find_your_duo.Cards.arrayAdapter
import com.example.find_your_duo.Cards.cards
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lorentzos.flingswipe.SwipeFlingAdapterView


class MainActivity : AppCompatActivity() {
  val al: MutableList<String> = mutableListOf()
    private var mAuth: FirebaseAuth? = null
    private var currentUId: String? = null
    private var usersDb: DatabaseReference? = null
    private val i = 0
    private var arrayAdapter: arrayAdapter? = null

    var listView: ListView? = null
    var rowItems: MutableList<cards>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        usersDb = FirebaseDatabase.getInstance().reference.child("Users")
        currentUId = FirebaseAuth.getInstance().currentUser.uid
        checkUserSex()
        rowItems = ArrayList()
        arrayAdapter = arrayAdapter(this, R.layout.item, rowItems)
        val flingContainer = findViewById<View>(R.id.frame) as SwipeFlingAdapterView
        flingContainer.adapter = arrayAdapter
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!")
                rowItems?.removeAt(0)
                arrayAdapter!!.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                val obj = p0 as cards
                val userId = obj.userId
                usersDb?.child(userId)?.child("Sex")?.child("connections")?.child("Nego")?.child(currentUId!!)?.setValue(true)
                Toast.makeText(this@MainActivity, "Left", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(p0: Any?) {
                val obj = p0 as cards
                val userId = obj.userId
                usersDb?.child(userId)?.child("Sex")?.child("connections")?.child("Acepto")?.child(currentUId!!)?.setValue(true)
                isConnectionMatch(userId)
                Toast.makeText(this@MainActivity, "Rigth", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(p0: Int) {}

            override fun onScroll(p0: Float) {}
        })
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener { itemPosition, dataObject -> Toast.makeText(this@MainActivity, "Item Clicked", Toast.LENGTH_SHORT).show() }

    }

    private fun isConnectionMatch(userId: String) {
        val currentUserConnectionsDb = usersDb!!.child(currentUId!!).child("Sex").child("connections").child("Acepto").child(userId)
        currentUserConnectionsDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(this@MainActivity, "new Connection", Toast.LENGTH_LONG).show()
                   // val key = FirebaseDatabase.getInstance().reference.child("Chat").push().key
                    usersDb!!.child(dataSnapshot.key!!).child("Sex").child("connections").child("Matches").child(currentUId!!).setValue(true)
                    usersDb!!.child(currentUId!!).child("Sex").child("connections").child("Matches").child(dataSnapshot.key!!).setValue(true)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private var userSex: String? = null
    private var oppositeUserSex: String? = null
    private var idUsuario: String? = null
    fun checkUserSex() {
        var b= 2;
        val user = FirebaseAuth.getInstance().currentUser
         idUsuario = user!!.uid;
        val userDb = usersDb!!.child(user!!.uid)
        userDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("Sex").child("Name").value != null) {
                        userSex = dataSnapshot.child("Sex").child("sexo").value.toString()
                        when (userSex) {
                            "Male" -> oppositeUserSex = "Female"
                            "Female" -> oppositeUserSex = "Male"
                        }
                        oppositeSexUsers
                    }
                }
            }

        })

    }


    val oppositeSexUsers: Unit
    get(){
        usersDb!!.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                if (dataSnapshot.child("Sex").child("Name").value != null){

                    if (dataSnapshot.exists() &&
                            (dataSnapshot.key.toString() != idUsuario) &&
                            !dataSnapshot.child("Sex")?.child("connections")?.child("Acepto")?.hasChild(currentUId!!) &&
                            !dataSnapshot.child("Sex")?.child("connections")?.child("Nego")?.hasChild(currentUId!!) )
                    {
                        if (dataSnapshot.child("Sex").child("Sexo").value.toString() == oppositeUserSex) {
                            var profileImageUrl = "default"
                        }
                        var profileImageUrl = "default"
                        if (dataSnapshot.child("Sex").child("profileImageUrl").value != "default") {
                            profileImageUrl = dataSnapshot.child("Sex").child("profileImageUrl").value.toString()
                        }
                        al.add(dataSnapshot.child("Sex").child("Name").value.toString())
                        val item = cards(dataSnapshot.key.toString(), dataSnapshot.child("Sex").child("Name").value.toString(),profileImageUrl)
                        rowItems!!.add(item)
                        arrayAdapter!!.notifyDataSetChanged()

                    }

                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })
    }


    fun logoutUser(view: View?) {
        FirebaseAuth.getInstance().signOut();
        val intent = Intent(this@MainActivity, ChooseLoginOrRegistrationActivity::class.java)
        startActivity(intent)
        finish()
        return
    }

    fun goToSettings(view: View?) {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
        return
    }
}