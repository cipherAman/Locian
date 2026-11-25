package io.locian.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.locian.core.ui.theme.CyanNeon
import kotlinx.coroutines.delay

/**
 * A clean, compact, and minimalist thinking component that seamlessly integrates
 * into the assistant message bubble without bulky nested cards or excessive spacing.
 *
 * Features:
 * - Ultra-compact inline disclosure (no heavy nested cards, frames, or dividers)
 * - Fluid chevron rotation animation (0° to 180°)
 * - Real-time thinking duration counter (e.g. "Thinking (3s)..." -> "Thought for 4s")
 * - 3-dot sequential wave animation while reasoning
 * - Minimal left quote line with tight padding for expanded reasoning
 * - Auto-expands while thinking, smoothly collapses when final response begins
 */
@Composable
fun ThinkingProcessCard(
    thinking: String,
    isStillThinking: Boolean,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    var userToggled by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(initialExpanded) }

    // Auto-expand during live thought generation, auto-collapse when answer begins
    LaunchedEffect(isStillThinking) {
        if (!userToggled) {
            if (isStillThinking) {
                isExpanded = true
            } else {
                delay(250)
                isExpanded = false
            }
        }
    }

    // Live duration counter
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(isStillThinking) {
        if (isStillThinking) {
            val startTime = System.currentTimeMillis()
            while (isStillThinking) {
                elapsedSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                delay(250)
            }
        }
    }

    // Smooth arrow rotation (0° to 180°)
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "arrowRotation"
    )

    // Breathing pulse for icon while thinking
    val infiniteTransition = rememberInfiniteTransition(label = "thinkingPulse")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    val headerTitle = when {
        isStillThinking && elapsedSeconds > 0 -> "Thinking (${elapsedSeconds}s)"
        isStillThinking -> "Thinking..."
        elapsedSeconds > 0 -> "Thought for ${elapsedSeconds}s"
        else -> "Thought process"
    }

    val outlineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            )
    ) {
        // Compact Inline Header
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    userToggled = true
                    isExpanded = !isExpanded
                }
                .padding(vertical = 3.dp, horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = if (isStillThinking) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(15.dp)
                    .graphicsLayer {
                        scaleX = if (isStillThinking) iconScale else 1f
                        scaleY = if (isStillThinking) iconScale else 1f
                    }
            )
            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = headerTitle,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isStillThinking) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isStillThinking) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontSize = 12.sp
            )

            // Sequential 3-dot wave while reasoning
            if (isStillThinking) {
                Spacer(modifier = Modifier.width(6.dp))
                ThinkingWaveDots(tint = CyanNeon)
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Smooth rotating chevron
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer { rotationZ = arrowRotation }
            )
        }

        // Compact Expanded Content (Clean left quote border)
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(180)) + expandVertically(tween(220, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(140)) + shrinkVertically(tween(180, easing = FastOutSlowInEasing))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp)
                    .drawBehind {
                        val strokePx = 1.5.dp.toPx()
                        drawLine(
                            color = if (isStillThinking) CyanNeon.copy(alpha = 0.45f) else outlineColor,
                            start = Offset(strokePx / 2f, 0f),
                            end = Offset(strokePx / 2f, size.height),
                            strokeWidth = strokePx
                        )
                    }
                    .padding(start = 10.dp)
            ) {
                if (thinking.isBlank() && isStillThinking) {
                    Text(
                        text = "Formulating reasoning chain...",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                } else {
                    MarkdownContent(
                        content = thinking,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f)
                    )
                }
            }
        }
    }
}

/**
 * A subtle, sequential 3-dot thinking wave animation.
 */
@Composable
private fun ThinkingWaveDots(
    tint: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "waveDots")

    val dot1Alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, delayMillis = 160, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, delayMillis = 320, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(3.5.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = dot1Alpha))
        )
        Box(
            modifier = Modifier
                .size(3.5.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = dot2Alpha))
        )
        Box(
            modifier = Modifier
                .size(3.5.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = dot3Alpha))
        )
    }
}
