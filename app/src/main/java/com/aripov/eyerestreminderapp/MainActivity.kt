package com.aripov.eyerestreminderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aripov.eyerestreminderapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var workTimeLength = 20
    private var restLength = 0.5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}