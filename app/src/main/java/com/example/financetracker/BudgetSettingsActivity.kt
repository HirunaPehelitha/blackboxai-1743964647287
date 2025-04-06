package com.example.financetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BudgetSettingsActivity : AppCompatActivity() {
    private lateinit var dataStore: DataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_settings)

        dataStore = DataStore(this)

        val etBudget: EditText = findViewById(R.id.et_budget)
        val btnSave: Button = findViewById(R.id.btn_save_budget)

        // Load existing budget if available
        val budget = dataStore.getBudget()
        etBudget.setText(budget?.monthlyLimit?.toString() ?: "")

        btnSave.setOnClickListener {
            val budgetAmount = etBudget.text.toString().toDoubleOrNull()
            if (budgetAmount != null && budgetAmount > 0) {
                val newBudget = Budget(budgetAmount, 0.0)
                dataStore.saveBudget(newBudget)
                Toast.makeText(this, "Budget saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid budget", Toast.LENGTH_SHORT).show()
            }
        }
    }
}