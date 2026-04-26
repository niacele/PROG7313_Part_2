package com.example.budgetbuddy

import Data.database.AppDatabase
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.jvm.java

class Login : AppCompatActivity() {

    //global declarations
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegisterPage: Button

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        //typecasting
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegisterPage = findViewById(R.id.btnRegisterPage)

        //initialize database
        db = AppDatabase.getDatabase(applicationContext)

        //button onclick listener
        btnLogin.setOnClickListener {
            loginUser()
        }

        btnRegisterPage.setOnClickListener {
            openRegisterScreen()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun loginUser(){
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()

        //validate email and password
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        //lifecycle.Scope
        lifecycleScope.launch {
            //matching email and password
            val foundUser = db.userDao().loginUser(email, password)

            //if user exists
            runOnUiThread {
                if(foundUser != null){
                    Toast.makeText(this@Login, "Login Successful", Toast.LENGTH_SHORT).show()

                    //go to home page
                    openHomePage(foundUser.email)
                } else {
                    //if user doesn't exist
                    Toast.makeText(this@Login, "Invalid Username or Password. Please try again!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun openRegisterScreen(){
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
    private fun openHomePage(email: String){
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }
}