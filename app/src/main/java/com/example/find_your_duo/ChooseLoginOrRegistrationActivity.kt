package com.example.find_your_duo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button

class ChooseLoginOrRegistrationActivity : AppCompatActivity() {
    private var mLogin: Button? = null
    private var mRegister: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login_or_registration)
        mLogin = findViewById<View>(R.id.login) as Button
        mRegister = findViewById<View>(R.id.register) as Button

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
