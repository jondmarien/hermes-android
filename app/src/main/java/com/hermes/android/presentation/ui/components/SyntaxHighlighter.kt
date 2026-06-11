package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SyntaxHighlighter(
    text: String,
    textColor: Color,
    fontSize: androidx.compose.ui.unit.Sp = 15.sp,
    modifier: Modifier = Modifier
) {
    // Simple markdown code block parsing
    val segments = parseMarkdown(text)
    
    Column(modifier = modifier.fillMaxWidth()) {
        segments.forEachIndexed { index, segment ->
            when (segment.type) {
                SegmentType.PLAIN -> {
                    Text(
                        text = segment.content,
                        color = textColor,
                        fontSize = fontSize,
                        maxLines = Int.MAX_VALUE,
                        overflow = TextOverflow.Visible
                    )
                }
                SegmentType.CODE_INLINE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                            .background(HermesTheme.colorScheme.surfaceContainer)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = segment.content,
                            color = textColor.copy(alpha = 0.9f),
                            fontSize = fontSize,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                SegmentType.CODE_BLOCK -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color(0xFF1E1E1E))
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            if (segment.language.isNotBlank()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = segment.language,
                                        color = Color(0xFF888888),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    // Copy button would go here
                                }
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 8.dp))
                            }
                            Text(
                                text = segment.content,
                                color = Color(0xFFD4D4D4),
                                fontSize = fontSize,
                                fontFamily = FontFamily.Monospace,
                                maxLines = Int.MAX_VALUE,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                }
            }
            if (index < segments.lastIndex) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

enum class SegmentType {
    PLAIN, CODE_INLINE, CODE_BLOCK
}

data class Segment(
    val type: SegmentType,
    val content: String,
    val language: String = ""
)

fun parseMarkdown(text: String): List<Segment> {
    val segments = mutableListOf<Segment>()
    var remaining = text
    var inCodeBlock = false
    var codeBlockLanguage = ""
    var codeBlockContent = StringBuilder()
    var plainContent = StringBuilder()

    val lines = text.split("\n")
    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()

        if (trimmed.startsWith("```")) {
            // Handle code block start/end
            if (!inCodeBlock) {
                // Flush pending plain content
                if (plainContent.isNotBlank()) {
                    segments.add(Segment(SegmentType.PLAIN, plainContent.toString()))
                    plainContent = StringBuilder()
                }
                inCodeBlock = true
                codeBlockLanguage = trimmed.substring(3).trim()
                codeBlockContent = StringBuilder()
            } else {
                // End of code block
                inCodeBlock = false
                segments.add(Segment(SegmentType.CODE_BLOCK, codeBlockContent.toString().trim(), codeBlockLanguage))
                codeBlockLanguage = ""
                codeBlockContent = StringBuilder()
            }
        } else if (inCodeBlock) {
            codeBlockContent.append(line).append("\n")
        } else {
            // Process inline code and plain text
            processInlineCode(line, plainContent, segments)
        }
        i++
    }

    // Flush any remaining
    if (inCodeBlock && codeBlockContent.isNotBlank()) {
        segments.add(Segment(SegmentType.CODE_BLOCK, codeBlockContent.toString().trim(), codeBlockLanguage))
    } else if (plainContent.isNotBlank()) {
        segments.add(Segment(SegmentType.PLAIN, plainContent.toString()))
    }

    return if (segments.isEmpty()) listOf(Segment(SegmentType.PLAIN, text)) else segments
}

private fun processInlineCode(
    line: String,
    plainContent: StringBuilder,
    segments: MutableList<Segment>
) {
    var remaining = line
    var lastEnd = 0

    while (true) {
        val start = remaining.indexOf('`', lastEnd)
        if (start == -1) break

        val end = remaining.indexOf('`', start + 1)
        if (end == -1) break

        // Add text before backtick
        if (start > lastEnd) {
            plainContent.append(remaining.substring(lastEnd, start))
        }

        // Add inline code segment if we have accumulated plain text
        if (plainContent.isNotBlank()) {
            segments.add(Segment(SegmentType.PLAIN, plainContent.toString()))
            plainContent = StringBuilder()
        }

        // Add inline code
        val code = remaining.substring(start + 1, end)
        if (code.isNotBlank()) {
            segments.add(Segment(SegmentType.CODE_INLINE, code))
        }

        lastEnd = end + 1
    }

    // Add remaining text
    if (lastEnd < remaining.length) {
        plainContent.append(remaining.substring(lastEnd))
    }
}