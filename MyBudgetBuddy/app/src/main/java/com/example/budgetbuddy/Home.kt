package com.example.budgetbuddy

import Data.database.AppDatabase
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
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

    private lateinit var db: AppDatabase

    //nav bar buttons
    private lateinit var btnAccountButton: ImageButton
    private lateinit var btnAddExpenseHome: ImageButton
    private lateinit var btnEnvelope: ImageButton


    //month expenses
    private var selectedMonthKey: String = ""

    private var selectedMonthDisplay: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)


        //typecasting
        btnGenReport = findViewById(R.id.btnGenReport)
        btnMonthlyGoal = findViewById(R.id.btnMonthlyGoal)
        btnMonthFilter = findViewById(R.id.btnMonthFilter)
        txtMonthName = findViewById(R.id.txtMonthName)
        txtTotalExpensesAmount = findViewById(R.id.txtTotalExpensesAmount)
        btnAccountButton = findViewById(R.id.btnAccountButton)
        btnAddExpenseHome = findViewById(R.id.btnAddExpenseHome)
        btnEnvelope = findViewById(R.id.btnEnvelope)

        //initialize the date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val formattedMonth = String.format("%02d", month + 1)

        selectedMonthKey = "$year-$formattedMonth"
        selectedMonthDisplay = "${getMonthName(month)} $year"
        txtMonthName.text = selectedMonthDisplay


        //database
        db = AppDatabase.getDatabase(this)

        //button onclick listener
        btnGenReport.setOnClickListener {
            //go to report
            val intent = Intent(this, Report::class.java)
            startActivity(intent)
        }
        btnMonthlyGoal.setOnClickListener {
            //go to monthly goal
            val intent = Intent(this, MonthlyGoal::class.java)
            intent.putExtra("selectedMonth", selectedMonthKey)
            startActivity(intent)
        }

        btnMonthFilter.setOnClickListener {
            //filter by month
            showMonthPicker()
        }

        updateTotalSpending(selectedMonthKey)

        //nav bar buttons set on click listener
        btnAccountButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        btnAddExpenseHome.setOnClickListener {
            Toast.makeText(this, "you clicked Add Expense", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Expense::class.java)
            startActivity(intent)
        }

        btnEnvelope.setOnClickListener {
            val intent = Intent(this, ComingSoon::class.java)
            startActivity(intent)
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

    private fun updateTotalSpending(monthKey: String) {
        lifecycleScope.launch {
            val totalSpending = db.expenseDao().getTotalForMonth(monthKey) ?: 0.0
            withContext(Dispatchers.Main) {
                txtTotalExpensesAmount.text = "R%.2f".format(totalSpending)
            }
        }
    }
}