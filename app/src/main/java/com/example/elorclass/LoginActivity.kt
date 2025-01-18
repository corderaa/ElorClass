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
import com.example.elorclass.socketIO.SocketClient
import com.example.elorclass.socketIO.config.Events
import com.google.gson.Gson
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private var socketClient: SocketClient? = null
    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login)

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
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

        socketClient = SocketClient(this)



        if (users.isNotEmpty()) {
            val usersNames = ArrayList<String>()
            val usersPasswords = ArrayList<String>()
            for (user in users) {
                usersNames.add(user.userLogin)
                usersPasswords.add(user.password)
            }
            val adapterUser =
                ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, usersNames)
            val rememberedUserLogin: String = usersNames[users.size - 1]
            val rememberPassword: String = usersPasswords[users.size - 1]
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
                val password = etPassword.text.toString()
                login(userId, password)
                //PEDIR USUARIO

                // UserSession.setUserSession(
                //    usuarioDePrueba.name!!,
                //     usuarioDePrueba.lastNames!!,
                //     usuarioDePrueba.dni!!,
                //     usuarioDePrueba.address!!,
                //     usuarioDePrueba.phone!!,
                //     usuarioDePrueba.phone2!!,
                //     usuarioDePrueba.studies!!,
                //     usuarioDePrueba.password!!,
                //     usuarioDePrueba.schoolyear!!,
                //     usuarioDePrueba.dual!!,
                //     usuarioDePrueba.registered!!,
                //     usuarioDePrueba.role!!
                // )
                if (cbRememberMe.isChecked) {
                    val rememberMeUser = RememberMeDB(
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
                        ).show()
                    }
                    db.rememberMeDao().insertAll(rememberMeUser)
                }
                actvUser.text.clear()
                etPassword.text.clear()
            } else {
                createDialog(
                    getString(R.string.error), "No tienes conexion", true
                )
            }

        }

        buttonForgotten.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)) {
                val userLogin = actvUser.text.toString()
                val newPassword = functionalities.generateRandomPassword(10)
                val userForgottenPassword = User(dni = userLogin, password = newPassword)
                //Mandar este usuario al servidor
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

        socketClient?.emit("onLogin", message.toString())
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

    fun loginSuccess(user: User) {
        UserSession.setUserSession(user)

        if (user.registered == true) {
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
            val intent = Intent(this, MainPanelActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            createDialog(
                getString(R.string.register),
                getString(R.string.register_needed),
                false
            )
        }
    }

    fun loginFailed(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
