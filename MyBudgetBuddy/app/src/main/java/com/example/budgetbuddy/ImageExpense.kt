package com.example.budgetbuddy

import Data.dao.ExpenseDao
import Data.database.AppDatabase
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageExpense : AppCompatActivity() {
    //global declarations
    private lateinit var imgExpensePhoto: ImageView
    private lateinit var btnBackToHome: Button

    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image_expense)

        //bottom navigation
        bottomNav = findViewById(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, Settings::class.java))
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, Expense::class.java))
                    true
                }
                R.id.nav_envelope -> {
                    startActivity(Intent(this, ComingSoon::class.java))
                    true
                }
                else -> false
            }
        }

        //typecasting
        imgExpensePhoto = findViewById(R.id.imgExpensePhoto)
        btnBackToHome = findViewById(R.id.btnBackToHome)

        //database
        db = AppDatabase.getDatabase(this)

        //expense link
        val expenseId = intent.getIntExtra("expense_id", -1)

        if (expenseId != -1) {
            lifecycleScope.launch {
                val expense = expenseDao.GetExpenseById(expenseId)
                withContext(Dispatchers.Main) {
                    expense.photoUri?.let { uriString ->
                        imgExpensePhoto.setImageURI(uriString.toUri())
                    }
                }
            }
        }

        //button click listeners
        btnBackToHome.setOnClickListener {
            val intent = Intent(this, Expense::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}