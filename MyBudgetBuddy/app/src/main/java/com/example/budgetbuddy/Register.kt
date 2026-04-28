package com.example.budgetbuddy

import Data.User
import Data.database.AppDatabase
import Data.getCurrentDate
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

class Register : AppCompatActivity() {

    //global declarations
    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtEmailRegister: EditText
    private lateinit var edtPasswordReg: EditText
    private lateinit var edtConfirmPasswordReg: EditText
    private lateinit var btnRegister: Button

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        //typecasting
        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtEmailRegister = findViewById(R.id.edtEmailRegister)
        edtPasswordReg = findViewById(R.id.edtPasswordReg)
        edtConfirmPasswordReg = findViewById(R.id.edtConfirmPasswordReg)
        btnRegister = findViewById(R.id.btnRegister)

        db = AppDatabase.getDatabase(this) //initializing the database

        //admin user
        addDefaultUser()

        //register button
        btnRegister.setOnClickListener {
            registerUser()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun addDefaultUser() {
        lifecycleScope.launch {
            //check if the admin already exists
            val existingUser = db.userDao().getUserByEmail("admin")
            if (existingUser == null){
                //if not, insert the default admin user
                db.userDao().insertUser(
                    User(
                        firstName = "admin",
                        lastName = "admin",
                        email = "admin@mbb.com",
                        password = "admin"
                    )
                )
            }
        }
    }

    private fun registerUser() {
        val firstName = edtFirstName.text.toString()
        val lastName = edtLastName.text.toString()
        val email = edtEmailRegister.text.toString()
        val password = edtPasswordReg.text.toString()
        val confirmPassword = edtConfirmPasswordReg.text.toString()

        //validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        //database lifecycleScope
        lifecycleScope.launch {
            //check if the user already exists
            val existingUser = db.userDao().getUserByEmail(email)
            if (existingUser != null) {
                Toast.makeText(this@Register, "User already exists", Toast.LENGTH_SHORT).show()
                return@launch
            } else {
                //if new user, insert the new user
                val newUser = User(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password
                )
                db.userDao().insertUser(newUser)

                Toast.makeText(this@Register, "User registered successfully", Toast.LENGTH_SHORT)
                    .show()

                clearFields()
                openLoginScreen()
            }
        }
    }

    private fun clearFields(){
        edtFirstName.text.clear()
        edtLastName.text.clear()
        edtEmailRegister.text.clear()
        edtPasswordReg.text.clear()
        edtConfirmPasswordReg.text.clear()
    }

    private fun openLoginScreen() {
        val intent = android.content.Intent(this, Login::class.java)
        startActivity(intent)
        finish() // closes register screen
    }
}