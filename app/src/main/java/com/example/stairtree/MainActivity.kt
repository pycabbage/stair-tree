package com.example.stairtree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.stairtree.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.textView.text = "change"
//        CoroutineScope(Dispatchers.Default).launch {
//            val db = AppDatabase.create(applicationContext)
//            val sample = db.sample()
//            sample.deleteAll()
//            repeat(10) {
//                sample.insert(SampleEntity(it, "b$it"))
//            }
//            sample.selectAll().forEach {
//                Log.i("sample", it.name)
//            }
//        }
    }
}