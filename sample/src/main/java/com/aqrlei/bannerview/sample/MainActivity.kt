package com.aqrlei.bannerview.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.aqrlei.bannerview.sample.fragment.HomeFragment
import com.aqrlei.bannerview.sample.fragment.IndicatorFragment
import com.aqrlei.bannerview.sample.fragment.TransformerFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val pagerTitleArray = arrayOf("Home", "Transformer", "Indicator")

    private val pagerArray = arrayOf(HomeFragment.create(), TransformerFragment.create() ,IndicatorFragment.create())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val pager  = findViewById<ViewPager2>(R.id.pager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        pager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return pagerArray[position]
            }
            override fun getItemCount(): Int = pagerArray.size
        }

        TabLayoutMediator(tabLayout, pager) { tab , position ->
            tab.text = pagerTitleArray[position]
        }.attach()
    }
}