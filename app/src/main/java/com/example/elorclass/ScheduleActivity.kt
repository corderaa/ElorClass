package com.example.elorclass

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.elorclass.data.ScheduleItem
import com.example.elorclass.data.ScheduleItemArrayAdapter

class ScheduleActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.schedule)

        val returnButton: Button = findViewById(R.id.returnBtn)
        val myList = findViewById<ListView>(R.id.listViewSchedule)
        val scheduleItem = mutableListOf<ScheduleItem>()

        returnButton.setOnClickListener {
            val intent = Intent(this, MainPanelActivity::class.java)
            startActivity(intent)
        }
            scheduleItem.add(ScheduleItem("Programacion", 2))
            scheduleItem.add(ScheduleItem("Desarrollo de interfaces", 4))
            scheduleItem.add(ScheduleItem("Empresa", 6))

        val scheduleAdapter = ScheduleItemArrayAdapter(this, R.layout.schedule_item, scheduleItem)
        myList.adapter = scheduleAdapter

        }
}