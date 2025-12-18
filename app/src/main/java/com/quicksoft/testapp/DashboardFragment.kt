package com.quicksoft.testapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.quicksoft.testapp.adapter.BannerAdapter
import com.razorpay.Checkout
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import org.json.JSONObject
import java.util.concurrent.Executors

class DashboardFragment : Fragment() {

    private lateinit var view: View
    private lateinit var viewPager: ViewPager2
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var card1: MaterialCardView
    private val executorService = Executors.newSingleThreadExecutor()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        card1 = view.findViewById(R.id.card1)
        viewPager = view.findViewById(R.id.bannerViewPager)
        val dots = view.findViewById<DotsIndicator>(R.id.dotsIndicator)

        val images = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )

        card1.setOnClickListener {
            val amountInPaise = 100 * 100 // ₹100

            val checkout = Checkout()
            checkout.setKeyID("rzp_test_Rs0MPeZheCKmln")

            val options = JSONObject().apply {
                put("name", "Test Payment")
                put("description", "Test Payment")
                put("currency", "INR")
                put("amount", amountInPaise)

                val prefill = JSONObject().apply {
                    put("email", "test@gmail.com")
                    put("contact", "9999999999")
                }
                put("prefill", prefill)
            }

            checkout.open(requireActivity(), options)
        }
        val adapter = BannerAdapter(images)
        viewPager.adapter = adapter
        dots.attachTo(viewPager)

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            val next = (viewPager.currentItem + 1) % images.size
            viewPager.currentItem = next
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 6000)
            }
        })

        return view;
    }


    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 6000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

}