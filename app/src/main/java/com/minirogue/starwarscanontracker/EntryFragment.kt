package com.minirogue.starwarscanontracker


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_entry.view.*

class EntryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragView = inflater.inflate(R.layout.fragment_entry, container, false)
        fragView.welcome_textview.movementMethod = LinkMovementMethod.getInstance()
        return fragView
    }


}
