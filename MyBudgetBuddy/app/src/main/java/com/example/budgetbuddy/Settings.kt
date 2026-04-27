package com.example.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Settings : AppCompatActivity() {

    //global declaration
    private lateinit var btnBackButton : ImageButton
    private lateinit var btnAccountInfo : Button
    private lateinit var btnCurrency : Button
    private lateinit var btnAchievements : Button
    private lateinit var btnLogout : Button
    private lateinit var btnManageSubscription : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        //typecasting
        btnBackButton = findViewById(R.id.btnBackButton)
        btnAccountInfo = findViewById(R.id.btnAccountInfo)
        btnCurrency = findViewById(R.id.btnCurrency)
        btnAchievements = findViewById(R.id.btnAchievements)
        btnLogout = findViewById(R.id.btnLogout)
        btnManageSubscription = findViewById(R.id.btnManageSubscription)

        //button onclick listener
        btnBackButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        btnAccountInfo.setOnClickListener {
            val intent = Intent(this, AccountInfo::class.java)
            startActivity(intent)
        }

        btnCurrency.setOnClickListener {
            val intent = Intent(this, ComingSoon::class.java)
            startActivity(intent)
        }

        btnAchievements.setOnClickListener {
            val intent = Intent(this, ComingSoon::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            Toast.makeText(this, "Logging you out!",
                Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}