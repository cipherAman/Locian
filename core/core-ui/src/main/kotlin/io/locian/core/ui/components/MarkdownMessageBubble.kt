package io.locian.core.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.locian.core.ui.theme.CodeBlockBackgroundDark
import io.locian.core.ui.theme.CyanNeon

sealed interface MarkdownPart {
    data class TextPart(val text: String) : MarkdownPart
    data class CodeBlock(val language: String, val code: String) : MarkdownPart
}

fun parseMarkdown(content: String): List<MarkdownPart> {
    val parts = mutableListOf<MarkdownPart>()
    val lines = content.lines()
    var inCodeBlock = false
    var currentLanguage = ""
    val codeBuilder = StringBuilder()
    val textBuilder = StringBuilder()

    for (line in lines) {
        if (line.trimStart().startsWith("```")) {
            if (inCodeBlock) {
                // End code block
                parts.add(MarkdownPart.CodeBlock(currentLanguage, codeBuilder.toString().trimEnd()))
                codeBuilder.clear()
                currentLanguage = ""
                inCodeBlock = false
            } else {
                // Start code block
                if (textBuilder.isNotEmpty()) {
                    parts.add(MarkdownPart.TextPart(textBuilder.toString()))
                    textBuilder.clear()
                }
                currentLanguage = line.trimStart().removePrefix("```").trim()
                inCodeBlock = true
            }
        } else {
            if (inCodeBlock) {
                if (codeBuilder.isNotEmpty()) codeBuilder.append("\n")
                codeBuilder.append(line)
            } else {
                if (textBuilder.isNotEmpty()) textBuilder.append("\n")
                textBuilder.append(line)
            }
        }
    }

    if (inCodeBlock && codeBuilder.isNotEmpty()) {
        parts.add(MarkdownPart.CodeBlock(currentLanguage, codeBuilder.toString()))
    } else if (textBuilder.isNotEmpty()) {
        parts.add(MarkdownPart.TextPart(textBuilder.toString()))
    }

    return if (parts.isEmpty()) listOf(MarkdownPart.TextPart(content)) else parts
}

@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val parts = parseMarkdown(content)
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        parts.forEach { part ->
            when (part) {
                is MarkdownPart.TextPart -> {
                    Text(
                        text = part.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        lineHeight = 22.sp
                    )
                }
                is MarkdownPart.CodeBlock -> {
                    CodeBlockView(
                        language = part.language,
                        code = part.code,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Code", part.code))
                            Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                        }
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
    val displayLanguage = if (language.isNotBlank()) language else "code"
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CodeBlockBackgroundDark)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        // Code block header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayLanguage,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyanNeon.copy(alpha = 0.8f)
            )
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy code",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        // Code content
        Text(
            text = code,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
            color = Color(0xFFE2E8F0)
        )
    }
}
