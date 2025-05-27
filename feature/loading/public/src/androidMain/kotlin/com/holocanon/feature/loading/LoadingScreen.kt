package com.holocanon.feature.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource

private const val ROTATION_DURATION = 2000

@Composable
fun LoadingScreen() = Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(ROTATION_DURATION, easing = LinearEasing)),
    )
    Image(
        modifier = Modifier.rotate(rotation),
        painter = painterResource(R.drawable.common_resources_app_icon),
        contentDescription = "loading",
    )
}
