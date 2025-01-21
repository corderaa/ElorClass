package com.example.elorclass

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.elorclass.data.UserSession
import com.example.elorclass.functionalities.AppDatabase
import com.example.elorclass.functionalities.Functionalities
import java.util.Locale

class MainPanelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_panel)
        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val functionalities = Functionalities()
        val buttonProfile : Button = findViewById(R.id.buttonProfile)
        val buttonLogout : Button = findViewById(R.id.buttonLogout)
        val buttonHorario : Button = findViewById(R.id.horario)

        buttonProfile.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){

                finish()
            } else {
                Toast.makeText(
                    this, getString(R.string.no_conected), Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonLogout.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                UserSession.clearSession()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this, getString(R.string.no_conected), Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonHorario.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                val intent = Intent(this,ScheduleActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this, getString(R.string.no_conected), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}