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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.locian.core.ui.R

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale

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
        contentScale = ContentScale.Fit,
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
    backgroundColor: Color = Color(0xFFCDD5DE),
    border: BorderStroke? = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(if (border != null) Modifier.border(border, CircleShape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_locian_logo),
            contentDescription = "Locian",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size * 0.72f)
        )
    }
}

/**
 * Renders the official Locian full wordmark logo ("LOCIAn").
 * Automatically displays the dark or light variant based on the theme background luminance.
 */
@Composable
fun LocianFullLogo(
    modifier: Modifier = Modifier,
    height: Dp = 32.dp,
    contentDescription: String? = "Locian"
) {
    val isLight = MaterialTheme.colorScheme.background.luminance() > 0.5f
    val logoRes = if (isLight) R.drawable.ic_locian_full_logo_light else R.drawable.ic_locian_full_logo_dark

    Image(
        painter = painterResource(id = logoRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier.height(height)
    )
}
