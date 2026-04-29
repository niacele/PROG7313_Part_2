package com.example.budgetbuddy

import Data.database.AppDatabase
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar

class Expense : AppCompatActivity() {
    //global declarations
    private lateinit var edtCategory: EditText
    private lateinit var edtAmount: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtDate: EditText
    private lateinit var btnAddImage: ImageButton
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton

    private var selectedPhotoUri: String? = null

    //nav bar buttons
    private lateinit var btnAccountButton: ImageButton
    private lateinit var btnAddExpense: ImageButton
    private lateinit var btnEnvelope: ImageButton

    //database
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense)

        //typecasting
        edtCategory = findViewById(R.id.edtCategory)
        edtAmount = findViewById(R.id.edtAmount)
        edtDescription = findViewById(R.id.edtDescription)
        edtDate = findViewById(R.id.edtDate)
        btnAddImage = findViewById(R.id.btnAddImage)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBackButton)
        btnAccountButton = findViewById(R.id.btnAccountButton)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnEnvelope = findViewById(R.id.btnEnvelope)

        db = AppDatabase.getDatabase(this)


        //button click listeners
        btnAddImage.setOnClickListener {
            Toast.makeText(this, "Add a photo", Toast.LENGTH_SHORT).show()

            //open a photo picker
            pickImageLauncher.launch("image/*")
        }

        btnSave.setOnClickListener {
            saveExpense()
        }

        //date picker
        edtDate.setOnClickListener {
            showDatePicker()
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        //nav bar buttons set on click listener
        btnAccountButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }

        btnAddExpense.setOnClickListener {
            //handle button click
            Toast.makeText(this, "Open the expense screen",
                Toast.LENGTH_SHORT).show()
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


    private fun saveExpense(){
        //get the text from the input fields and remove extra spaces
        val category = edtCategory.text.toString().trim()
        val amountTxt = edtAmount.text.toString().trim()
        val date = edtDate.text.toString().trim() //changed from .string to .text.tostring
        val description = edtDescription.text.toString().trim()


        //validation
        if (category.isEmpty() || amountTxt.isEmpty() || date.isEmpty()) {
            Toast.makeText(
                this, "Please fill in all fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val amount = amountTxt.toDoubleOrNull()

        if (amount == null) {
            Toast.makeText(
                this, "Please enter a valid amount",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val expense = Data.Expense(
            category = category,
            amount = amount,
            date = date,
            description = description,
            photoUri = selectedPhotoUri
        )

        lifecycleScope.launch {
            db.expenseDao().insertExpense(expense)

            runOnUiThread {
                Toast.makeText(
                    this@Expense, "Expense saved successfully",
                    Toast.LENGTH_SHORT
                ).show()

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

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPhotoUri = uri.toString()
            Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun clearFields(){
        edtCategory.text.clear()
        edtAmount.text.clear()
        edtDescription.text.clear()
        edtDate.text.clear()
        selectedPhotoUri = null
    }
}