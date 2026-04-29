package com.example.budgetbuddy

import Data.database.AppDatabase
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class Report : AppCompatActivity() {

    //global declarations
    private lateinit var btnBackButton: ImageButton
    private lateinit var txtExpenseReport: TextView
    private lateinit var resultsContainer: LinearLayout
    private lateinit var edtStartDate: EditText
    private lateinit var edtEndDate: EditText
    private lateinit var txtTotal: TextView
    private lateinit var btnViewReport: Button

    private lateinit var db: AppDatabase

    //nav bar buttons
    private lateinit var btnAccountButton: ImageButton
    private lateinit var btnAddExpense: ImageButton
    private lateinit var btnEnvelope: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)

        //typecasting
        btnBackButton = findViewById(R.id.btnBackButton)
        txtExpenseReport = findViewById(R.id.txtExpenseReport)
        edtStartDate = findViewById(R.id.edtStartDate)
        edtEndDate = findViewById(R.id.edtEndDate)
        txtTotal = findViewById(R.id.txtTotal)
        btnViewReport = findViewById(R.id.btnViewReport)
        btnAccountButton = findViewById(R.id.btnAccountButton)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnEnvelope = findViewById(R.id.btnEnvelope)
        resultsContainer = findViewById(R.id.resultsContainer)

        //database
        db = AppDatabase.getDatabase(this)

        //date picker
        edtStartDate.setOnClickListener {
            showStartDatePicker()
        }

        edtEndDate.setOnClickListener {
            showEndDatePicker()
        }


        //button click listeners
        btnBackButton.setOnClickListener {
            val intent = Intent(this, Expense::class.java)
            startActivity(intent)
        }

        btnViewReport.setOnClickListener {
            viewReport()
        }

        //nav bar buttons set on click listener
        btnAccountButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        btnAddExpense.setOnClickListener {
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

    private fun showStartDatePicker(){
        //implement date picker
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)

                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                edtStartDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showEndDatePicker(){
        //implement date picker
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)

                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                edtStartDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun viewReport(){
        val startDate = edtStartDate.text.toString().trim()
        val endDate = edtEndDate.text.toString().trim()

        //validation
        if(startDate.isEmpty() || endDate.isEmpty()){
            //Toast
            Toast.makeText(this, "Please enter both a start and end date",
                Toast.LENGTH_SHORT).show()
            return
        }

        //Database operation
        lifecycleScope.launch {
            val filteredExpenses = db.expenseDao().getExpensesBetweenDates(startDate, endDate)

            runOnUiThread {
                resultsContainer.removeAllViews()

                if (filteredExpenses.isEmpty()) {
                    txtTotal.text = "Total: R0.00"

                    val noResultsText = TextView(this@Report)
                    noResultsText.text = "No expenses found for the selected period"
                    noResultsText.textSize = 16f

                    resultsContainer.addView(noResultsText)
                    return@runOnUiThread
                }
                var totalAmount = 0.0

                for (expense in filteredExpenses) {

                    val expenseText = TextView(this@Report)
                    expenseText.text =
                        "Category: ${expense.category}\n" +
                                "Amount: R${expense.amount}\n" +
                                "Date: ${expense.date}\n" +
                                "Description: ${expense.description}"+
                                "__________________________________"

                    // Styling
                    expenseText.textSize = 16f
                    expenseText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

                    // Add expense details to screen
                    resultsContainer.addView(expenseText)

                    // If an image was saved with this expense
                    if (expense.photoUri != null) {
                        val imageView = ImageView(this@Report)

                        // Set layout parameters
                        imageView.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        // Maintain image proportions
                        imageView.adjustViewBounds = true
                        // Prevent very large images
                        imageView.maxHeight = 800

                        try {
                            // Load saved image from URI
                            imageView.setImageURI(Uri.parse(expense.photoUri))
                            // Display image neatly
                            imageView.scaleType = ImageView.ScaleType.FIT_CENTER

                            // Add image below expense details
                            resultsContainer.addView(imageView)

                        } catch (e: Exception) {
                            // If image fails to load
                            val imageErrorText = TextView(this@Report)
                            imageErrorText.text = "Image could not be loaded"
                            imageErrorText.textSize = 14f
                            resultsContainer.addView(imageErrorText)
                        }
                    }
                }
                txtTotal.text = "Total: R%.2f".format(totalAmount)
            }
        }
    }
}