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

    private var selectedMonth: String = ""

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

        //database
        db = AppDatabase.getDatabase(this)

        //button onclick listener
        btnGenReport.setOnClickListener {
            val intent = Intent(this, Report::class.java)
            intent.putExtra("selectedMonth", selectedMonth)
            startActivity(intent)
        }
        btnMonthlyGoal.setOnClickListener {
            val intent = Intent(this, MonthlyGoal::class.java)
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
        // Example: use a DatePickerDialog to select month/year
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, _ ->
            selectedMonth = "${getMonthName(m)} $y"
            txtMonthName.text = selectedMonth
        //    updateTotalSpending(y, m)
        }, year, month, 1)

        // Hide day selection (optional: only month/year)
        //datePicker.datePicker.findViewById<View>(
        //    resources.getIdentifier("day", "id", "android")
        //)?.visibility = View.GONE

        datePicker.show()
    }
    private fun getMonthName(month: Int): String {
        return DateFormatSymbols().months[month]
    }
    //private fun updateTotalSpending(y, m){
    //    lifecycleScope.launch {
    //        val totalSpending = db.expenseDao().GetExpensesForMonth(selectedMonth)
    //        txtTotalExpensesAmount.text = "R%.2f".format(totalSpending)
    //    }
    //}
}