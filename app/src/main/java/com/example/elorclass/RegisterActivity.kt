package com.example.elorclass

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = parentView?.getItemAtPosition(position).toString()
                if (selectedOption == "Segundo")
                    cbDual.visibility = View.VISIBLE
                else
                    cbDual.visibility = View.INVISIBLE
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                cbDual.visibility = View.INVISIBLE
            }
        }

        buttonRegister.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)){
                Toast.makeText(
                    this, "conectado", Toast.LENGTH_SHORT
                ).show()
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