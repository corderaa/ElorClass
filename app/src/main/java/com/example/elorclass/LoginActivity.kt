package com.example.elorclass

import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.elorclass.functionalities.AppDatabase
import com.example.elorclass.functionalities.Functionalities
import com.example.elorclass.functionalities.RememberMeDB

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)

        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val functionalities = Functionalities()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "RememberMeDB"
        ).allowMainThreadQueries().build()

        val buttonConnexion: Button = findViewById(R.id.buttonLogin)
        val etUser: EditText = findViewById(R.id.editTextUser)
        val etPassword: EditText = findViewById(R.id.editTextPassword)
        val cbRememberMe: CheckBox = findViewById(R.id.checkBoxRememberMe)

        buttonConnexion.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                if (cbRememberMe.isChecked) {
                    val userName: String = etUser.text.toString()
                    val password: String = etPassword.text.toString()
                    val users: List<RememberMeDB> = db.rememberMeDao().getAll()
                    if (!users.any { it.user == userName }) {
                        val user = RememberMeDB(
                            0,
                            user = userName,
                            password = password
                        )
                        db.rememberMeDao().insertAll(user)
                        Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "No conectado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}