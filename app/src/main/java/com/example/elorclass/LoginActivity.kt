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

class LoginActivity : BaseActivity() {

    private var socketClient: SocketClient? = null
    private val gson = Gson()
    private var actvUser: AutoCompleteTextView? = null
    private var etPassword: EditText? = null
    private var users: List<RememberMeDB>? = null
    private var password: String? = null
    private val functionalities = Functionalities()


    private lateinit var db: AppDatabase
    private lateinit var randomPassword:String
    private lateinit var cbRememberMeTest: CheckBox

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
        cbRememberMeTest = findViewById(R.id.checkBoxRememberMe)

        socketClient = SocketClient(this, null)



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

            cbRememberMeTest.isChecked = true
        }


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
                changeForgottenPassword(userLogin)
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

        val newUser = User()
        newUser.dni = dni
        newUser.password = password

        val message = this.gson.toJson(newUser)

        socketClient?.emit("onLogin", message)
    }

    fun changeForgottenPassword(dni: String) {

        val newUser = User()
        newUser.dni = dni
        randomPassword = functionalities.generateRandomPassword(10)
        newUser.password = randomPassword
        password = randomPassword
        val message = this.gson.toJson(newUser)

        socketClient?.emit("onForgottenPasswordChange", message)

        val senderEmail = "elorclass@gmail.com"
        val senderPassword = "apld msns reek cocx"
        val recipientEmail = UserSession.fetchUser()?.email.toString()
        val subject = "asunto"
        val messageToSend = "Tu nueva contraseña es: \n $randomPassword"

        SendEmailTask(
            senderEmail,
            senderPassword,
            recipientEmail,
            subject,
            messageToSend
        ).execute()
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

    fun userRegistered(user: User) {
        if (cbRememberMeTest.isChecked) {
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
                db.rememberMeDao().delete(userToDelete)

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.remembered_user),
                    Toast.LENGTH_SHORT
                ).show()

            }
            try {
                Log.d("d", rememberMeUser?.userLogin.toString())
                Log.d("d", rememberMeUser?.password.toString())
                db.rememberMeDao().insertAll(rememberMeUser!!)
            } catch (e: Exception) {
                e.message
                Log.e("Database Error", e.toString())
            }
        }
        if (user.registered == true) {
            loginSuccess(user)
        } else {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun loginSuccess(user: User) {
        if (cbRememberMeTest.isChecked) {
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
                db.rememberMeDao().delete(userToDelete)

            } else {
                Toast.makeText(
                    this,
                    getString(R.string.remembered_user),
                    Toast.LENGTH_SHORT
                ).show()

            }
            try {
                Log.d("d", rememberMeUser?.userLogin.toString())
                Log.d("d", rememberMeUser?.password.toString())
                db.rememberMeDao().insertAll(rememberMeUser!!)
            } catch (e: Exception) {
                e.message
                Log.e("Database Error", e.toString())
            }
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

    fun changeForgottenPasswordSuccess() {
        Toast.makeText(this, "Revise su correo", Toast.LENGTH_LONG).show()
        val senderEmail = "elorclass@gmail.com"
        val senderPassword = "apld msns reek cocx"
        val recipientEmail = UserSession.fetchUser()?.email.toString()
        val subject = "asunto"
        val messageToSend = "Tu nueva contraseña es: \n $randomPassword"

        SendEmailTask(
            senderEmail,
            senderPassword,
            recipientEmail,
            subject,
            messageToSend
        ).execute()
    }

    fun changeForgottenPasswordFailed(){
        Toast.makeText(this, "Ha habido un error", Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        if (socketClient != null && socketClient!!.isConnected())
        socketClient?.disconnect()
    }

    override fun onStop() {
        super.onStop()
        if (socketClient != null && socketClient!!.isConnected())
        socketClient?.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (socketClient != null && socketClient!!.isConnected())
        socketClient?.disconnect()
    }
}
