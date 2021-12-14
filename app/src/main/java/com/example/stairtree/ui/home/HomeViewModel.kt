package com.example.stairtree.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.stairtree.db.AppDatabase

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val dailyDB = AppDatabase.create(application).daily()
    val selectElevatorSum2 = dailyDB.selectSum()
}