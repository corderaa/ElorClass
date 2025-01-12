package com.example.elorclass

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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

        val users: List<RememberMeDB> = db.rememberMeDao().getAll()
        val buttonConnexion: Button = findViewById(R.id.buttonLogin)
        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        val buttonForgotten: Button = findViewById(R.id.buttonForgotten)
        val actvUser: AutoCompleteTextView = findViewById(R.id.autoCompleteTextViewUser)
        val etPassword: EditText = findViewById(R.id.editTextPassword)
        val cbRememberMe: CheckBox = findViewById(R.id.checkBoxRememberMe)

        if (users.isNotEmpty()){
            val usersNames = ArrayList<String>()
            val usersPasswords = ArrayList<String>()
            for (user in users){
                usersNames.add(user.userLogin)
                usersPasswords.add(user.password)
            }
            val adapterUser = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, usersNames)
            val rememberedUserLogin: String = usersNames[users.size - 1]
            val rememberPassword: String = usersPasswords[users.size - 1]
            actvUser.setAdapter(adapterUser)
            actvUser.threshold = 1

            actvUser.setText(rememberedUserLogin)
            etPassword.setText(rememberPassword)
            actvUser.setOnItemClickListener{ _, _, _, _ ->
                etPassword.setText(usersPasswords[usersNames.indexOf(actvUser.text.toString())])
            }

            cbRememberMe.isChecked = true
        }

        //FALTA AÑADIR LA AUTENTICACIÓN CONTRA LA BASE DE DATOS
        buttonConnexion.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                if (cbRememberMe.isChecked) {
                    val userName: String = actvUser.text.toString()
                    val password: String = etPassword.text.toString()
                    val user = RememberMeDB(
                        userLogin = userName,
                        password = password
                    )
                    val userToDelete = users.find { it.userLogin == userName }
                    if (userToDelete != null) {
                        db.rememberMeDao().delete(userToDelete)
                    } else {
                        Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    }
                    db.rememberMeDao().insertAll(user)
                }
                actvUser.text.clear()
                etPassword.text.clear()
            } else {
                Toast.makeText(this, "No conectado", Toast.LENGTH_SHORT).show()
            }
        }

        buttonForgotten.setOnClickListener{
            if (functionalities.checkConnection(connectivityManager)){

            }
        }

        buttonRegister.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}