package com.example.elorclass

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.elorclass.functionalities.Functionalities

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile)

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
                    if (newPassword == confirmPassword) {
                        Toast.makeText(
                            this, "Contraseña actualizada", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this, "La contraseña no coincide", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this, "Rellena todos los campos", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this, "No conectado", Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonChangeLanguage.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                Toast.makeText(
                    this, "Idioma", Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, "No conectado", Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonChangeTheme.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                Toast.makeText(
                    this, "Tema", Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this, "No conectado", Toast.LENGTH_SHORT
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
                    this, "No conectado", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}