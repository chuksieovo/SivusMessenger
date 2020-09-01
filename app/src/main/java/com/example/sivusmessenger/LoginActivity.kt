package com.example.sivusmessenger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            val email = login_edittext_email.text.toString()
            val password = login_edittext_password.text.toString()

            Log.d("LoginActivity", "Attempt login with e-mail & password: $email/***")
        }
        backto_main_activity.setOnClickListener {
            finish()
        }
    }
}