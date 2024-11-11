package com.minirogue.feature.test.app.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.minirogue.holocanon.feature.test.app.internal.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestFragmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_app_fragment_activity)
        val fragment = createFragment?.invoke(this)
        requireNotNull(fragment)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }

    companion object {
        private var createFragment: ((context: Context) -> Fragment)? = null
        fun newIntent(context: Context, createFragment: (context: Context) -> Fragment): Intent =
            Intent(context, TestFragmentActivity::class.java)
                .also { Companion.createFragment = createFragment}
    }
}
