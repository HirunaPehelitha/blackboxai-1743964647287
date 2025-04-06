package com.example.financetracker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class DataStore(context: Context) {
    private val sharedPref: SharedPreferences = 
        context.getSharedPreferences("FinancePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Transaction operations
    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPref.edit().putString("transactions", json).apply()
    }

    fun getTransactions(): List<Transaction> {
        val json = sharedPref.getString("transactions", null)
        val type: Type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // Budget operations
    fun saveBudget(budget: Budget) {
        val json = gson.toJson(budget)
        sharedPref.edit().putString("budget", json).apply()
    }

    fun getBudget(): Budget? {
        val json = sharedPref.getString("budget", null)
        return gson.fromJson(json, Budget::class.java)
    }

    // Currency preference
    fun saveCurrency(currency: String) {
        sharedPref.edit().putString("currency", currency).apply()
    }

    fun getCurrency(): String {
        return sharedPref.getString("currency", "$") ?: "$"
    }

    fun updateBudgetWithTransaction(amount: Double, isIncome: Boolean) {
        val budget = getBudget() ?: return
        val newSpent = if (isIncome) {
            budget.currentSpent - amount
        } else {
            budget.currentSpent + amount
        }
        saveBudget(Budget(budget.monthlyLimit, newSpent))
    }
}