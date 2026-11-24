package io.locian.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.locian.core.ui.R

/**
 * Renders the official Locian ribbon logo.
 */
@Composable
fun LocianLogo(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    contentDescription: String? = "Locian Logo"
) {
    Image(
        painter = painterResource(id = R.drawable.ic_locian_logo),
        contentDescription = contentDescription,
        modifier = modifier.size(size)
    )
}

/**
 * Renders the circular assistant avatar for chat messages, streaming bubbles, and branding.
 */
@Composable
fun LocianAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_locian_logo),
            contentDescription = "Locian",
            modifier = Modifier.size(size * 0.78f)
        )
    }
}
