package com.example.budgetbuddy

import Data.database.AppDatabase
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormatSymbols
import java.util.Calendar

class Home : AppCompatActivity() {
    //global declarations
    private lateinit var txtMonthName : TextView
    private lateinit var btnGenReport : Button
    private lateinit var btnMonthlyGoal : Button
    private lateinit var btnMonthFilter : ImageButton
    private lateinit var txtTotalExpensesAmount : TextView

    private lateinit var bottomNav: BottomNavigationView

    private lateinit var db: AppDatabase

    //month expenses
    private var selectedMonthKey: String = ""

    private var selectedMonthDisplay: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

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
        btnGenReport = findViewById(R.id.btnGenReport)
        btnMonthlyGoal = findViewById(R.id.btnMonthlyGoal)
        btnMonthFilter = findViewById(R.id.btnMonthFilter)
        txtMonthName = findViewById(R.id.txtMonthName)
        txtTotalExpensesAmount = findViewById(R.id.txtTotalExpensesAmount)

        //database
        db = AppDatabase.getDatabase(this)

        //button onclick listener
        btnGenReport.setOnClickListener {
            //go to report
            val intent = Intent(this, Report::class.java)
            startActivity(intent)
        }
        btnMonthlyGoal.setOnClickListener {
            //go to monthly goel
            val intent = Intent(this, MonthlyGoal::class.java)
            intent.putExtra("selectedMonth", selectedMonthKey)
            startActivity(intent)
        }

        btnMonthFilter.setOnClickListener {
            //filter by month
            showMonthPicker()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showMonthPicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, _ ->
            val formattedMonth = String.format("%02d", m + 1)
            selectedMonthKey = "$y-$formattedMonth"          // used for DB query
            selectedMonthDisplay = "${getMonthName(m)} $y"   // user-friendly
            txtMonthName.text = selectedMonthDisplay

            updateTotalSpending(selectedMonthKey)
        }, year, month, 1)

        datePicker.show()
    }
    private fun getMonthName(month: Int): String {
        return DateFormatSymbols().months[month]
    }

    private fun updateTotalSpending(monthKey: String){
        lifecycleScope.launch {
            val totalSpending = db.expenseDao().getTotalForMonth(monthKey) ?: 0.0
            withContext(Dispatchers.Main) {
                txtTotalExpensesAmount.text = "R%.2f".format(totalSpending)
            }
        }
    }
}