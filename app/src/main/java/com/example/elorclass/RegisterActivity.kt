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
import com.example.elorclass.data.UserSession
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
        val buttonGoBack = findViewById<Button>(R.id.buttonLogout)
        val years = ArrayList<String>()
        years.add("Primero")
        years.add("Segundo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        var selectedOption: String
        var selectedOptionInteger : Int = 0
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
                if (selectedOption == "Segundo") {
                    selectedOptionInteger = 2
                    cbDual.visibility = View.VISIBLE
                }else {
                    cbDual.visibility = View.INVISIBLE
                    cbDual.isChecked = false
                    selectedOptionInteger = 1
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                cbDual.visibility = View.INVISIBLE
                cbDual.isChecked = false
            }
        }

        autoCompleteData(etName, etSurname, etId, etAdress, etFirstTelephone, etSecondTelephone, etStudies, etPassword, spinner, cbDual)

        buttonRegister.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()
                val name = etName.text.toString()
                val surname = etSurname.text.toString()
                val id = etId.text.toString()
                val adress = etAdress.text.toString()
                val firstTelephone = etFirstTelephone.text.toString()
                val secondTelephone = etSecondTelephone.text.toString()
                val studies = etStudies.text.toString()
                if (name.isNotEmpty() && surname.isNotEmpty() && id.isNotEmpty()
                    && adress.isNotEmpty() && firstTelephone.isNotEmpty() && secondTelephone.isNotEmpty()
                    && studies.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()){
                    if (password == confirmPassword) {
                        val dual = cbDual.isChecked
                        val user = User(
                            name = name,
                            surname = surname,
                            id = id,
                            adress = adress,
                            firstTelephone = firstTelephone,
                            secondTelephone = secondTelephone,
                            studies = studies,
                            schoolyear = selectedOptionInteger,
                            dual = dual,
                            password = password,
                            registered = true
                        )
                        //ENVIAR "user" A LA BASE DE DATOS
                        UserSession.setUserSession(user.name, user.surname, user.id,
                            user.adress, user.firstTelephone, user.secondTelephone,
                            user.studies, user.password, user.schoolyear,
                            user.dual, user.registered)
                        etName.text.clear()
                        etSurname.text.clear()
                        etId.text.clear()
                        etAdress.text.clear()
                        etFirstTelephone.text.clear()
                        etSecondTelephone.text.clear()
                        etStudies.text.clear()
                        etPassword.text.clear()
                        etConfirmPassword.text.clear()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(
                            this, "Usuario registrado", Toast.LENGTH_SHORT
                        ).show()
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

        buttonGoBack.setOnClickListener {
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

    private fun autoCompleteData(
        etName: EditText?,
        etSurname: EditText?,
        etId: EditText?,
        etAdress: EditText?,
        etFirstTelephone: EditText?,
        etSecondTelephone: EditText?,
        etStudies: EditText?,
        etPassword: EditText?,
        spinner: Spinner?,
        cbDual: CheckBox?,
    ) {
        etName?.setText(UserSession.fetchName()!!)
        etSurname?.setText(UserSession.fetchSurname()!!)
        etId?.setText(UserSession.fetchId()!!)
        etAdress?.setText(UserSession.fetchAdress()!!)
        etFirstTelephone?.setText(UserSession.fetchFirstTelephone()!!)
        etSecondTelephone?.setText(UserSession.fetchSecondTelephone()!!)
        etStudies?.setText(UserSession.fetchStudies()!!)
        etPassword?.setText(UserSession.fetchPassword())
        val year = UserSession.fetchSchoolyear()
        if (year != null) {
            spinner?.setSelection(year-1)
        }
        if (UserSession.fetchDual()==true){
            cbDual?.isChecked = true
        }
    }
}