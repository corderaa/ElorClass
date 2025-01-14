package com.example.elorclass

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.elorclass.data.UserSession
import com.example.elorclass.functionalities.Functionalities
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile)

        var language=""
        var theme=""
        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val functionalities = Functionalities()
        val buttonChangePassword: Button = findViewById(R.id.buttonChangePassword)
        val buttonChangeLanguage: Button = findViewById(R.id.buttonChangeLanguage)
        val buttonChangeTheme: Button = findViewById(R.id.buttonChangeTheme)
        val buttonGoBack: Button = findViewById(R.id.buttonGoBack)
        val etOldPassword = findViewById<EditText>(R.id.editTextOldPassword)
        val etNewPassword = findViewById<EditText>(R.id.editTextNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.editTextConfirmNewPassword)

        buttonChangePassword.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                val oldPassword = etOldPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()
                if(oldPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()){
                    if(oldPassword == UserSession.fetchPassword()) {
                        if (newPassword == confirmPassword) {
                            Toast.makeText(
                                this, getString(R.string.password_updated), Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this, getString(R.string.wrong_password), Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this, getString(R.string.complete_fields), Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this, getString(R.string.no_conected), Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonChangeLanguage.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                Toast.makeText(
                    this, getString(R.string.language), Toast.LENGTH_SHORT
                ).show()
                val languages = listOf(getString(R.string.spanish), getString(R.string.english), getString(R.string.basque), getString(R.string.portugues))
                createDialog(languages, getString(R.string.language)){selectedOption -> language = selectedOption
                    setLocale(language)
                }
            } else {
                Toast.makeText(
                    this, getString(R.string.no_conected), Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonChangeTheme.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                Toast.makeText(
                    this, getString(R.string.theme), Toast.LENGTH_SHORT
                ).show()
                val themes = listOf("Claro", "Oscuro")
                createDialog(themes, "Tema"){ selectedOption -> theme = selectedOption}
            } else {
                Toast.makeText(
                    this, getString(R.string.no_conected), Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonGoBack.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                val intent = Intent(this, MainPanelActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this, getString(R.string.no_conected), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun createDialog(list: List<String>, title: String, onOptionSelected: (String) -> Unit) {
        val spinner = Spinner(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(spinner)
            .setPositiveButton("Aceptar") { _, _ ->
                val selectedOption = spinner.selectedItem.toString()
                onOptionSelected(selectedOption)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        alertDialog.show()
    }

    private fun setLocale(language: String) {
        var languageCode=""
        when (language){
            "Inglés" -> languageCode = "en"
            "Español" -> languageCode = "es"
            "Portugués" -> languageCode = "pt"
            "Euskera" -> languageCode = "eu"
        }
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }
}