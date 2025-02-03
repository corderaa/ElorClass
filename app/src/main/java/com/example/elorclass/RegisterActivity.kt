package com.example.elorclass

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.example.elorclass.data.User
import com.example.elorclass.data.UserSession
import com.example.elorclass.functionalities.AppDatabase
import com.example.elorclass.functionalities.Functionalities
import com.example.elorclass.socketIO.SocketClient
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class RegisterActivity : BaseActivity() {

    private var socketClient: SocketClient? = null
    private val gson = Gson()

    lateinit var etName: EditText
    lateinit var etSurname: EditText
    lateinit var etId: EditText
    lateinit var etAdress: EditText
    lateinit var etFirstTelephone: EditText
    lateinit var etSecondTelephone: EditText
    lateinit var etStudies: EditText
    lateinit var etYear: EditText
    lateinit var etDual: EditText
    lateinit var etMail: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var imageView: ImageView
    val user = UserSession.fetchUser()
    var isIncomplete = false
    var imageBytes: ByteArray? = null
    private lateinit var db: AppDatabase

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
        etName = findViewById<EditText>(R.id.editTextName)
        etSurname = findViewById<EditText>(R.id.editTextSurname)
        etId = findViewById<EditText>(R.id.editTextID)
        etAdress = findViewById<EditText>(R.id.editTextAdress)
        etFirstTelephone = findViewById<EditText>(R.id.editTextFirstTelephone)
        etSecondTelephone = findViewById<EditText>(R.id.editTextSecondTelephone)
        etMail = findViewById<EditText>(R.id.editTextMail)
        etStudies = findViewById<EditText>(R.id.editTextStudies)
        etYear = findViewById<EditText>(R.id.editTextYear)
        etDual = findViewById<EditText>(R.id.editTextDual)
        etPassword = findViewById<EditText>(R.id.editTextPassword)
        etConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        imageView = findViewById<ImageView>(R.id.imageView2)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "AppDatabase"
        ).allowMainThreadQueries().build()

        socketClient = SocketClient(null, this)

        try {
            autoCompleteData(
                etName,
                etSurname,
                etId,
                etAdress,
                etFirstTelephone,
                etSecondTelephone,
                etMail,
                etStudies,
                etYear,
                etDual,
                imageView
            )

        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }

        if (UserSession.fetchUser()?.userTypes?.id?.toInt() != 4) {
            etStudies.visibility = View.GONE
            etYear.visibility = View.GONE
            etDual.visibility = View.GONE
        }
        buttonRegister.setOnClickListener {
            socketClient!!.connect()
            if (functionalities.checkConnection(connectivityManager)) {
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()
                val name = etName.text.toString()
                val surname = etSurname.text.toString()
                val id = etId.text.toString()
                val adress = etAdress.text.toString()
                val firstTelephone = etFirstTelephone.text.toString()
                val secondTelephone = etSecondTelephone.text.toString()
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
                            val registeredUser = User()
                            registeredUser.registered = true
                            registeredUser.name = name
                            registeredUser.password = password
                            registeredUser.lastNames = surname
                            registeredUser.dni = id
                            registeredUser.address = adress
                            registeredUser.phone = firstTelephone
                            registeredUser.phone2 = secondTelephone
                            if (imageBytes != null)
                                registeredUser.photo = byteArrayToBase64(imageBytes!!)

                            //ENVIAR "user" A LA BASE DE DATOS
                            UserSession.setUserSession(registeredUser)
                            socketClient?.emit("onRegister", this.gson.toJson(registeredUser))
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                // Solicitar permiso
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val imageBitmap = intent?.extras?.get("data") as Bitmap
                imageView.setImageBitmap(imageBitmap)
                imageBytes = bitmapToByteArray(imageBitmap)
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startForResult.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    private fun autoCompleteData(
        etName: EditText?,
        etSurname: EditText?,
        etId: EditText?,
        etAdress: EditText?,
        etFirstTelephone: EditText?,
        etSecondTelephone: EditText?,
        etMail: EditText?,
        etStudies: EditText?,
        etYear: EditText?,
        etDual: EditText?,
        imageView: ImageView?,
    ) {
        val user = UserSession.fetchUser()
        Log.d("UserSession", "UserSession " + user)
        if(user?.photo != null)
            imageView?.setImageBitmap(byteArrayToBitmap(base64ToByteArray(user.photo!!)))
        if (user?.name != null)
            etName?.setText(user.name)
        else
            isIncomplete = true
        if (user?.lastNames != null)
            etSurname?.setText(user.lastNames)
        else
            isIncomplete = true
        if (user?.dni != null)
            etId?.setText(user.dni)
        else
            isIncomplete = true
        if (user?.address != null)
            etAdress?.setText(user.address)
        else
            isIncomplete = true
        if (user?.phone != null)
            etFirstTelephone?.setText(user.phone)
        else
            isIncomplete = true
        if (user?.phone2 != null)
            etSecondTelephone?.setText(user.phone2)
        else
            isIncomplete = true
        if (user?.email != null)
            etMail?.setText(user.email)
        else
            isIncomplete = true
        if (user?.studies != null)
            etStudies?.setText(user.studies)
        val year = user?.schoolYear
        if (year == null)
            isIncomplete = true
        else {
            if (year == 1)
                etYear?.setText(getString(R.string.first))
            else
                etYear?.setText(getString(R.string.second))
            if (user.dualStudies == null)
                isIncomplete = true
            else {
                if (user.dualStudies == true) {
                    etDual?.setText(getString(R.string.dual_studies))
                } else
                    etDual?.setText(getString(R.string.no_dual_studies))
            }
        }
        if (isIncomplete) {
            Toast.makeText(
                this, "Datos incompletos", Toast.LENGTH_SHORT
            ).show()
            isIncomplete = !isIncomplete
        }
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

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun byteArrayToBase64(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToByteArray(base64String: String): ByteArray {
        return Base64.decode(base64String, Base64.DEFAULT)
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


    fun registerSuccess(){
        val intent = Intent(this, MainPanelActivity::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(
            this, "Registrado con éxito", Toast.LENGTH_SHORT
        ).show()
    }
    fun registerFail(){
        Toast.makeText(this, "Error en el registro", Toast.LENGTH_LONG).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        socketClient?.disconnect()
    }
}