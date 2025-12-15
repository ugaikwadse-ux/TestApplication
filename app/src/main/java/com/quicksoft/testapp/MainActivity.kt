package com.quicksoft.testapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quicksoft.testapp.adapter.SideAdapter
import com.quicksoft.testapp.model.SideItems
import com.quicksoft.testapp.model.UserInfo
import com.razorpay.PaymentResultListener
import me.ibrahimsn.lib.SmoothBottomBar
import java.util.Locale

class MainActivity : AppCompatActivity() , PaymentResultListener {

    private lateinit var profilename: AppCompatTextView
    private lateinit var profileemail: AppCompatTextView
    private lateinit var smoothBottomBar: SmoothBottomBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerview: RecyclerView
    private lateinit var addressofuser: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true // false = white icons
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)

        addressofuser = findViewById(R.id.addressofuser)
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

                            getAddressFromLatLng(it.latitude?: 0.0, it.longitude?:0.0) { address ->
                                addressofuser.text = address
                            }
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

    private fun sendLocalFcmNotification(title: String, message: String) {

        val channelId = "default_channel"

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }


    override fun onPaymentSuccess(p0: String?) {
        sendLocalFcmNotification(
            title = "Payment Successful 🎉",
            message = "Your payment has been completed successfully"
        )
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        sendLocalFcmNotification(
            title = "Payment Failed!!",
            message = "Payment has been failed due to $p1"
        )
    }

    fun getAddressFromLatLng(latitude: Double, longitude: Double, callback: (String) -> Unit) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<android.location.Address>) {
                        if (addresses.isNotEmpty()) {
                            callback(addresses[0].getAddressLine(0))
                        } else {
                            callback("Address not found")
                        }
                    }
                })
            } else {
                // Fallback for older devices
                Thread {
                    try {
                        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                        val address = if (!addresses.isNullOrEmpty()) {
                            addresses[0].getAddressLine(0)
                        } else {
                            "Address not found"
                        }
                        runOnUiThread { callback(address) }
                    } catch (e: Exception) {
                        Log.e("GeocoderError", e.message ?: "Error getting address")
                        runOnUiThread { callback("Error getting address") }
                    }
                }.start()
            }

        } catch (e: Exception) {
            Log.e("GeocoderError", e.message ?: "Error getting address")
            callback("Error getting address")
        }
    }
}