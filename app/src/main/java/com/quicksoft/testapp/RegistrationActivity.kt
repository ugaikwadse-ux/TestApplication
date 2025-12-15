package com.quicksoft.testapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.sign

class RegistrationActivity : AppCompatActivity() {
    private lateinit var back_btn: AppCompatImageView
    private lateinit var inputemail: AppCompatEditText
    private lateinit var inputpassword: AppCompatEditText
    private lateinit var inputcomfirmpassword: AppCompatEditText
    private lateinit var inputmobileno: AppCompatEditText
    private lateinit var inputpasscode: AppCompatEditText
    private lateinit var inputfullname: AppCompatEditText
    private lateinit var signupbtn: AppCompatButton

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLatitude: Double? = null
    private var userLongitude: Double? = null

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            if (granted) {
                fetchCurrentLocation()
            } else {
                Toast.makeText(this@RegistrationActivity, "Location permission required", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@RegistrationActivity)

        back_btn = findViewById(R.id.back_btn)
        inputemail = findViewById(R.id.inputemail)
        inputpassword = findViewById(R.id.inputpassword)
        inputcomfirmpassword = findViewById(R.id.inputcomfirmpassword)
        inputmobileno = findViewById(R.id.inputmobileno)
        inputpasscode = findViewById(R.id.inputPassword)
        inputfullname = findViewById(R.id.inputfullname)
        signupbtn = findViewById(R.id.signupbtn)

        back_btn.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity, SiginActivity::class.java))
        }

        checkLocationPermission()
        signupbtn.setOnClickListener {

            val mAuth = FirebaseAuth.getInstance()

            val email = inputemail.text.toString()
            val password = inputpassword.text.toString()
            val confirmPassword = inputcomfirmpassword.text.toString()
            val mobile = inputmobileno.text.toString()
            val fullname = inputfullname.text.toString()

            if (
                email.isNotEmpty() &&
                password.isNotEmpty() &&
                confirmPassword.isNotEmpty() &&
                mobile.isNotEmpty() &&
                password == confirmPassword &&
                mobile.length == 10
            ) {

                mAuth.createUserWithEmailAndPassword(email, confirmPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser
                            val uid = user?.uid ?: return@addOnCompleteListener

                            saveUserToFirestore(uid, email, fullname)
                        }
                    }

            } else {
                Toast.makeText(this@RegistrationActivity, "Enter valid Data", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveUserToFirestore(
        uid: String,
        email: String,
        name: String
    ) {

        val db = FirebaseFirestore.getInstance()

        val userData = hashMapOf(
            "email" to email,
            "name" to name,
            "isUser" to "1",
            "latitude" to userLatitude,
            "longitude" to userLongitude
        )

        db.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))

            }
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@RegistrationActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    userLatitude = location.latitude
                    userLongitude = location.longitude
                }
            }
    }


}