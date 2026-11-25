package io.locian.core.common

/**
 * Represents parsed content of a chat message that may contain
 * internal reasoning/thought blocks (<think>...</think> or <thought>...</thought>).
 */
data class ParsedMessage(
    val thinking: String? = null,
    val response: String = "",
    val isStillThinking: Boolean = false
)

object MessageContentParser {

    /**
     * Parses message content into reasoning thinking block and clean response text.
     * Handles:
     * 1. Standard `<think>...</think>` tags
     * 2. Unclosed `<think>...` (currently streaming reasoning)
     * 3. Prefilled `<think>` from prompt templates where generation begins inside thinking
     *    and only emits `</think>`
     * 4. Alternative `<thought>...</thought>` tags
     * 5. Standard non-reasoning responses
     */
    fun parse(content: String, isStreaming: Boolean = false): ParsedMessage {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return ParsedMessage(thinking = null, response = "", isStillThinking = isStreaming)
        }

        // Case 1: Standard <think>...</think>
        if (content.contains("<think>")) {
            val thinkStart = content.indexOf("<think>") + "<think>".length
            if (content.contains("</think>")) {
                val thinkEnd = content.indexOf("</think>")
                val thinking = content.substring(thinkStart, thinkEnd).trim()
                val response = content.substring(thinkEnd + "</think>".length).trimStart()
                return ParsedMessage(
                    thinking = thinking.ifEmpty { null },
                    response = response,
                    isStillThinking = false
                )
            } else {
                // <think> opened but not closed yet
                val thinking = content.substring(thinkStart).trimStart()
                return ParsedMessage(
                    thinking = thinking,
                    response = "",
                    isStillThinking = isStreaming
                )
            }
        }

        // Case 2: Prefilled <think> where prompt opened <think> and model only emitted </think>
        if (content.contains("</think>")) {
            val thinkEnd = content.indexOf("</think>")
            val thinking = content.substring(0, thinkEnd).trim()
            val response = content.substring(thinkEnd + "</think>".length).trimStart()
            return ParsedMessage(
                thinking = thinking.ifEmpty { null },
                response = response,
                isStillThinking = false
            )
        }

        // Case 3: <thought>...</thought>
        if (content.contains("<thought>")) {
            val thinkStart = content.indexOf("<thought>") + "<thought>".length
            if (content.contains("</thought>")) {
                val thinkEnd = content.indexOf("</thought>")
                val thinking = content.substring(thinkStart, thinkEnd).trim()
                val response = content.substring(thinkEnd + "</thought>".length).trimStart()
                return ParsedMessage(
                    thinking = thinking.ifEmpty { null },
                    response = response,
                    isStillThinking = false
                )
            } else {
                val thinking = content.substring(thinkStart).trimStart()
                return ParsedMessage(
                    thinking = thinking,
                    response = "",
                    isStillThinking = isStreaming
                )
            }
        }

        if (content.contains("</thought>")) {
            val thinkEnd = content.indexOf("</thought>")
            val thinking = content.substring(0, thinkEnd).trim()
            val response = content.substring(thinkEnd + "</thought>".length).trimStart()
            return ParsedMessage(
                thinking = thinking.ifEmpty { null },
                response = response,
                isStillThinking = false
            )
        }

        // Case 4: Normal content without reasoning tags
        return ParsedMessage(
            thinking = null,
            response = content,
            isStillThinking = false
        )
    }
}
