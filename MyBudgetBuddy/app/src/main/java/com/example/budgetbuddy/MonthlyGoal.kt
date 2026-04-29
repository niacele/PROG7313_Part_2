package com.example.budgetbuddy

import Data.MonthlyGoal
import Data.database.AppDatabase
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MonthlyGoal : AppCompatActivity() {

    //global declarations
    private lateinit var txtMonthOutput : TextView
    private lateinit var edtMinGoal : EditText
    private lateinit var edtMaxGoal : EditText
    private lateinit var btnSaveMonthlyGoal : Button
    private lateinit var btnBackToHome : Button

    private lateinit var db: AppDatabase

    //nav bar buttons
    private lateinit var btnAccountButton: ImageButton
    private lateinit var btnAddExpense: ImageButton
    private lateinit var btnEnvelope: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_monthly_goal)

        //passed month
        val selectedMonthKey = intent.getStringExtra("selectedMonth")

        //typecasting
        txtMonthOutput = findViewById(R.id.txtMonthOutput)
        edtMinGoal = findViewById(R.id.edtMinGoal)
        edtMaxGoal = findViewById(R.id.edtMaxGoal)
        btnSaveMonthlyGoal = findViewById(R.id.btnSaveMonthlyGoal)
        btnBackToHome = findViewById(R.id.btnBackToHome)
        btnAccountButton = findViewById(R.id.btnAccountButton)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnEnvelope = findViewById(R.id.btnEnvelope)


        //database
        db = AppDatabase.getDatabase(this)

        //button onclick listener
        btnSaveMonthlyGoal.setOnClickListener {
            saveGoals()
        }

        //back to home
        btnBackToHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }


        // show month in the TextView
        txtMonthOutput.text = selectedMonthKey ?: "No month selected"


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

    private fun saveGoals(){
        val month = txtMonthOutput.text.toString()
        val minGoalTxt = edtMinGoal.text.toString().trim()
        val maxGoalTxt = edtMaxGoal.text.toString().trim()

        if(minGoalTxt.isEmpty() || maxGoalTxt.isEmpty()){
            Toast.makeText(this, "Please enter both goals", Toast.LENGTH_SHORT).show()
            return
        }

        val minGoal = minGoalTxt.toDoubleOrNull()
        val maxGoal = maxGoalTxt.toDoubleOrNull()

        if(minGoal == null || maxGoal == null){
            Toast.makeText(this, "Please enter valid numbers for goals", Toast.LENGTH_SHORT).show()
            return
        }

        if(minGoal  > maxGoal){
            Toast.makeText(this, "Minimum goal cannot be greater than maximum goal", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingGoal = db.monthlyGoalDao().getGoalForMonth(month)

            if(existingGoal == null) {
                val newGoal = MonthlyGoal(
                    month = month,
                    minGoal = minGoal,
                    maxGoal = maxGoal
                )
                db.monthlyGoalDao().insertGoal(newGoal)
            } else {
                val updatedGoal = existingGoal.copy(
                    minGoal = minGoal,
                    maxGoal = maxGoal
                )
                db.monthlyGoalDao().updateGoal(updatedGoal)
            }

            runOnUiThread {
                Toast.makeText(this@MonthlyGoal, "Goals saved successfully",
                    Toast.LENGTH_SHORT).show()
                clearFields()
            }
        }
    }
    private fun clearFields(){
        edtMinGoal.text.clear()
        edtMaxGoal.text.clear()
    }
}