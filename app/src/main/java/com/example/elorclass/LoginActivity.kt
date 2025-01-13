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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.elorclass.data.User
import com.example.elorclass.data.UserSession
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
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
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
        buttonLogin.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                val userId: String = actvUser.text.toString()
                //PEDIR USUARIO
                val usuarioDePrueba = User(
                    name = "nahikari",
                    surname = "surname",
                    id = "id",
                    adress = "adress",
                    firstTelephone = "firstTelephone",
                    secondTelephone = "secondTelephone",
                    studies = "studies",
                    schoolyear = 2,
                    dual = true,
                    password = "password",
                    registered = true)
                UserSession.setUserSession(usuarioDePrueba.name, usuarioDePrueba.surname, usuarioDePrueba.id,
                    usuarioDePrueba.adress, usuarioDePrueba.firstTelephone, usuarioDePrueba.secondTelephone,
                    usuarioDePrueba.studies, usuarioDePrueba.password, usuarioDePrueba.schoolyear,
                    usuarioDePrueba.dual, usuarioDePrueba.registered)
                if(userId == usuarioDePrueba.id) {
                    val password: String = etPassword.text.toString()
                    if (password == usuarioDePrueba.password) {
                        if (cbRememberMe.isChecked) {
                            val user = RememberMeDB(
                                userLogin = userId,
                                password = password
                            )
                            val userToDelete = users.find { it.userLogin == userId }
                            if (userToDelete != null) {
                                db.rememberMeDao().delete(userToDelete)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Usuario recordado",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            db.rememberMeDao().insertAll(user)
                        }
                        if (usuarioDePrueba.registered) {
                            val intent = Intent(this, MainPanelActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            createDialog("Registro", "Debe registrarse", false)
                        }
                    } else {
                        createDialog("Error", "Contraseña incorrecta", true)
                    }
                } else {
                    createDialog("Error", "El usuario no existe", true)
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

    private fun createDialog(title: String, message: String, registered: Boolean) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, which ->
                if (!registered) {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
}