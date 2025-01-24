package com.example.elorclass

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.Visibility
import com.example.elorclass.data.User
import com.example.elorclass.data.UserSession
import com.example.elorclass.functionalities.Functionalities

class RegisterActivity : AppCompatActivity() {

    lateinit var etName: EditText;
    lateinit var etSurname: EditText;
    lateinit var etId: EditText;
    lateinit var etAdress: EditText;
    lateinit var etFirstTelephone: EditText;
    lateinit var etSecondTelephone: EditText;
    lateinit var etStudies: EditText;
    lateinit var etYear: EditText;
    lateinit var etDual: EditText;
    lateinit var etPassword: EditText;
    lateinit var etConfirmPassword: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.register)
        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val functionalities = Functionalities()
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonGoBack = findViewById<Button>(R.id.buttonLogout)
        val buttonCamera = findViewById<ImageButton>(R.id.imageButtonCamera)
        val years = ArrayList<String>()
        years.add(getString(R.string.first))
        years.add(getString(R.string.second))
        val selectedOptionInteger = 0
        etName = findViewById<EditText>(R.id.editTextName)
        etSurname = findViewById<EditText>(R.id.editTextSurname)
        etId = findViewById<EditText>(R.id.editTextID)
        etAdress = findViewById<EditText>(R.id.editTextAdress)
        etFirstTelephone = findViewById<EditText>(R.id.editTextFirstTelephone)
        etSecondTelephone = findViewById<EditText>(R.id.editTextSecondTelephone)
        etStudies = findViewById<EditText>(R.id.editTextStudies)
        etYear = findViewById<EditText>(R.id.editTextYear)
        etDual = findViewById<EditText>(R.id.editTextDual)
        etPassword = findViewById<EditText>(R.id.editTextPassword)
        etConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)

        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val imageBitmap = intent?.extras?.get("data") as Bitmap
                    val imageView = findViewById<ImageView>(R.id.imageView2)
                    imageView.setImageBitmap(imageBitmap)
                }
            }

        autoCompleteData(
            etName,
            etSurname,
            etId,
            etAdress,
            etFirstTelephone,
            etSecondTelephone,
            etStudies,
            etYear,
            etDual
        )


        if (UserSession.fetchUser()?.userTypes?.id?.toInt() != 2) {
            etStudies.visibility = View.GONE
            etYear.visibility = View.GONE
            etDual.visibility = View.GONE
        }
        buttonRegister.setOnClickListener {
            if (functionalities.checkConnection(connectivityManager)) {
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
                    && password.isNotEmpty() && confirmPassword.isNotEmpty()
                ) {
                    if (password == confirmPassword) {
                        if (password == UserSession.fetchUser()?.password) {
                            Toast.makeText(
                                this, getString(R.string.change_your_password), Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            // Crear usuario o coger usuario
                            val testUser: User = User()

                            //ENVIAR "user" A LA BASE DE DATOS
                            UserSession.setUserSession(testUser)


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
            if (functionalities.checkConnection(connectivityManager)) {
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
        etName?.setText(UserSession.fetchUser()?.name!!)
        etSurname?.setText(UserSession.fetchUser()?.lastNames!!)
        etId?.setText(UserSession.fetchUser()?.dni!!)
        etAdress?.setText(UserSession.fetchUser()?.address!!)
        etFirstTelephone?.setText(UserSession.fetchUser()?.phone!!)
        etSecondTelephone?.setText(UserSession.fetchUser()?.phone2!!)
        etStudies?.setText(UserSession.fetchUser()?.studies!!)
        val year = UserSession.fetchUser()?.schoolyear
        if (year == 1)
            etYear?.setText(getString(R.string.first))
        else
            etYear?.setText(getString(R.string.second))
        if (UserSession.fetchUser()?.dual == true) {
            etDual?.setText(getString(R.string.dual_studies))
        } else
            etDual?.setText(getString(R.string.no_dual_studies))
    }

    private fun clearFields() {
        etName.text.clear()
        etSurname.text.clear()
        etId.text.clear()
        etAdress.text.clear()
        etFirstTelephone.text.clear()
        etSecondTelephone.text.clear()
        etStudies.text.clear()
        etPassword.text.clear()
        etConfirmPassword.text.clear()
    }
}