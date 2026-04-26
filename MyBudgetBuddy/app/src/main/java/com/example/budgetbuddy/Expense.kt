package com.example.budgetbuddy

import Data.database.AppDatabase
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class Expense : AppCompatActivity() {
    //global declarations
    private lateinit var edtAmount: EditText
    private lateinit var edtDate: EditText
    private lateinit var btnAddImage: ImageButton
    private lateinit var btnSave: Button

    //database
    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense)

        //typecasting
        edtAmount = findViewById(R.id.edtAmount)
        edtDate = findViewById(R.id.edtDate)
        btnAddImage = findViewById(R.id.btnAddImage)
        btnSave = findViewById(R.id.btnSave)

        db = AppDatabase.getDatabase(this)

        //button click listeners
        btnAddImage.setOnClickListener {
            Toast.makeText(this, "Add a photo", Toast.LENGTH_SHORT).show()

            //open a photo picker
            photoPicker()
        }

        btnSave.setOnClickListener {
            saveExpense()
        }

        //date picker
        edtDate.setOnClickListener {
            showDatePicker()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun saveExpense(){
        //get the text from the input fields and remove extra spaces
        val amountTxt = edtAmount.text.toString().trim()
        val date = edtDate.toString().trim()

        //validation
        if(amountTxt.isEmpty() || date.isEmpty()){
            Toast.makeText(this, "Please fill in all fields",
                Toast.LENGTH_SHORT).show()
            return
        }
        val amount = amountTxt.toDoubleOrNull()

        if(amount == null) {
            Toast.makeText(this, "Please enter a valid amount",
                Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Data.Expense(
            amount = amount,
            date = date
            //photoUri = selectedImageUri
        )

        lifecycleScope.launch {
            db.expenseDao().insertExpense(expense)

            runOnUiThread {
                Toast.makeText(this@Expense, "Expense saved successfully",
                    Toast.LENGTH_SHORT).show()

                clearFields()
            }
        }
    }
    private fun showDatePicker(){
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
                edtDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun photoPicker(){
        //create an intent to open the photo picker
        //val intent = Intent(Intent.ACTION_PICK)
        //intent.type = "image/*"
        //photoPickerLauncher.launch(intent)
    }
    private fun clearFields(){
        edtAmount.text.clear()
        edtDate.text.clear()
        //selectedImageUri = null
    }
}