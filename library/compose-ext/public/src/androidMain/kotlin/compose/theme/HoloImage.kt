package compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import holocanon.library.common_resources.public.generated.resources.Res
import holocanon.library.common_resources.public.generated.resources.common_resources_app_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun HoloImage(
    modifier: Modifier = Modifier,
    sourceUri: String?,
    contentDescription: String?,
    isNetworkAllowed: Boolean,
    scale: ContentScale = ContentScale.Fit,
    placeHolderResource: DrawableResource = Res.drawable.common_resources_app_icon,
    fallbackResource: DrawableResource = placeHolderResource,
    errorResource: DrawableResource = placeHolderResource,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(sourceUri)
            .networkCachePolicy(if (isNetworkAllowed) CachePolicy.ENABLED else CachePolicy.DISABLED)
            .build(),
        contentDescription = contentDescription,
        contentScale = scale,
        placeholder = painterResource(placeHolderResource),
        fallback = painterResource(fallbackResource),
        error = painterResource(errorResource),
    )
}
