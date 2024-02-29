package com.aripov.eyerestreminderapp

import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.aripov.eyerestreminderapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener {

    companion object {
        const val CHANNEL_ID : String = "My Notifications"
    }

    private lateinit var binding: ActivityMainBinding
    private var workTimeLength = 20
    private var restLength = 0.5
    @RequiresApi(VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        while(VERSION.SDK_INT >= VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
        binding.btnSubmit.setOnClickListener(this)
    }

    fun buildNotification() {
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_remove_red_eye_24)
            .setContentTitle("Time for eyes to rest!")
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText("Look at something that is 20 meters away from you." +
                            "Try to focus on small details of that object for ${restLength*60} seconds"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnSubmit -> {
                val etWorkTime = binding.etWorkTime.toString().toInt()
                val etRestTime = (binding.etRestTime.toString().toInt()/60).toDouble()
                if(etWorkTime != 0 && etRestTime != 0.0){
                    workTimeLength = etWorkTime
                    restLength = etRestTime
                } else {
                    Toast.makeText(applicationContext, "You need to provide time for both.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}