package com.aripov.eyerestreminderapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.aripov.eyerestreminderapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener {

    companion object {
        const val CHANNEL_ID : String = "My Notifications"
        var NOTIFICATION_ID = 0
        const val REST_TITLE = "Time for your eyes to rest!"
        const val REST_DESCRIPTION = "Look at something that is 20 meters away from you." +
                                    " Try to focus on small details of that object."

        const val WORK_TITLE = "Work Time"
        const val WORK_DESCRIPTION = "Thank you for your hard work!"
    }

    private lateinit var binding: ActivityMainBinding
    private var workTimeLength = 20
    private var restLength = 60
    private var runTimers = false
    @RequiresApi(VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        while(VERSION.SDK_INT >= VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }
        binding.btnSubmit.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
        createNotificationChannel()
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.btnSubmit -> {
                val etWorkTime = binding.etWorkTime.text.toString().toInt()
                val etRestTime = binding.etRestTime.text.toString().toInt()
                if(etWorkTime != 0 && etRestTime != 0){
                    workTimeLength = etWorkTime
                    restLength = etRestTime
                } else {
                    Toast.makeText(
                        applicationContext,
                        "You need to provide time for both.",
                        Toast.LENGTH_SHORT).show()
                }
            }

            R.id.btnStart -> {
                if(!runTimers) {
                    runTimers = true
                    buildNotification()
                    binding.btnStart.text = getString(R.string.stop_timer)
                } else {
                    runTimers = false
                    binding.btnStart.text = getString(R.string.start_timer)
                }
            }
        }
    }
    private fun buildNotification(): NotificationCompat.Builder {
        var title = WORK_TITLE
        var bigText = WORK_DESCRIPTION

        val builder =  NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_remove_red_eye_24)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(bigText)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        var PROGRESS_MAX = workTimeLength*60000
        var PROGRESS_CURRENT : Int

        NotificationManagerCompat.from(this).apply {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                val timer = object: CountDownTimer(PROGRESS_MAX.toLong(), 0){
                    @SuppressLint("MissingPermission")
                    override fun onTick(millisUntilFinish: Long) {
                        if(!runTimers) {
                            return
                        }
                        if((millisUntilFinish%1000).toInt() == 0) {
                            PROGRESS_CURRENT = (PROGRESS_MAX-millisUntilFinish).toInt()
                            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
                            binding.tvTimeLeft.text =
                                getString(R.string.tv_main_time_left, title, millisUntilFinish)
                            notify(NOTIFICATION_ID++, builder.build())
                        }
                    }

                    override fun onFinish() {
                        if(title == WORK_TITLE) {
                            title = REST_TITLE
                            bigText = REST_DESCRIPTION
                            PROGRESS_MAX = restLength*1000
                        } else {
                            title = WORK_TITLE
                            bigText = WORK_DESCRIPTION
                            PROGRESS_MAX = workTimeLength*60000
                        }
                        PROGRESS_CURRENT = 0
                        if(runTimers) {
                           buildNotification()
                        }
                    }

                }
                timer.start()
            }
        }
        return builder
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager : NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}