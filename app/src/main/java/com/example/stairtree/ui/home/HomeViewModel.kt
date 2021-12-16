package com.example.stairtree.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.stairtree.db.AppDatabase
import com.example.stairtree.db.daily.DataTuple
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.map

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val dailyDB = AppDatabase.create(application).daily()
    val selectSum = dailyDB.selectSum()
        .map { DataTuple(it.stair / 60000, it.elevator / 60000) }
        .asLiveData()
    private val firebaseDb = Firebase.firestore
    private val _selectWorldSum = MutableLiveData<DataTuple>()
    val selectWorldSum: LiveData<DataTuple> = _selectWorldSum

    init {
        firebaseDb.collection("global").document("global").addSnapshotListener { value, error ->
            if (error == null) {
                val stairSum = value!!.data!!["stair"].toString().toDouble()
                val elevatorSum = value.data!!["elevator"].toString().toDouble()
                _selectWorldSum.value = DataTuple(stairSum / 60000, elevatorSum / 60000)
            }
        }
    }
}