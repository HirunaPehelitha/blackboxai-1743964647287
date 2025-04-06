package com.example.financetracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        dataStore = DataStore(this)
        setupCategorySpinner()

        val btnSave: Button = findViewById(R.id.btn_save)
        btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun setupCategorySpinner() {
        val spinner: Spinner = findViewById(R.id.spinner_category)
        ArrayAdapter.createFromResource(
            this,
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun saveTransaction() {
        val title = findViewById<EditText>(R.id.et_title).text.toString()
        val amountText = findViewById<EditText>(R.id.et_amount).text.toString()
        val category = findViewById<Spinner>(R.id.spinner_category).selectedItem.toString()
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        if (title.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = try {
            amountText.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val transactions = dataStore.getTransactions().toMutableList()
        val newId = if (transactions.isEmpty()) 1 else transactions.maxOf { it.id } + 1
        val newTransaction = Transaction(newId, title, amount, category, date)

        transactions.add(newTransaction)
        dataStore.saveTransactions(transactions)
        dataStore.updateBudgetWithTransaction(
            amount = newTransaction.amount,
            isIncome = newTransaction.category == "Income"
        )

        Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
        finish()
    }
}