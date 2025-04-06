package com.example.financetracker

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataStore: DataStore
    private lateinit var tvBudgetSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataStore = DataStore(this)
        recyclerView = findViewById(R.id.rv_transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tvBudgetSummary = findViewById(R.id.tv_budget_summary)

        transactionAdapter = TransactionAdapter(dataStore.getTransactions())
        recyclerView.adapter = transactionAdapter

        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.tv_budget_settings).setOnClickListener {
            val intent = Intent(this, BudgetSettingsActivity::class.java)
            startActivity(intent)
        }

        updateBudgetSummary()
    }

    override fun onResume() {
        super.onResume()
        transactionAdapter.updateTransactions(dataStore.getTransactions())
        updateBudgetSummary()
    }

    private fun updateBudgetSummary() {
        val budget = dataStore.getBudget()
        val currency = dataStore.getCurrency()
        val totalExpense = dataStore.getTransactions()
            .filter { it.category != "Income" }
            .sumOf { it.amount }

        budget?.let {
            val remaining = it.monthlyLimit - totalExpense
            tvBudgetSummary.text = 
                "Budget: $currency${"%.2f".format(remaining)} / $currency${"%.2f".format(it.monthlyLimit)}"
            
            if (remaining < 0) {
                tvBudgetSummary.setTextColor(getColor(R.color.expenseColor))
            } else if (remaining < it.monthlyLimit * 0.2) {
                tvBudgetSummary.setTextColor(getColor(R.color.incomeColor))
            } else {
                tvBudgetSummary.setTextColor(getColor(android.R.color.black))
            }
        } ?: run {
            tvBudgetSummary.text = "No budget set"
        }
    }
}
