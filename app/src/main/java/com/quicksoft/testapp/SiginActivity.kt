package com.quicksoft.testapp

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class SiginActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var inputPassword: EditText
    private lateinit var inputEmail: EditText
    private lateinit var tvDontHaveAcc: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var mLoadingDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sigin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mAuth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        tvDontHaveAcc = findViewById(R.id.tv_dont_have_acc)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        loginBtn = findViewById(R.id.login_Buttton)


        mLoadingDialog = ProgressDialog(this@SiginActivity)

        val spannableString = SpannableString("Don't have an account? Sign Up")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = false
            }
        }

        spannableString.setSpan(
            clickableSpan,
            23,
            30,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvDontHaveAcc.text = spannableString
        tvDontHaveAcc.movementMethod = LinkMovementMethod.getInstance()
        tvDontHaveAcc.highlightColor = Color.TRANSPARENT

        tvDontHaveAcc.setOnClickListener {
            startActivity(Intent(this@SiginActivity, RegistrationActivity::class.java))

        }

        loginBtn.setOnClickListener {
            checkCredential()
        }

        checkNotificationPermission()

    }

    private fun checkCredential() {

        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()

        when {
            email.isEmpty() || !email.contains("@gmail.com") -> {
                showError(inputEmail, "Email is not valid")
            }

            password.isEmpty() || password.length < 5 -> {
                showError(inputPassword, "Password must be 5 characters")
            }

            else -> {
                mLoadingDialog.setTitle("Login")
                mLoadingDialog.setMessage("Please wait while validating your credential")
                mLoadingDialog.setCanceledOnTouchOutside(false)
                mLoadingDialog.show()

                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        mLoadingDialog.dismiss()
                        startActivity(Intent(this@SiginActivity, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        mLoadingDialog.dismiss()
                        Toast.makeText(
                            this@SiginActivity,
                            "Incorrect username or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }


    private fun showError(input: EditText, message: String) {
        input.error = message
        input.requestFocus()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this@SiginActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showNotificationDialog()
        }
    }

    private fun showNotificationDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_notification)
        dialog.setCancelable(false)

        dialog.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }


        dialog.findViewById<ImageView>(R.id.close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<RelativeLayout>(R.id.allow_permission).setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                22
            )
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 22) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}