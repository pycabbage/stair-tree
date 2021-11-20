package com.example.stairtree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stairtree.databinding.ActivityStairBinding

class StairActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStairBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}