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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.locian.core.ui.syntax.CodeSyntaxHighlighter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.locian.core.ui.theme.CodeBlockBackgroundDark
import io.locian.core.ui.theme.CyanNeon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed interface MarkdownElement {
    data class Header(val level: Int, val text: String) : MarkdownElement
    data class Paragraph(val text: String) : MarkdownElement
    data class BulletItem(val text: String, val level: Int = 0) : MarkdownElement
    data class NumberedItem(val number: String, val text: String) : MarkdownElement
    data class Blockquote(val text: String) : MarkdownElement
    data class CodeBlock(val language: String, val code: String) : MarkdownElement
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MarkdownElement
    data object Divider : MarkdownElement
}

fun parseMarkdownDocument(markdown: String): List<MarkdownElement> {
    val elements = mutableListOf<MarkdownElement>()
    val lines = markdown.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()

        // 1. Code Block
        if (trimmed.startsWith("```")) {
            val language = trimmed.removePrefix("```").trim()
            val codeBuilder = StringBuilder()
            i++
            while (i < lines.size && !lines[i].trim().startsWith("```")) {
                if (codeBuilder.isNotEmpty()) codeBuilder.append("\n")
                codeBuilder.append(lines[i])
                i++
            }
            if (i < lines.size) i++ // Skip closing ```
            elements.add(MarkdownElement.CodeBlock(language, codeBuilder.toString()))
            continue
        }

        // 2. Horizontal Divider
        if (trimmed == "---" || trimmed == "***" || trimmed == "___") {
            elements.add(MarkdownElement.Divider)
            i++
            continue
        }

        // 3. Headers (# H1, ## H2, ### H3, #### H4)
        if (trimmed.startsWith("#")) {
            val level = trimmed.takeWhile { it == '#' }.length.coerceIn(1, 4)
            val headerText = trimmed.drop(level).trim()
            if (headerText.isNotEmpty()) {
                elements.add(MarkdownElement.Header(level, headerText))
                i++
                continue
            }
        }

        // 4. Blockquotes (> quote)
        if (trimmed.startsWith(">")) {
            val quoteBuilder = StringBuilder()
            while (i < lines.size && lines[i].trim().startsWith(">")) {
                if (quoteBuilder.isNotEmpty()) quoteBuilder.append("\n")
                quoteBuilder.append(lines[i].trim().removePrefix(">").trim())
                i++
            }
            elements.add(MarkdownElement.Blockquote(quoteBuilder.toString()))
            continue
        }

        // 5. Tables (| col1 | col2 |)
        if (trimmed.startsWith("|") && trimmed.endsWith("|") && i + 1 < lines.size && lines[i + 1].trim().contains("---")) {
            val headers = trimmed.split("|").map { it.trim() }.filter { it.isNotEmpty() }
            i += 2 // skip header line and separator line
            val rows = mutableListOf<List<String>>()
            while (i < lines.size && lines[i].trim().startsWith("|") && lines[i].trim().endsWith("|")) {
                val rowCells = lines[i].trim().split("|").map { it.trim() }.filter { it.isNotEmpty() }
                rows.add(rowCells)
                i++
            }
            elements.add(MarkdownElement.Table(headers, rows))
            continue
        }

        // 6. Bullet lists (* or - or +)
        val bulletMatch = Regex("^([\\s]*)[*\\-+]\\s+(.*)").find(line)
        if (bulletMatch != null) {
            val indent = bulletMatch.groupValues[1].length / 2
            val text = bulletMatch.groupValues[2]
            elements.add(MarkdownElement.BulletItem(text, indent))
            i++
            continue
        }

        // 7. Numbered lists (1. 2. etc.)
        val numberedMatch = Regex("^([\\s]*)([0-9]+)[.)]\\s+(.*)").find(line)
        if (numberedMatch != null) {
            val number = numberedMatch.groupValues[2]
            val text = numberedMatch.groupValues[3]
            elements.add(MarkdownElement.NumberedItem(number, text))
            i++
            continue
        }

        // 8. Paragraph / regular text
        if (trimmed.isNotEmpty()) {
            val paragraphBuilder = StringBuilder(line)
            i++
            while (i < lines.size) {
                val nextTrimmed = lines[i].trim()
                if (nextTrimmed.isEmpty() ||
                    nextTrimmed.startsWith("```") ||
                    nextTrimmed.startsWith("#") ||
                    nextTrimmed.startsWith(">") ||
                    nextTrimmed.startsWith("|") ||
                    nextTrimmed.startsWith("* ") ||
                    nextTrimmed.startsWith("- ") ||
                    Regex("^[0-9]+[.)]\\s+").containsMatchIn(nextTrimmed) ||
                    nextTrimmed == "---"
                ) {
                    break
                }
                paragraphBuilder.append(" ").append(nextTrimmed)
                i++
            }
            elements.add(MarkdownElement.Paragraph(paragraphBuilder.toString()))
            continue
        }

        i++
    }

    return elements
}

/**
 * Builds AnnotatedString handling inline code (`code`), bold (**bold**),
 * italic (*italic*), and links.
 */
fun buildMarkdownInlineAnnotatedString(
    text: String,
    textColor: Color,
    codeBgColor: Color = Color(0xFF1E293B),
    codeTextColor: Color = CyanNeon
): AnnotatedString {
    return buildAnnotatedString {
        var index = 0
        while (index < text.length) {
            // 1. Inline code: `...`
            if (text[index] == '`') {
                val closeIndex = text.indexOf('`', index + 1)
                if (closeIndex != -1) {
                    val codeContent = text.substring(index + 1, closeIndex)
                    pushStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = codeBgColor,
                            color = codeTextColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    append(" $codeContent ")
                    pop()
                    index = closeIndex + 1
                    continue
                }
            }

            // 2. Bold italic: ***...***
            if (text.startsWith("***", index)) {
                val closeIndex = text.indexOf("***", index + 3)
                if (closeIndex != -1) {
                    val inner = text.substring(index + 3, closeIndex)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, color = textColor))
                    append(inner)
                    pop()
                    index = closeIndex + 3
                    continue
                }
            }

            // 3. Bold: **...** or __...__
            if (text.startsWith("**", index)) {
                val closeIndex = text.indexOf("**", index + 2)
                if (closeIndex != -1) {
                    val inner = text.substring(index + 2, closeIndex)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = textColor))
                    append(inner)
                    pop()
                    index = closeIndex + 2
                    continue
                }
            }

            // 4. Italic: *...* (avoid bullet starts or trailing)
            if (text[index] == '*' && (index == 0 || text[index - 1] != ' ') && index + 1 < text.length && text[index + 1] != ' ') {
                val closeIndex = text.indexOf('*', index + 1)
                if (closeIndex != -1 && closeIndex > index + 1) {
                    val inner = text.substring(index + 1, closeIndex)
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = textColor))
                    append(inner)
                    pop()
                    index = closeIndex + 1
                    continue
                }
            }

            // Default character
            append(text[index])
            index++
        }
    }
}

@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val elements = remember(content) { parseMarkdownDocument(content) }
    val context = LocalContext.current
    val codeBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        elements.forEach { element ->
            when (element) {
                is MarkdownElement.Header -> {
                    val (fontSize, fontWeight, topPadding) = when (element.level) {
                        1 -> Triple(20.sp, FontWeight.Bold, 8.dp)
                        2 -> Triple(18.sp, FontWeight.Bold, 6.dp)
                        3 -> Triple(16.sp, FontWeight.SemiBold, 4.dp)
                        else -> Triple(15.sp, FontWeight.SemiBold, 2.dp)
                    }
                    Column(modifier = Modifier.padding(top = topPadding)) {
                        Text(
                            text = buildMarkdownInlineAnnotatedString(element.text, textColor, codeBgColor = codeBgColor),
                            fontSize = fontSize,
                            fontWeight = fontWeight,
                            color = if (element.level == 1) CyanNeon else textColor,
                            lineHeight = (fontSize.value + 6).sp
                        )
                        if (element.level == 1) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .width(40.dp)
                                    .height(2.dp)
                                    .background(CyanNeon, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }

                is MarkdownElement.Paragraph -> {
                    Text(
                        text = buildMarkdownInlineAnnotatedString(element.text, textColor, codeBgColor = codeBgColor),
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        lineHeight = 22.sp
                    )
                }

                is MarkdownElement.BulletItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = (element.level * 16).dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            color = CyanNeon,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp, top = 1.dp)
                        )
                        Text(
                            text = buildMarkdownInlineAnnotatedString(element.text, textColor, codeBgColor = codeBgColor),
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor,
                            lineHeight = 21.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is MarkdownElement.NumberedItem -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${element.number}.",
                            color = CyanNeon,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                        )
                        Text(
                            text = buildMarkdownInlineAnnotatedString(element.text, textColor, codeBgColor = codeBgColor),
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor,
                            lineHeight = 21.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                is MarkdownElement.Blockquote -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(vertical = 6.dp, horizontal = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(24.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CyanNeon)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = buildMarkdownInlineAnnotatedString(element.text, textColor, codeBgColor = codeBgColor),
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            color = textColor.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }
                }

                is MarkdownElement.CodeBlock -> {
                    CodeBlockView(
                        language = element.language,
                        code = element.code,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Code", element.code))
                            Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                is MarkdownElement.Table -> {
                    MarkdownTableView(table = element, textColor = textColor)
                }

                is MarkdownElement.Divider -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun CodeBlockView(
    language: String,
    code: String,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayLanguage = if (language.isNotBlank()) language.uppercase() else "CODE"
    var isCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val highlightedCode = remember(code, language) {
        CodeSyntaxHighlighter.highlight(code, language)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CodeBlockBackgroundDark)
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                RoundedCornerShape(10.dp)
            )
    ) {
        // Code block header (Window titlebar style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF161B22))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFF5F56).copy(alpha = 0.85f)))
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFFFBD2E).copy(alpha = 0.85f)))
                Spacer(modifier = Modifier.width(5.dp))
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF27C93F).copy(alpha = 0.85f)))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = displayLanguage,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CyanNeon,
                    letterSpacing = 1.sp
                )
            }

            IconButton(
                onClick = {
                    onCopy()
                    isCopied = true
                    scope.launch {
                        delay(2000)
                        isCopied = false
                    }
                },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = if (isCopied) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFF21262D))

        // Code content with horizontal scrolling for long lines
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = highlightedCode,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.5.sp,
                    lineHeight = 19.sp
                )
            )
        }
    }
}

@Composable
fun MarkdownTableView(
    table: MarkdownElement.Table,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .horizontalScroll(scrollState)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                table.headers.forEach { header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = CyanNeon,
                        modifier = Modifier
                            .width(120.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            // Body Rows
            table.rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .background(if (index % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        .padding(vertical = 6.dp, horizontal = 12.dp)
                ) {
                    row.forEach { cell ->
                        Text(
                            text = cell,
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor,
                            modifier = Modifier
                                .width(120.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
