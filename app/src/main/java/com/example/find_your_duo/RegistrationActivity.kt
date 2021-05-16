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
    private var mBiografia: EditText? = null
    private var mRadioGroup: RadioGroup? = null
    private var mRadioGroupBuscar: RadioGroup? = null
    private var mAuth: FirebaseAuth? = null
    private var firebaseAuthStateListener: AuthStateListener? = null
    private var PC: CheckBox?=null
    private var Playstation: CheckBox?=null
    private var Xbox : CheckBox?=null
    private var Nintendo : CheckBox?=null
    private var Movil: CheckBox?=null
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
        mBiografia = findViewById<View>(R.id.biografia) as EditText
        mPassword = findViewById<View>(R.id.password) as EditText
        mName = findViewById<View>(R.id.name) as EditText
        mRadioGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        mRadioGroupBuscar = findViewById<View>(R.id.radioGroupBusca) as RadioGroup
        PC = findViewById<View>(R.id.checkBoxPc) as CheckBox
        Playstation = findViewById<View>(R.id.checkBoxPlaytation) as CheckBox
        Xbox = findViewById<View>(R.id.checkBoxXbox) as CheckBox
        Nintendo = findViewById<View>(R.id.checkBoxNintendo) as CheckBox
        Movil = findViewById<View>(R.id.checkBoxMovil) as CheckBox
        mRegister!!.setOnClickListener(View.OnClickListener {
            val selectId = mRadioGroup!!.checkedRadioButtonId
            val selectIdBuscaSexo = mRadioGroupBuscar!!.checkedRadioButtonId
            val radioButtonBuscaSexo = findViewById<View>(selectIdBuscaSexo) as RadioButton
            if(radioButtonBuscaSexo.text == null) return@OnClickListener
            val radioButton = findViewById<View>(selectId) as RadioButton
            if (radioButton.text == null) return@OnClickListener
            val email = mEmail!!.text.toString()
            val password = mPassword!!.text.toString()
            val name = mName!!.text.toString()
            val biografia = mBiografia!!.text.toString()
            mAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener(this@RegistrationActivity) { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@RegistrationActivity, "sign up error", Toast.LENGTH_SHORT).show()
                } else {
                    val userId = mAuth?.getCurrentUser()!!.uid
                   // val currentUserDbsex = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Sex").child("sexo")
                 //   currentUserDbsex.setValue(radioButton.text)
                   // val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Sex").child("Name")
                  //  currentUserDb.setValue(name)

                    val currentUserDb = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
                    val userInfo: HashMap<Any?, Any?> = HashMap<Any?, Any?>()
                    userInfo["Name"] = name
                    userInfo["sexo"] = radioButton.text.toString()
                    userInfo["buscarSexo"] = radioButtonBuscaSexo.text.toString()
                    userInfo["biografia"] = biografia
                    userInfo["profileImageUrl"] = "https://www.miwuki.com/wp-content/uploads/2016/11/gatito-830x623.jpg"
                    userInfo["PC"] = checkIfItsChecked(PC!!)
                    userInfo["Playstation"] = checkIfItsChecked(Playstation!!)
                    userInfo["Xbox"] = checkIfItsChecked(Xbox!!)
                    userInfo["Nintendo"] = checkIfItsChecked(Nintendo!!)
                    userInfo["Movile"] = checkIfItsChecked(Movil!!)

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

    private fun checkIfItsChecked(checkBox: CheckBox): Boolean{
        return checkBox.isChecked
    }
}