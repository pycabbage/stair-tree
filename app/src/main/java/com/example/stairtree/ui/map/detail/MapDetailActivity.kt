package com.example.stairtree.ui.map.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.stairtree.databinding.ActivityMapDetailBinding

class MapDetailActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMapDetailBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = intent.getStringExtra("title")
        binding.textView.text = intent.getStringExtra("message")
    }
}