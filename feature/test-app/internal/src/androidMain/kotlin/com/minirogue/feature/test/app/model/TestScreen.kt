package com.minirogue.feature.test.app.model

import android.content.Context
import com.minirogue.feature.test.app.view.TestComposeActivity

public abstract class TestScreen private constructor() {
    abstract val screenName: String
    abstract fun launchScreen(context: Context)

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

        fun compose(screenName: String, navDestination: Any): TestScreen =
            TestCompose(screenName, navDestination)
    }
}
