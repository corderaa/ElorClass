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
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.example.elorclass.data.User
import com.example.elorclass.data.UserSession
import com.example.elorclass.functionalities.AppDatabase
import com.example.elorclass.functionalities.Functionalities
import com.example.elorclass.functionalities.RememberMeDB
import com.example.elorclass.functionalities.SendEmailTask
import java.util.Locale
import jakarta.mail.*
import jakarta.mail.internet.*
import java.util.Properties

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)

        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val functionalities = Functionalities()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "AppDatabase"
        ).allowMainThreadQueries().build()

        val users: List<RememberMeDB> = db.rememberMeDao().getAll()
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
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
                val userId = actvUser.text.toString()
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
                    registered = true,
                    role = 3)
                UserSession.setUserSession(usuarioDePrueba.name!!, usuarioDePrueba.surname!!, usuarioDePrueba.id!!,
                    usuarioDePrueba.adress!!, usuarioDePrueba.firstTelephone!!, usuarioDePrueba.secondTelephone!!,
                    usuarioDePrueba.studies!!, usuarioDePrueba.password!!, usuarioDePrueba.schoolyear!!,
                    usuarioDePrueba.dual!!, usuarioDePrueba.registered!!, usuarioDePrueba.role!!)
                if(userId.equals(usuarioDePrueba.id,true)) {
                    val password: String = etPassword.text.toString()

                    //UGAITZ
                    val userPruebaLogin = User(id = actvUser.text.toString(), password = etPassword.text.toString())




                    if (password == usuarioDePrueba.password) {
                        if (cbRememberMe.isChecked) {
                            val user = RememberMeDB(
                                userLogin = userId,
                                password = password,
                            )
                            val userToDelete = users.find { it.userLogin == userId }
                            if (userToDelete != null) {
                                db.rememberMeDao().delete(userToDelete)
                            } else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.remembered_user),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                            db.rememberMeDao().insertAll(user)
                        }
                        if (usuarioDePrueba.registered) {
                            val dbPreferences = Room.databaseBuilder(
                                applicationContext,
                                AppDatabase::class.java, "AppDatabase"
                            ).allowMainThreadQueries().build()
                            val userPreference = dbPreferences.preferencesDao().getPreferenceByLogin(UserSession.fetchId().toString())
                            if (userPreference != null) {
                                val language = userPreference.language
                                val theme = userPreference.theme
                                if (language != null && language != Locale.getDefault().language)
                                    setLocale(language)
                                if (theme != null)
                                    setAppTheme(theme)
                            }
                            val intent = Intent(this, MainPanelActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            createDialog(getString(R.string.register), getString(R.string.register_needed), false)
                        }
                    } else {
                        createDialog(getString(R.string.error), getString(R.string.wrong_password), true)
                    }
                } else {
                    createDialog(getString(R.string.error), getString(R.string.non_existing_user), true)
                }
                actvUser.text.clear()
                etPassword.text.clear()
            } else {
                Toast.makeText(this, getString(R.string.no_conected), Toast.LENGTH_SHORT).show()
            }
        }

        buttonForgotten.setOnClickListener{
            if (functionalities.checkConnection(connectivityManager)){
                val userLogin = actvUser.text.toString()
                val newPassword = functionalities.generateRandomPassword(10)
                val userForgottenPassword = User(id = userLogin, password = newPassword)
                //Mandar este usuario al servidor
                val senderEmail = "elorclass@gmail.com"
                val senderPassword = "apld msns reek cocx"
                val recipientEmail = "ugaitz.corderosa@elorrieta-errekamari.com"
                val subject = "asunto"
                val message = "mensaje"

                SendEmailTask(senderEmail, senderPassword, recipientEmail, subject, message).execute()

            }
        }
    }

    private fun createDialog(title: String, message: String, registered: Boolean) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
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

    private fun setLocale(language: String) {
        var languageCode=""
        when (language){
            getString(R.string.english) -> languageCode = "en"
            getString(R.string.spanish) -> languageCode = "es"
            getString(R.string.portugues) -> languageCode = "pt"
            getString(R.string.basque) -> languageCode = "eu"
        }
        if(languageCode != Locale.getDefault().language) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    private fun setAppTheme(theme: String) {
        val mode = if (theme == getString(R.string.dark)) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}