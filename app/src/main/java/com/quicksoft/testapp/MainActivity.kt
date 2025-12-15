package com.quicksoft.testapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quicksoft.testapp.adapter.SideAdapter
import com.quicksoft.testapp.helper.ApiManager
import com.quicksoft.testapp.model.SideItems
import com.quicksoft.testapp.model.UserInfo
import com.quicksoft.testapp.model.apidata
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {

    private lateinit var profilename: AppCompatTextView
    private lateinit var profileemail: AppCompatTextView
    private lateinit var smoothBottomBar: SmoothBottomBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true // false = white icons
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        recyclerview = findViewById(R.id.recyclerview)
        drawerLayout = findViewById(R.id.main)
        smoothBottomBar = findViewById(R.id.smoothBottomBar)
        profilename = findViewById(R.id.profilename)
        profileemail = findViewById(R.id.profileemail)

        loadFragment(DashboardFragment())
        smoothBottomBar.setOnItemSelectedListener { item ->
            when (item) {
                R.id.home -> {
                    loadFragment(DashboardFragment())
                }
            }
        }

        findViewById<View>(R.id.person).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            // open profile
        }

        ApiManager().fetchApiData(
            this@MainActivity, "", "","","",
            object : ApiManager.FeedApiCallBack {
                @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
                override fun onSuccess(response: apidata) {
                    // Extract the user object
                    val user = response.data
                    if (user != null) {

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Api Called Using Retrofit But No data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(str: String) {
                    Toast.makeText(this@MainActivity, str, Toast.LENGTH_SHORT).show()
                    return
                }
            })

        val mAuth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        mAuth.currentUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userInfo = document.toObject(UserInfo::class.java)
                        userInfo?.let {
                            profilename.text = it.name
                            profileemail.text = it.email
                        }

                    } else {
                        Log.e("Firestore", "User document does not exist")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching user", e)
                }
        }

        val list = listOf(
            SideItems("Profile", R.drawable.person),
            SideItems("KYC", R.drawable.card) ,
            SideItems("Support", R.drawable.whatsappsupport),
            SideItems("Settings", R.drawable.settlements),
            SideItems("Terms & Conditions", R.drawable.terms),
            SideItems("Privacy", R.drawable.privacy),
            SideItems("FAQ", R.drawable.faq),
            SideItems("Share", R.drawable.send),
            SideItems("Logout", R.drawable.logout),
        )

        recyclerview.layoutManager = LinearLayoutManager(this@MainActivity)
        recyclerview.adapter = SideAdapter(list)


    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .setReorderingAllowed(true)
            .commit()
    }
}