package com.quicksoft.testapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.quicksoft.testapp.adapter.BannerAdapter
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class DashboardFragment : Fragment() {

    private lateinit var view: View
    private lateinit var viewPager: ViewPager2
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        viewPager = view.findViewById(R.id.bannerViewPager)
        val dots = view.findViewById<DotsIndicator>(R.id.dotsIndicator)

        val images = listOf(
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3
        )

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