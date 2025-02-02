package com.example.elorclass

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.elorclass.data.ScheduleItem
import com.example.elorclass.data.ScheduleItemArrayAdapter
import com.example.elorclass.functionalities.Functionalities

class ScheduleActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.schedule)

        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val functionalities = Functionalities()

        val returnButton: Button = findViewById(R.id.returnBtn)
        val myList = findViewById<ListView>(R.id.listViewSchedule)
        val scheduleItem = mutableListOf<ScheduleItem>()


        returnButton.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)) {
                val intent = Intent(this, MainPanelActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.no_conected), Toast.LENGTH_SHORT).show()
            }

        }

        scheduleItem.add(ScheduleItem("Programacion", 2))
        scheduleItem.add(ScheduleItem("Desarrollo de interfaces", 4))
        scheduleItem.add(ScheduleItem("Empresa", 6))

        val scheduleAdapter = ScheduleItemArrayAdapter(this, R.layout.schedule_item, scheduleItem)
        myList.adapter = scheduleAdapter

    }
}