// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Aman Kumar. All rights reserved.
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

package io.locian.core.ui.syntax

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Modern code editor theme palette inspired by One Dark & VS Code Dark+.
 */
object SyntaxTheme {
    val Keyword = Color(0xFFC678DD)       // Vibrant Purple (def, class, fun, return, if, else, async, await)
    val Function = Color(0xFF61AFEF)      // Sky Blue (print, sleep, enqueue_task, main)
    val Type = Color(0xFFE5C07B)          // Golden Amber (TaskQueue, String, Int, User)
    val String = Color(0xFF98C379)        // Spring Green ("Task 1", 'hello')
    val Number = Color(0xFFD19A66)        // Peach Orange (1, 2, 0.5, 100)
    val Comment = Color(0xFF7F848E)       // Muted Slate Gray (italic)
    val Constant = Color(0xFFE5C07B)      // Amber (True, False, None, null, nil)
    val Special = Color(0xFFE06C75)       // Soft Coral (self, this, super, cls)
    val Operator = Color(0xFF56B6C2)      // Cyan / Aqua (=, +, -, ->, =>)
    val Annotation = Color(0xFFD19A66)    // Peach (@staticmethod, @Composable)
    val Variable = Color(0xFFE06C75)      // Coral ($var in bash)
    val Default = Color(0xFFABB2BF)       // Clean Off-White
}

/**
 * High-performance, O(N) tokenizer and syntax highlighter for Jetpack Compose AnnotatedString.
 */
object CodeSyntaxHighlighter {

    private val pythonKeywords = setOf(
        "def", "class", "import", "from", "as", "return", "if", "elif", "else",
        "while", "for", "in", "try", "except", "finally", "raise", "with",
        "async", "await", "yield", "lambda", "pass", "break", "continue",
        "global", "nonlocal", "assert", "is", "not", "and", "or", "match", "case"
    )

    private val kotlinKeywords = setOf(
        "fun", "val", "var", "class", "interface", "object", "enum", "sealed", "data",
        "package", "import", "return", "if", "else", "when", "while", "for", "in",
        "try", "catch", "finally", "throw", "public", "private", "protected", "internal",
        "override", "open", "abstract", "final", "suspend", "inline", "reified",
        "companion", "by", "is", "as", "typealias", "constructor", "init", "where",
        "actual", "expect", "annotation", "crossinline", "noinline", "tailrec", "operator"
    )

    private val javaKeywords = setOf(
        "public", "private", "protected", "class", "interface", "enum", "extends",
        "implements", "static", "final", "void", "return", "if", "else", "switch",
        "case", "default", "while", "for", "do", "break", "continue", "try", "catch",
        "finally", "throw", "throws", "new", "package", "import", "instanceof", "synchronized",
        "volatile", "transient", "native", "abstract", "strictfp", "assert", "record"
    )

    private val jsKeywords = setOf(
        "function", "const", "let", "var", "class", "interface", "type", "enum",
        "export", "import", "from", "default", "return", "if", "else", "switch",
        "case", "break", "while", "for", "of", "in", "try", "catch", "finally",
        "throw", "async", "await", "yield", "new", "typeof", "instanceof", "void",
        "delete", "extends", "implements", "public", "private", "protected", "static",
        "readonly", "as", "is", "keyof", "debugger"
    )

    private val rustKeywords = setOf(
        "fn", "let", "mut", "struct", "enum", "trait", "impl", "pub", "use", "mod",
        "return", "if", "else", "match", "while", "loop", "for", "in", "break",
        "continue", "async", "await", "move", "where", "type", "unsafe", "const",
        "static", "dyn", "ref", "extern", "crate", "as", "box"
    )

    private val goKeywords = setOf(
        "func", "package", "import", "type", "struct", "interface", "var", "const",
        "return", "if", "else", "switch", "case", "default", "for", "range",
        "break", "continue", "go", "defer", "select", "chan", "map", "goto", "fallthrough"
    )

    private val cppKeywords = setOf(
        "int", "float", "double", "char", "void", "bool", "auto", "struct", "class",
        "enum", "union", "public", "private", "protected", "virtual", "override",
        "template", "typename", "namespace", "using", "return", "if", "else",
        "switch", "case", "while", "for", "do", "break", "continue", "try", "catch",
        "throw", "new", "delete", "const", "static", "inline", "constexpr", "sizeof",
        "typedef", "extern", "explicit", "mutable"
    )

    private val sqlKeywords = setOf(
        "SELECT", "FROM", "WHERE", "INSERT", "INTO", "UPDATE", "DELETE", "CREATE",
        "TABLE", "DROP", "ALTER", "JOIN", "LEFT", "RIGHT", "INNER", "OUTER", "ON",
        "GROUP", "BY", "ORDER", "ASC", "DESC", "HAVING", "LIMIT", "OFFSET", "UNION",
        "ALL", "AS", "AND", "OR", "NOT", "IN", "IS", "NULL", "LIKE", "CASE", "WHEN",
        "THEN", "ELSE", "END", "EXISTS", "BETWEEN", "COUNT", "SUM", "AVG", "MIN",
        "MAX", "DISTINCT", "PRIMARY", "KEY", "FOREIGN", "REFERENCES", "INDEX",
        "VIEW", "DATABASE", "SCHEMA", "VALUES", "SET", "DEFAULT", "WITH"
    )

    private val commonKeywords = pythonKeywords + kotlinKeywords + javaKeywords + jsKeywords + rustKeywords + goKeywords + cppKeywords

    private val builtins = setOf(
        "true", "false", "null", "nil", "None", "True", "False", "undefined", "NaN",
        "self", "this", "super", "cls", "it", "Some", "Ok", "Err", "iota"
    )

    /**
     * Highlights code snippet and returns an [AnnotatedString] styled like a code editor.
     */
    fun highlight(code: String, language: String): AnnotatedString {
        val lang = language.lowercase().trim()
        val keywords = when {
            lang in listOf("python", "py") -> pythonKeywords
            lang in listOf("kotlin", "kt") -> kotlinKeywords
            lang in listOf("java") -> javaKeywords
            lang in listOf("javascript", "typescript", "js", "ts", "jsx", "tsx") -> jsKeywords
            lang in listOf("rust", "rs") -> rustKeywords
            lang in listOf("go", "golang") -> goKeywords
            lang in listOf("c", "cpp", "cxx", "h", "hpp") -> cppKeywords
            lang in listOf("sql") -> sqlKeywords
            else -> commonKeywords
        }

        val isSql = lang == "sql"
        val isPythonOrShell = lang in listOf("python", "py", "bash", "sh", "zsh", "shell")

        return buildAnnotatedString {
            var i = 0
            val n = code.length

            while (i < n) {
                val c = code[i]

                // 1. Single-line comments (# or // or --)
                if ((isPythonOrShell && c == '#') ||
                    (code.startsWith("//", i)) ||
                    (isSql && code.startsWith("--", i))
                ) {
                    val end = code.indexOf('\n', i).let { if (it == -1) n else it }
                    pushStyle(SpanStyle(color = SyntaxTheme.Comment, fontStyle = FontStyle.Italic))
                    append(code.substring(i, end))
                    pop()
                    i = end
                    continue
                }

                // 2. Multi-line comments (/* ... */ or HTML <!-- ... -->)
                if (code.startsWith("/*", i)) {
                    val end = code.indexOf("*/", i + 2).let { if (it == -1) n else it + 2 }
                    pushStyle(SpanStyle(color = SyntaxTheme.Comment, fontStyle = FontStyle.Italic))
                    append(code.substring(i, end))
                    pop()
                    i = end
                    continue
                }

                if (code.startsWith("<!--", i)) {
                    val end = code.indexOf("-->", i + 4).let { if (it == -1) n else it + 3 }
                    pushStyle(SpanStyle(color = SyntaxTheme.Comment, fontStyle = FontStyle.Italic))
                    append(code.substring(i, end))
                    pop()
                    i = end
                    continue
                }

                // 3. Triple quoted strings (""" or ''')
                if (code.startsWith("\"\"\"", i) || code.startsWith("'''", i)) {
                    val delim = code.substring(i, i + 3)
                    val end = code.indexOf(delim, i + 3).let { if (it == -1) n else it + 3 }
                    pushStyle(SpanStyle(color = SyntaxTheme.String))
                    append(code.substring(i, end))
                    pop()
                    i = end
                    continue
                }

                // 4. Standard strings ("..." or '...' or `...`)
                if (c == '"' || c == '\'' || c == '`') {
                    val quote = c
                    var j = i + 1
                    var escaped = false
                    while (j < n) {
                        val ch = code[j]
                        if (ch == '\\' && !escaped) {
                            escaped = true
                        } else if (ch == quote && !escaped) {
                            j++
                            break
                        } else if (ch == '\n' && quote != '`') {
                            // Unclosed single line string
                            break
                        } else {
                            escaped = false
                        }
                        j++
                    }
                    pushStyle(SpanStyle(color = SyntaxTheme.String))
                    append(code.substring(i, j))
                    pop()
                    i = j
                    continue
                }

                // 5. Annotations / Decorators (@Composable, @override, @staticmethod)
                if (c == '@' && i + 1 < n && (code[i + 1].isLetter() || code[i + 1] == '_')) {
                    var j = i + 1
                    while (j < n && (code[j].isLetterOrDigit() || code[j] == '_' || code[j] == '.')) {
                        j++
                    }
                    pushStyle(SpanStyle(color = SyntaxTheme.Annotation, fontWeight = FontWeight.Medium))
                    append(code.substring(i, j))
                    pop()
                    i = j
                    continue
                }

                // 6. Shell variables ($var or ${var})
                if (c == '$' && i + 1 < n && (code[i + 1].isLetter() || code[i + 1] == '{' || code[i + 1] == '_')) {
                    var j = i + 1
                    if (code[j] == '{') {
                        val close = code.indexOf('}', j)
                        j = if (close == -1) n else close + 1
                    } else {
                        while (j < n && (code[j].isLetterOrDigit() || code[j] == '_')) j++
                    }
                    pushStyle(SpanStyle(color = SyntaxTheme.Variable))
                    append(code.substring(i, j))
                    pop()
                    i = j
                    continue
                }

                // 7. Numbers (0x..., 123, 45.67)
                if (c.isDigit() && (i == 0 || !code[i - 1].isLetterOrDigit())) {
                    var j = i
                    if (code.startsWith("0x", i, ignoreCase = true) || code.startsWith("0b", i, ignoreCase = true)) {
                        j += 2
                    }
                    while (j < n && (code[j].isLetterOrDigit() || code[j] == '.')) {
                        j++
                    }
                    pushStyle(SpanStyle(color = SyntaxTheme.Number))
                    append(code.substring(i, j))
                    pop()
                    i = j
                    continue
                }

                // 8. Identifiers, Keywords, Types, and Functions
                if (c.isLetter() || c == '_') {
                    var j = i
                    while (j < n && (code[j].isLetterOrDigit() || code[j] == '_')) {
                        j++
                    }
                    val word = code.substring(i, j)

                    // Peek if followed by '(' -> function call / definition
                    var k = j
                    while (k < n && (code[k] == ' ' || code[k] == '\t')) k++
                    val isFunction = k < n && code[k] == '('

                    when {
                        // Special keywords like self, this, super
                        word in setOf("self", "this", "super", "cls") -> {
                            pushStyle(SpanStyle(color = SyntaxTheme.Special, fontStyle = FontStyle.Italic))
                            append(word)
                            pop()
                        }
                        // Builtin constants
                        word in builtins -> {
                            pushStyle(SpanStyle(color = SyntaxTheme.Constant, fontWeight = FontWeight.Bold))
                            append(word)
                            pop()
                        }
                        // Keywords
                        word in keywords || (isSql && word.uppercase() in keywords) -> {
                            pushStyle(SpanStyle(color = SyntaxTheme.Keyword, fontWeight = FontWeight.SemiBold))
                            append(word)
                            pop()
                        }
                        // Function call/definition
                        isFunction -> {
                            pushStyle(SpanStyle(color = SyntaxTheme.Function, fontWeight = FontWeight.Medium))
                            append(word)
                            pop()
                        }
                        // PascalCase Class / Type Name
                        word.isNotEmpty() && word[0].isUpperCase() && !isSql -> {
                            pushStyle(SpanStyle(color = SyntaxTheme.Type, fontWeight = FontWeight.SemiBold))
                            append(word)
                            pop()
                        }
                        // Default identifier
                        else -> {
                            pushStyle(SpanStyle(color = SyntaxTheme.Default))
                            append(word)
                            pop()
                        }
                    }
                    i = j
                    continue
                }

                // 9. Operators (=, +, -, *, /, %, &, |, ^, <, >, !, :, ?, ->, =>)
                if (c in "=+-*/%&|^<>!?:") {
                    var j = i
                    while (j < n && code[j] in "=+-*/%&|^<>!?:") j++
                    pushStyle(SpanStyle(color = SyntaxTheme.Operator))
                    append(code.substring(i, j))
                    pop()
                    i = j
                    continue
                }

                // 10. Default characters (punctuation, parentheses, brackets, whitespace)
                pushStyle(SpanStyle(color = SyntaxTheme.Default))
                append(c)
                pop()
                i++
            }
        }
    }
}
