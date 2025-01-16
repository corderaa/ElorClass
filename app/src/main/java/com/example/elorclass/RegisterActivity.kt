package com.example.elorclass

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonGoBack = findViewById<Button>(R.id.buttonLogout)
        val buttonCamera = findViewById<ImageButton>(R.id.imageButtonCamera)
        val years = ArrayList<String>()
        years.add(getString(R.string.first))
        years.add(getString(R.string.second))
        val selectedOptionInteger = 0
        val etName = findViewById<EditText>(R.id.editTextName)
        val etSurname = findViewById<EditText>(R.id.editTextSurname)
        val etId = findViewById<EditText>(R.id.editTextID)
        val etAdress = findViewById<EditText>(R.id.editTextAdress)
        val etFirstTelephone = findViewById<EditText>(R.id.editTextFirstTelephone)
        val etSecondTelephone = findViewById<EditText>(R.id.editTextSecondTelephone)
        val etStudies = findViewById<EditText>(R.id.editTextStudies)
        val etYear = findViewById<EditText>(R.id.editTextYear)
        val etDual = findViewById<EditText>(R.id.editTextDual)
        val etPassword = findViewById<EditText>(R.id.editTextPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)

        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val imageBitmap = intent?.extras?.get("data") as Bitmap
                    val imageView = findViewById<ImageView>(R.id.imageView2)
                    imageView.setImageBitmap(imageBitmap)
                }
            }

        autoCompleteData(etName, etSurname, etId, etAdress, etFirstTelephone, etSecondTelephone, etStudies, etYear, etDual)

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
                        if(password == UserSession.fetchPassword()){
                            Toast.makeText(
                                this, getString(R.string.change_your_password), Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val dual = true
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
                            UserSession.setUserSession(
                                user.name, user.surname, user.id,
                                user.adress, user.firstTelephone, user.secondTelephone,
                                user.studies, user.password, user.schoolyear,
                                user.dual, user.registered
                            )
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
                                this, getString(R.string.user_registered), Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this, getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT
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

        buttonGoBack.setOnClickListener {
            if(functionalities.checkConnection(connectivityManager)){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this, getString(R.string.no_conected), Toast.LENGTH_SHORT
                ).show()
            }
        }

        buttonCamera.setOnClickListener {
            startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
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
        etYear: EditText?,
        etDual: EditText?,
    ) {
        etName?.setText(UserSession.fetchName()!!)
        etSurname?.setText(UserSession.fetchSurname()!!)
        etId?.setText(UserSession.fetchId()!!)
        etAdress?.setText(UserSession.fetchAdress()!!)
        etFirstTelephone?.setText(UserSession.fetchFirstTelephone()!!)
        etSecondTelephone?.setText(UserSession.fetchSecondTelephone()!!)
        etStudies?.setText(UserSession.fetchStudies()!!)
        val year = UserSession.fetchSchoolyear()
        if (year == 1)
            etYear?.setText(getString(R.string.first))
        else
            etYear?.setText(getString(R.string.second))
        if (UserSession.fetchDual()==true){
            etDual?.setText(getString(R.string.dual_studies))
        } else
            etDual?.setText(getString(R.string.no_dual_studies))
    }
}