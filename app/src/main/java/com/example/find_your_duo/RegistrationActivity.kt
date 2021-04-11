package com.example.find_your_duo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import java.util.*

class RegistrationActivity : AppCompatActivity() {
    private var mRegister: Button? = null
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mName: EditText? = null
    private var mRadioGroup: RadioGroup? = null
    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthStateListener: AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        mAuth = FirebaseAuth.getInstance()
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return@AuthStateListener
            }
        }
        mRegister = findViewById<View>(R.id.register) as Button
        mEmail = findViewById<View>(R.id.email) as EditText
        mPassword = findViewById<View>(R.id.password) as EditText
        mName = findViewById<View>(R.id.name) as EditText
        mRadioGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        mRegister!!.setOnClickListener(View.OnClickListener {
            val selectId = mRadioGroup!!.checkedRadioButtonId
            val radioButton = findViewById<View>(selectId) as RadioButton
            if (radioButton.text == null) {
                return@OnClickListener
            }
            val email = mEmail!!.text.toString()
            val password = mPassword!!.text.toString()
            val name = mName!!.text.toString()
            mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this@RegistrationActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@RegistrationActivity, "sign up error", Toast.LENGTH_SHORT).show()
                } else {
                    val userId = mAuth?.getCurrentUser()!!.uid
                   // val currentUserDbsex = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Sex").child("sexo")
                 //   currentUserDbsex.setValue(radioButton.text)
                   // val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Sex").child("Name")
                  //  currentUserDb.setValue(name)

                    val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Sex")
                    val userInfo: HashMap<Any?, Any?> = HashMap<Any?, Any?>()
                    userInfo["Name"] = name
                    userInfo["sexo"] = radioButton.text.toString()
                    userInfo["profileImageUrl"] = "https://www.miwuki.com/wp-content/uploads/2016/11/gatito-830x623.jpg"
                    currentUserDb.updateChildren(userInfo as Map<String, Any>)

                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(firebaseAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(firebaseAuthStateListener!!)
    }
}