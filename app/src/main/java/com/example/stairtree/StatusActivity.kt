package com.example.stairtree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stairtree.databinding.ActivityStairBinding
import com.example.stairtree.databinding.ActivityStatusBinding

class StatusActivity : AppCompatActivity() {
    private val binding by lazy { ActivityStatusBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}