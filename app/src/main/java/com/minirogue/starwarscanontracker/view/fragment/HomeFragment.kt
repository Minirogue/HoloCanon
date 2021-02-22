package com.minirogue.starwarscanontracker.view.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.minirogue.starwarscanontracker.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        // make the links in welcome_textview clickable
        fragmentBinding.welcomeTextview.movementMethod = LinkMovementMethod.getInstance()
        return fragmentBinding.root
    }
}
