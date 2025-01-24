package com.example.elorclass


import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
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
import com.example.elorclass.socketIO.SocketClient
import com.google.gson.Gson
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private var socketClient: SocketClient? = null
    val gson = Gson()
    var cbRememberMe: CheckBox? = null;
    var actvUser: AutoCompleteTextView? = null;
    var etPassword: EditText? = null;
    var users: List<RememberMeDB>? = null;
    var password: String? = null;
    val functionalities = Functionalities()


    lateinit var db: AppDatabase;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "AppDatabase"
        ).allowMainThreadQueries().build()
        users = db.rememberMeDao().getAll()
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val buttonForgotten: Button = findViewById(R.id.buttonForgotten)
        val actvUser: AutoCompleteTextView = findViewById(R.id.autoCompleteTextViewUser)
        val etPassword: EditText = findViewById(R.id.editTextPassword)
        val cbRememberMe: CheckBox = findViewById(R.id.checkBoxRememberMe)

        socketClient = SocketClient(this)



        if (users!!.isNotEmpty()) {
            val usersNames = ArrayList<String>()
            val usersPasswords = ArrayList<String>()
            for (user in users!!) {
                usersNames.add(user.userLogin)
                usersPasswords.add(user.password)
            }
            val adapterUser =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, usersNames)
            val rememberedUserLogin: String = usersNames[users!!.size - 1]
            val rememberPassword: String = usersPasswords[users!!.size - 1]
            actvUser.setAdapter(adapterUser)
            actvUser.threshold = 1
            actvUser.setText(rememberedUserLogin)
            etPassword.setText(rememberPassword)
            actvUser.setOnItemClickListener { _, _, _, _ ->
                etPassword.setText(usersPasswords[usersNames.indexOf(actvUser.text.toString())])
            }

            cbRememberMe.isChecked = true
        }

        val user = actvUser


        buttonLogin.setOnClickListener {

            if (functionalities.checkConnection(connectivityManager)) {
                socketClient!!.connect()
                val userId = actvUser.text.toString()
                password = etPassword.text.toString()
                login(userId, password!!)

            } else {
                createDialog(
                    getString(R.string.error), "No tienes conexion", true
                )
            }

        }

        buttonForgotten.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)) {
                socketClient!!.connect()
                val userLogin = actvUser.text.toString()
                changePassword(userLogin)

                val senderEmail = "elorclass@gmail.com"
                val senderPassword = "apld msns reek cocx"
                val recipientEmail = "ugaitz.corderosa@elorrieta-errekamari.com"
                val subject = "asunto"
                val message = "mensaje"

                SendEmailTask(senderEmail, senderPassword, recipientEmail, subject, message).execute()

            }
        }
    }

    fun createDialog(title: String, message: String, registered: Boolean) {
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

    fun login(dni: String, password: String) {

        var newUser = User();
        newUser.dni = dni;
        newUser.password = password;

        val message = this.gson.toJson(newUser);

        socketClient?.emit("onLogin", message)
    }

    fun changePassword(dni: String) {

        var newUser = User();
        newUser.dni = dni;
        val randomPassword = functionalities.generateRandomPassword(10)
        newUser.password = randomPassword
        password = randomPassword
        val message = this.gson.toJson(newUser);

        socketClient?.emit("onPasswordChange", message)
    }

    fun setLocale(language: String) {
        var languageCode = ""
        when (language) {
            getString(R.string.english) -> languageCode = "en"
            getString(R.string.spanish) -> languageCode = "es"
            getString(R.string.portugues) -> languageCode = "pt"
            getString(R.string.basque) -> languageCode = "eu"
        }
        if (languageCode != Locale.getDefault().language) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    fun setAppTheme(theme: String) {
        val mode = if (theme == getString(R.string.dark)) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun passwordChangedSuccess(user:User){

        val senderEmail = "elorclass@gmail.com"
        val senderPassword = "apld msns reek cocx"
        val recipientEmail = user.email.toString()
        val subject = "asunto"
        val message = password.toString()

        SendEmailTask(senderEmail, senderPassword, recipientEmail, subject, message).execute()
        password = null
    }

    fun loginSuccess(user: User) {
        val cbRememberMeTest: CheckBox = findViewById(R.id.checkBoxRememberMe)
        if (cbRememberMeTest!!.isChecked) {
            val rememberMeUser = user.dni?.let {
                password?.let { it2 ->
                    RememberMeDB(
                        userLogin = it,
                        password = it2
                    )
                }
            }
            val userToDelete = users?.find { user.dni.equals(it.userLogin, ignoreCase = true) }
            if (userToDelete != null) {
                db?.rememberMeDao()?.delete(userToDelete)

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.remembered_user),
                    Toast.LENGTH_SHORT
                ).show()

            }
            try {
                Log.d("d", rememberMeUser?.userLogin.toString())
                db?.rememberMeDao()?.insertAll(rememberMeUser!!)
            } catch (e: Exception) {
                e.message
                Log.e("Database Error", e.toString())
            }

            UserSession.setUserSession(user)
            val dbPreferences = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "AppDatabase"
            ).allowMainThreadQueries().build()
            val userPreference = dbPreferences.preferencesDao()
                .getPreferenceByLogin(UserSession.fetchUser()?.dni.toString())
            if (userPreference != null) {
                val language = userPreference.language
                val theme = userPreference.theme
                if (language != null && language != Locale.getDefault().language)
                    setLocale(language)
                if (theme != null)
                    setAppTheme(theme)
            }
        }

        actvUser?.text?.clear()
        etPassword?.text?.clear()

        val intent = Intent(this, MainPanelActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun loginFailed(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
