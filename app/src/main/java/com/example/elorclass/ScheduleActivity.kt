package com.example.elorclass

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.elorclass.data.ScheduleItem
import com.example.elorclass.data.ScheduleItemArrayAdapter
import com.example.elorclass.data.User
import com.example.elorclass.data.UserSession
import com.example.elorclass.functionalities.Functionalities
import com.example.elorclass.socketIO.SocketClient
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class ScheduleActivity : BaseActivity() {
    private var socketClient: SocketClient? = null
    private var myList: ListView? = null
    private var currentSelectedDate: String? = null
    private var scheduleItems: MutableList<ScheduleItem> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.schedule)

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val functionalities = Functionalities()
        val calendarView: CalendarView = findViewById(R.id.calendarView)
        val returnButton: Button = findViewById(R.id.returnBtn)
        myList = findViewById<ListView>(R.id.listViewSchedule)


        socketClient = SocketClient(null, null,null, this)
        socketClient!!.connect()




        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val message = JSONObject().apply {
                put("dni", UserSession.fetchUser()?.dni)
            }.toString()

            socketClient?.emit("onRequestSchedulle", message)
            val month = month + 1
            val scheduleDate = "$dayOfMonth/$month/$year"
            currentSelectedDate = scheduleDate

            Toast.makeText(this, "Schedule for $scheduleDate", Toast.LENGTH_SHORT).show()
        }

        returnButton.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)) {
                val intent = Intent(this, MainPanelActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.no_conected), Toast.LENGTH_SHORT).show()
            }
        }

    }


    fun takeSchedule(schedules: JSONArray): MutableList<ScheduleItem> {
        scheduleItems.clear()
        var ret: MutableList<ScheduleItem> = mutableListOf<ScheduleItem>();
        for (i in 0 until schedules.length()) {
            var schedule = JSONObject(schedules.get(i).toString())
            var hour = schedule.get("hour")
            val dayString = schedule.getString("day")
            val sdfInput = SimpleDateFormat("MMM d, yyyy")
            val day = sdfInput.parse(dayString)
            val sdf = SimpleDateFormat("d/M/yyyy")
            val formattedDate = sdf.format(day)

            var subject = schedule.get("subjects") as JSONObject
            var subjectName = subject.get("name")



            if (currentSelectedDate == formattedDate) {
                ret.add(ScheduleItem(hour.toString(), subjectName.toString()))
            } else {
                Toast.makeText(this, "No hay horarios disponibles", Toast.LENGTH_SHORT).show()
            }

            //ret.add(ScheduleItem(hour.toString(), subjectName.toString()))


            Log.d("d", schedule.toString())
        }

        return ret;
    }

    fun updateSchedule(schedules: JSONArray) {
        scheduleItems.clear()
        scheduleItems = takeSchedule(schedules)
        if (!scheduleItems.isEmpty()) {
            val scheduleAdapter =
                ScheduleItemArrayAdapter(this, R.layout.schedule_item, scheduleItems)
            myList?.adapter = scheduleAdapter
            (scheduleAdapter).notifyDataSetChanged()
        } else {
            Toast.makeText(this, "No hay horarios disponibles", Toast.LENGTH_SHORT).show()
        }
    }

    fun schedulleFailed() {
        Toast.makeText(this, "Error al cargar", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        if (socketClient != null && socketClient!!.isConnected()) {
            socketClient?.disconnect()
        }
    }

    override fun onStop() {
        super.onStop()
        if (socketClient != null && socketClient!!.isConnected()) {
            socketClient?.disconnect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (socketClient != null && socketClient!!.isConnected()) {
            socketClient?.disconnect()
        }
    }

}
