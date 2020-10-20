package com.aqrlei.bannerview.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aqrlei.bannerview.sample.R

/**
 * created by AqrLei on 2020/10/20
 */
class TransformerFragment : Fragment() {

    companion object {
        fun create()  = TransformerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pager_transformer, container, false)
    }
}