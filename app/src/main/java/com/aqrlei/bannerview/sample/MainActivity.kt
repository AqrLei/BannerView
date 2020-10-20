package com.aqrlei.bannerview.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aqrlei.bannerview.sample.fragment.HomeFragment
import com.aqrlei.bannerview.sample.fragment.IndicatorFragment
import com.aqrlei.bannerview.sample.fragment.TransformerFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val pagerTitleArray = arrayOf("Home", "Transformer", "Indicator")

    private val pagerArray = arrayOf(HomeFragment.create(), TransformerFragment.create() ,IndicatorFragment.create())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return pagerArray[position]
            }
            override fun getItemCount(): Int = pagerArray.size
        }
        pager.requestDisallowInterceptTouchEvent(false)

        TabLayoutMediator(tabLayout, pager) { tab , position ->
            tab.text = pagerTitleArray[position]
        }.attach()
    }
}