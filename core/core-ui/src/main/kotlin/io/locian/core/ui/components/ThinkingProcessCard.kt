package io.locian.core.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.locian.core.ui.theme.CyanNeon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A clean, modern, and professional collapsible card that displays the model's
 * internal reasoning / thought process (e.g. DeepSeek R1, Qwen3 thinking mode).
 *
 * Features:
 * - Fluid chevron rotation animation
 * - Live reasoning duration timer (e.g. "Thinking (3s)..." -> "Thought for 5s")
 * - 3-dot thinking wave pulse indicator while reasoning
 * - Left accent guide rail for easy reading of internal monologue
 * - Auto-expands while thinking, cleanly collapses when answer begins (with manual tap override)
 * - Copy action with animated feedback and word count
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

    // If user hasn't manually clicked:
    // Auto-expand while thinking so live thoughts are visible.
    // Auto-collapse when done thinking so final answer takes focus.
    LaunchedEffect(isStillThinking) {
        if (!userToggled) {
            if (isStillThinking) {
                isExpanded = true
            } else {
                // When finished thinking, smoothly auto-collapse after a brief delay
                delay(300)
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

    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Smooth arrow rotation animation (0° to 180°)
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "arrowRotation"
    )

    // Breathing pulse for thinking border & icon
    val infiniteTransition = rememberInfiniteTransition(label = "thinkingPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    val cardShape = RoundedCornerShape(12.dp)
    val headerTitle = when {
        isStillThinking && elapsedSeconds > 0 -> "Thinking (${elapsedSeconds}s)"
        isStillThinking -> "Thinking..."
        elapsedSeconds > 0 -> "Thought for ${elapsedSeconds}s"
        else -> "Thought process"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(
                if (isStillThinking) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f)
                }
            )
            .border(
                width = 0.75.dp,
                brush = if (isStillThinking) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            CyanNeon.copy(alpha = pulseAlpha),
                            CyanNeon.copy(alpha = pulseAlpha * 0.4f),
                            Color.Transparent
                        )
                    )
                } else {
                    SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                },
                shape = cardShape
            )
            .animateContentSize(
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            )
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    userToggled = true
                    isExpanded = !isExpanded
                }
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brain / Reasoning Icon with breathing animation
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = if (isStillThinking) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                modifier = Modifier
                    .size(17.dp)
                    .graphicsLayer {
                        scaleX = if (isStillThinking) iconScale else 1f
                        scaleY = if (isStillThinking) iconScale else 1f
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = headerTitle,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isStillThinking) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isStillThinking) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 3-dot thinking wave pulse when active
            if (isStillThinking) {
                Spacer(modifier = Modifier.width(8.dp))
                ThinkingWaveDots(tint = CyanNeon)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Smoothly rotating Chevron Arrow
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier
                    .size(19.dp)
                    .graphicsLayer { rotationZ = arrowRotation }
            )
        }

        // Expanded Reasoning Body
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(220)) + expandVertically(tween(260, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(180)) + shrinkVertically(tween(220, easing = FastOutSlowInEasing))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 10.dp)
            ) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Left guide rail alongside thought stream
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.5.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isStillThinking) {
                                    Brush.verticalGradient(
                                        listOf(
                                            CyanNeon.copy(alpha = 0.75f),
                                            CyanNeon.copy(alpha = 0.2f)
                                        )
                                    )
                                } else {
                                    SolidColor(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                                }
                            )
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (thinking.isBlank() && isStillThinking) {
                            Text(
                                text = "Formulating reasoning chain...",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 12.5.sp
                            )
                        } else {
                            MarkdownContent(
                                content = thinking,
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.88f)
                            )
                        }
                    }
                }

                // Footer: Word count & Copy Action
                if (thinking.isNotBlank()) {
                    val wordCount = remember(thinking) {
                        thinking.split(Regex("\\s+")).count { it.isNotBlank() }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$wordCount words",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                            fontSize = 11.sp
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Thought Process", thinking))
                                isCopied = true
                                Toast.makeText(context, "Thought process copied", Toast.LENGTH_SHORT).show()
                                scope.launch {
                                    delay(2000)
                                    isCopied = false
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy thinking",
                                tint = if (isCopied) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(13.5.dp)
                            )
                        }
                    }
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
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, delayMillis = 180, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, delayMillis = 360, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = dot1Alpha))
        )
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = dot2Alpha))
        )
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = dot3Alpha))
        )
    }
}
