package com.itu.simplealarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timePicker: TimePicker = findViewById(R.id.timePicker)
        val setAlarmButton: Button = findViewById(R.id.setAlarmButton)

        // Check and request permission for exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Grant exact alarm permission in settings.", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        setAlarmButton.setOnClickListener {
            val hour: Int
            val minute: Int

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hour = timePicker.hour
                minute = timePicker.minute
            } else {
                hour = timePicker.currentHour
                minute = timePicker.currentMinute
            }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            val intent = Intent(this@MainActivity, AlarmReceiver::class.java) // FIX: Explicitly specify the class and context
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            try {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
                Toast.makeText(this, "Alarm set for $hour:$minute", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Toast.makeText(this, "Exact alarm permission required!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
