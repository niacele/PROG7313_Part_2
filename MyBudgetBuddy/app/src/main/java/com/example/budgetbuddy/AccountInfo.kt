package com.example.budgetbuddy

import Data.database.AppDatabase
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountInfo : AppCompatActivity() {

    //global declarations
    private lateinit var btnBackButton: ImageButton
    private lateinit var txtUserName: TextView
    private lateinit var txtUserEmail: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtAchievements: TextView

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_info)

        //typecasting
        btnBackButton = findViewById(R.id.btnBackButton)
        txtUserName = findViewById(R.id.txtUserName)
        txtUserEmail = findViewById(R.id.txtUserEmail)
        txtDate = findViewById(R.id.txtDate)
        txtAchievements = findViewById(R.id.txtAchievements)

        //database
        db = AppDatabase.getDatabase(this)

        val userId = intent.getIntExtra("userId", -1)

        if (userId != -1) {
            lifecycleScope.launch {
                val user = db.userDao().getUserById(userId)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        txtUserName.text = "${user.firstName} ${user.lastName}"
                        txtUserEmail.text = user.email
                        txtDate.text = user.joinDate
                        txtAchievements.text = "Coming Soon!"
                    } else {
                        txtUserName.text = "Name will appear here"
                        txtUserEmail.text = "Email will appear here"
                        txtDate.text = "Date will appear here"
                        txtAchievements.text = "Coming Soon!"
                    }
                }
            }
        }

        //button onclick listener
        btnBackButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}