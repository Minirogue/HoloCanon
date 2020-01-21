package com.minirogue.starwarscanontracker.view.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.minirogue.starwarscanontracker.R

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_about, container, false)
        //make the links in welcome_textview clickable
        //fragView.welcome_textview.movementMethod = LinkMovementMethod.getInstance()
        return fragView
    }


}
