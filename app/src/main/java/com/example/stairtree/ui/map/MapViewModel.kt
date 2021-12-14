package com.example.stairtree.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapViewModel : ViewModel() {
    private val firebaseDb = Firebase.firestore
    private val _stair = MutableLiveData(0.0)
    val stair: LiveData<Double> = _stair
    private val _elevator = MutableLiveData(0.0)
    val elevator: LiveData<Double> = _elevator
    init {
        firebaseDb.collection("data").get().addOnSuccessListener {
            val stairSum = it
                .map { stairIt -> stairIt["stair"].toString().toDouble() }
                .reduce { acc, d -> acc + d }
            val elevatorSum = it
                .map { elevatorIt -> elevatorIt["elevator"].toString().toDouble() }
                .reduce { acc, d -> acc + d }
            Log.i("sample222", stairSum.toString())
            _stair.value = stairSum
            _elevator.value = elevatorSum
        }
    }
}