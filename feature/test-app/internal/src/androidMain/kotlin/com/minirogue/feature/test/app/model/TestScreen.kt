package com.minirogue.feature.test.app.model

import android.content.Context
import androidx.fragment.app.Fragment
import com.minirogue.feature.test.app.view.TestComposeActivity
import com.minirogue.feature.test.app.view.TestFragmentActivity

public abstract class TestScreen private constructor() {
    abstract val screenName: String
    abstract fun launchScreen(context: Context)

    private class TestFragment(
        override val screenName: String,
        private val createFragment: (Context) -> Fragment,
    ) : TestScreen() {
        override fun launchScreen(context: Context) {
            context.startActivity(TestFragmentActivity.newIntent(context, createFragment))
        }
    }

    private class TestCompose(
        override val screenName: String,
        private val navDestination: Any,
    ) : TestScreen() {
        override fun launchScreen(context: Context) {
            context.startActivity(
                TestComposeActivity.newIntent(
                    context,
                    screenName,
                    navDestination,
                ),
            )
        }
    }

    companion object {
        fun fragment(
            screenName: String,
            createFragment: (context: Context) -> Fragment,
        ): TestScreen {
            return TestFragment(screenName, createFragment)
        }

        fun compose(screenName: String, navDestination: Any): TestScreen =
            TestCompose(screenName, navDestination)
    }
}
