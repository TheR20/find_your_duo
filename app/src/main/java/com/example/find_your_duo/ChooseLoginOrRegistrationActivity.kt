package com.example.find_your_duo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth


class ChooseLoginOrRegistrationActivity : AppCompatActivity() {
    private var mLogin: Button? = null
    private var mRegister: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_or_registration)
        mLogin = findViewById<View>(R.id.login) as Button
        mRegister = findViewById<View>(R.id.register) as Button
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
            val i = Intent(this@ChooseLoginOrRegistrationActivity, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        } else {
            // User is signed out
            Log.d("tag", "onAuthStateChanged:signed_out")
        }
        mLogin!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ChooseLoginOrRegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })

        mRegister!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ChooseLoginOrRegistrationActivity, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
            return@OnClickListener
        })
    }
}
