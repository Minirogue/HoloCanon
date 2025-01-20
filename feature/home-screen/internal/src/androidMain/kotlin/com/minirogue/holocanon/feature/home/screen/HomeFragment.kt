package com.minirogue.holocanon.feature.home.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.holocanon.feature.home.screen.internal.R
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                HolocanonTheme {
                    HomeScreen()
                }
            }
        }

    @Composable
    fun HomeScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.home_screen_app_icon),
                contentDescription = getString(R.string.home_screen_app_icon_description),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            val welcomeString = getWelcomeString()
            val uriHandler = LocalUriHandler.current
            ClickableText(
                text = welcomeString,
                style = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurface),
                onClick = {
                    welcomeString
                        .getStringAnnotations(STRING_ANNOTATION_TAG, it, it)
                        .firstOrNull()?.let { stringAnnotation ->
                            uriHandler.openUri(stringAnnotation.item)
                        }
                }
            )
            Spacer(modifier = Modifier.heightIn(min = 12.dp, max = 48.dp))
            Text(
                text = getString(R.string.home_screen_copyright_notice),
                style = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurface, fontSize = 10.sp)
            )
        }
    }

    fun getWelcomeString(): AnnotatedString = buildAnnotatedString {
        val discordString = getString(R.string.home_screen_discord)
        val fullString =
            String.format(getString(R.string.home_screen_welcome_message), discordString)
        val startIndex = fullString.indexOf(discordString)
        val endIndex = startIndex + discordString.length
        append(fullString)
        addStyle(
            style = SpanStyle(
                color = LINK_COLOR,
                textDecoration = TextDecoration.Underline
            ),
            start = startIndex,
            end = endIndex,
        )
        addStringAnnotation(
            tag = STRING_ANNOTATION_TAG,
            annotation = "https://discord.gg/RxXvTfX",
            start = startIndex,
            end = endIndex
        )
    }

    companion object {
        private const val STRING_ANNOTATION_TAG = "URL"
        private val LINK_COLOR = Color(0xff64B5F6)
    }
}
