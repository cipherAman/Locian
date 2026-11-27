// SPDX-License-Identifier: Apache-2.0
// Copyright (c) [YEAR] [COPYRIGHT HOLDER]. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.locian.core.ui.theme.CyanNeon
import kotlinx.coroutines.delay

/**
 * A clean, completely borderless, inline collapsible thinking disclosure.
 *
 * - No box, card frames, or border outlines
 * - Collapsed by default (click to expand)
 * - Fluid 180° chevron rotation animation
 * - Live elapsed thinking duration counter
 * - Subtle 3-dot thinking wave pulse while active
 */
@Composable
fun ThinkingProcessCard(
    thinking: String,
    isStillThinking: Boolean,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    // Collapsed by default
    var isExpanded by remember { mutableStateOf(initialExpanded) }

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

    // Smooth chevron rotation (0° to 180°)
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            )
    ) {
        // Pure inline clickable header row (no box, no card frame)
        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isExpanded = !isExpanded
                }
                .padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = if (isStillThinking) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
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
                color = if (isStillThinking) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(15.dp)
                    .graphicsLayer { rotationZ = arrowRotation }
            )
        }

        // Expanded Reasoning Content (completely clean, no box, no lines)
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(tween(180)) + expandVertically(tween(220, easing = FastOutSlowInEasing)),
            exit = fadeOut(tween(140)) + shrinkVertically(tween(180, easing = FastOutSlowInEasing))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 4.dp)
            ) {
                if (thinking.isBlank() && isStillThinking) {
                    Text(
                        text = "Thinking...",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                } else {
                    MarkdownContent(
                        content = thinking,
                        textColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
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
