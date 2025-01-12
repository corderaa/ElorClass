package com.example.elorclass

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.elorclass.data.User
import com.example.elorclass.functionalities.Functionalities

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register)
        val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val functionalities = Functionalities()
        val spinner = findViewById<Spinner>(R.id.spinner)
        val cbDual = findViewById<CheckBox>(R.id.checkDual)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonVolver = findViewById<Button>(R.id.buttonGoBack)
        val years = ArrayList<String>()
        years.add("Primero")
        years.add("Segundo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        var selectedOption: String =""
        val etLogin = findViewById<EditText>(R.id.editTextLogin)
        val etName = findViewById<EditText>(R.id.editTextName)
        val etSurname = findViewById<EditText>(R.id.editTextSurname)
        val etId = findViewById<EditText>(R.id.editTextID)
        val etAdress = findViewById<EditText>(R.id.editTextAdress)
        val etFirstTelephone = findViewById<EditText>(R.id.editTextFirstTelephone)
        val etSecondTelephone = findViewById<EditText>(R.id.editTextSecondTelephone)
        val etStudies = findViewById<EditText>(R.id.editTextStudies)
        val etPassword = findViewById<EditText>(R.id.editTextPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)

        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedOption = parentView?.getItemAtPosition(position).toString()
                if (selectedOption == "Segundo")
                    cbDual.visibility = View.VISIBLE
                else {
                    cbDual.visibility = View.INVISIBLE
                    cbDual.isChecked = false
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                cbDual.visibility = View.INVISIBLE
                cbDual.isChecked = false
            }
        }

        buttonRegister.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()
                val login = etLogin.text.toString()
                val name = etName.text.toString()
                val surname = etSurname.text.toString()
                val id = etId.text.toString()
                val adress = etAdress.text.toString()
                val firstTelephone = etFirstTelephone.text.toString()
                val secondTelephone = etSecondTelephone.text.toString()
                val studies = etStudies.text.toString()
                if (login.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty() && id.isNotEmpty()
                    && adress.isNotEmpty() && firstTelephone.isNotEmpty() && secondTelephone.isNotEmpty()
                    && studies.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                    if (password == confirmPassword) {
                        var dual = cbDual.isChecked
                        val user = User(
                            login = login,
                            name = name,
                            surname = surname,
                            id = id,
                            adress = adress,
                            firstTelephone = firstTelephone,
                            secondTelephone = secondTelephone,
                            studies = studies,
                            year = selectedOption,
                            dual = dual,
                            password = password
                        )
                        //PEDIR USUARIO AL SERVIDOR Y COMPARARLO CON "user"
                        // ENVIAR "user" AL SERVIDOR
                        etLogin.text.clear()
                        etName.text.clear()
                        etSurname.text.clear()
                        etId.text.clear()
                        etAdress.text.clear()
                        etFirstTelephone.text.clear()
                        etSecondTelephone.text.clear()
                        etStudies.text.clear()
                        etPassword.text.clear()
                        etConfirmPassword.text.clear()
                    } else {
                        Toast.makeText(
                            this, "La contraseña no coincide", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this, "Debe rellenar todos los campos", Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this, "No conectado", Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonVolver.setOnClickListener {
            if(functionalities.checkConnection(connectivityManager)){
                val intent = Intent(this, LoginActivity::class.java)
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