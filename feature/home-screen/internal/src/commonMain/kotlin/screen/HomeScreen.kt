package screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import holocanon.feature.home_screen.internal.generated.resources.Res
import holocanon.feature.home_screen.internal.generated.resources.home_screen_app_icon
import holocanon.feature.home_screen.internal.generated.resources.home_screen_app_icon_description
import holocanon.feature.home_screen.internal.generated.resources.home_screen_copyright_notice
import holocanon.feature.home_screen.internal.generated.resources.home_screen_discord
import holocanon.feature.home_screen.internal.generated.resources.home_screen_welcome_message
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val LINK_COLOR = Color(0xff64B5F6) // TODO move to theme

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.home_screen_app_icon),
            contentDescription = stringResource(Res.string.home_screen_app_icon_description),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
        )
        val welcomeString = getWelcomeString()
        Text(
            text = welcomeString,
            style = TextStyle.Default.copy(color = MaterialTheme.colorScheme.onSurface),
        )
        Text(
            text = stringResource(Res.string.home_screen_copyright_notice),
            style = TextStyle.Default.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 10.sp,
            ),
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}

@Composable
fun getWelcomeString(): AnnotatedString = buildAnnotatedString {
    val discordString = stringResource(Res.string.home_screen_discord)
    val fullString = stringResource(Res.string.home_screen_welcome_message, discordString).trimMargin()
    val startIndex = fullString.indexOf(discordString)
    val endIndex = startIndex + discordString.length
    append(fullString)
    addStyle(
        style = SpanStyle(
            color = LINK_COLOR,
            textDecoration = TextDecoration.Underline,
        ),
        start = startIndex,
        end = endIndex,
    )
    addLink(LinkAnnotation.Url("https://discord.gg/RxXvTfX"), startIndex, endIndex)
}
