package io.locian.feature.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.locian.core.common.Formatters
import io.locian.core.common.MessageContentParser
import io.locian.core.model.ChatMessage
import io.locian.core.model.ChatRole
import io.locian.core.ui.components.LocianAvatar
import io.locian.core.ui.components.LocianFullLogo
import io.locian.core.ui.components.MarkdownContent
import io.locian.core.ui.components.TelemetryPill
import io.locian.core.ui.components.ThinkingProcessCard
import io.locian.core.ui.theme.CyanNeon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToModels: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    var userScrolledUp by remember { mutableStateOf(false) }

    val isAtBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf true
            val lastVisible = visibleItems.last()
            lastVisible.index >= layoutInfo.totalItemsCount - 1 && !listState.canScrollForward
        }
    }

    // When the user drags and moves away from the bottom, mark as scrolled up
    LaunchedEffect(isDragged, listState.canScrollForward) {
        if (isDragged && listState.canScrollForward) {
            userScrolledUp = true
        }
    }

    // Reset userScrolledUp when user reaches the bottom and releases their drag
    LaunchedEffect(isAtBottom, isDragged) {
        if (isAtBottom && !isDragged) {
            userScrolledUp = false
        }
    }

    // Auto-scroll when new messages are added
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            userScrolledUp = false
            val totalCount = uiState.messages.size + (if (uiState.streamingText.isNotEmpty()) 1 else 0)
            listState.animateScrollToItem((totalCount - 1).coerceAtLeast(0))
        }
    }

    // Auto-scroll on incoming stream tokens ONLY if the user has not scrolled up and is not actively dragging
    LaunchedEffect(uiState.streamingText) {
        if (!userScrolledUp && !isDragged && uiState.streamingText.isNotEmpty()) {
            val totalCount = uiState.messages.size + 1
            listState.scrollToItem((totalCount - 1).coerceAtLeast(0))
        }
    }

    val showScrollToBottom by remember {
        derivedStateOf {
            !isAtBottom && (uiState.messages.isNotEmpty() || uiState.streamingText.isNotEmpty())
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            LocianAvatar(
                                size = 44.dp,
                                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                if (uiState.conversationId == null) {
                                    LocianFullLogo(height = 18.dp)
                                } else {
                                    Text(
                                        text = uiState.conversationTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                // Model Selector Badge
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (uiState.isModelLoaded) CyanNeon.copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = BorderStroke(
                                        0.5.dp,
                                        if (uiState.isModelLoaded) CyanNeon.copy(alpha = 0.35f)
                                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.clickable { onNavigateToModels() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (uiState.isModelLoaded) CyanNeon else Color.Gray)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = uiState.activeModelName ?: "Select Model",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            fontWeight = FontWeight.Medium,
                                            color = if (uiState.isModelLoaded) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Switch Model",
                                            modifier = Modifier.size(12.dp),
                                            tint = if (uiState.isModelLoaded) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Conversations")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.startNewConversation() }) {
                            Icon(Icons.Default.Add, contentDescription = "New Chat")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            // Messages List Area with floating scroll-to-bottom FAB
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.messages.isEmpty() && uiState.streamingText.isEmpty()) {
                        item {
                            EmptyChatState(
                                isModelLoaded = uiState.isModelLoaded,
                                onSelectModel = onNavigateToModels,
                                onPromptClick = { prompt ->
                                    userScrolledUp = false
                                    viewModel.sendMessage(prompt)
                                }
                            )
                        }
                    }

                    items(uiState.messages, key = { it.id }) { message ->
                        MessageBubble(message = message, showThinking = uiState.showThinking)
                    }

                    if (uiState.isGenerating && uiState.streamingText.isNotEmpty()) {
                        item {
                            AssistantStreamingBubble(text = uiState.streamingText, showThinking = uiState.showThinking)
                        }
                    }
                }

                // Smooth Scroll to bottom button
                androidx.compose.animation.AnimatedVisibility(
                    visible = showScrollToBottom,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 12.dp, end = 16.dp)
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            userScrolledUp = false
                            coroutineScope.launch {
                                val totalCount = uiState.messages.size + (if (uiState.streamingText.isNotEmpty()) 1 else 0)
                                listState.animateScrollToItem((totalCount - 1).coerceAtLeast(0))
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = CyanNeon,
                        shape = CircleShape,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Scroll to bottom",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Integrated Pill Chat Input Dock
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    userScrolledUp = false
                    viewModel.sendMessage(inputText)
                    inputText = ""
                    keyboardController?.hide()
                },
                isGenerating = uiState.isGenerating,
                onStop = { viewModel.stopGeneration() },
                isModelLoaded = uiState.isModelLoaded
            )
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    showThinking: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == ChatRole.USER
    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val parsed = remember(message.content) {
        MessageContentParser.parse(message.content, isStreaming = false)
    }

    if (isUser) {
        // User message bubble aligned to End (max 85% width)
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.End
            ) {
                val bubbleShape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 6.dp
                )
                Box(
                    modifier = Modifier
                        .clip(bubbleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
                        .border(
                            0.5.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                            bubbleShape
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // User message footer (timestamp, copy action)
                Row(
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = Formatters.formatTimestamp(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Message", message.content))
                            isCopied = true
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                delay(2000)
                                isCopied = false
                            }
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy message",
                            tint = if (isCopied) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    } else {
        // AI response spanning FULL WIDTH
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            // Assistant Header (Avatar + "Locian")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
            ) {
                LocianAvatar(
                    size = 22.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Locian",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            val bubbleShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(bubbleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        bubbleShape
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val thinkingText = parsed.thinking
                    if (showThinking && thinkingText != null) {
                        ThinkingProcessCard(
                            thinking = thinkingText,
                            isStillThinking = false,
                            modifier = Modifier.padding(bottom = if (parsed.response.isNotEmpty()) 10.dp else 0.dp)
                        )
                    }

                    if (parsed.response.isNotEmpty()) {
                        MarkdownContent(
                            content = parsed.response,
                            textColor = MaterialTheme.colorScheme.onSurface
                        )
                    } else if (thinkingText != null && !showThinking) {
                        Text(
                            text = "(Thinking hidden)",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Assistant footer (timestamp, copy action, telemetry)
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = Formatters.formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val textToCopy = parsed.response.ifEmpty { parsed.thinking ?: message.content }
                        clipboard.setPrimaryClip(ClipData.newPlainText("Message", textToCopy))
                        isCopied = true
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        scope.launch {
                            delay(2000)
                            isCopied = false
                        }
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy message",
                        tint = if (isCopied) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                }

                TelemetryPill(
                    tokensPerSecond = message.tokensPerSecond,
                    timeToFirstTokenMs = message.timeToFirstTokenMs,
                    totalTokens = message.totalTokens
                )
            }
        }
    }
}

@Composable
fun AssistantStreamingBubble(
    text: String,
    showThinking: Boolean = true,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    val borderGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "borderGlow"
    )

    val parsed = remember(text) {
        MessageContentParser.parse(text, isStreaming = true)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Assistant Header (Avatar + "Locian")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 2.dp, bottom = 6.dp)
        ) {
            LocianAvatar(
                size = 22.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Locian",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val streamShape = RoundedCornerShape(16.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(streamShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                .border(
                    0.75.dp,
                    CyanNeon.copy(alpha = borderGlow),
                    streamShape
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val thinkingText = parsed.thinking
                if (showThinking && thinkingText != null) {
                    ThinkingProcessCard(
                        thinking = thinkingText,
                        isStillThinking = parsed.isStillThinking,
                        modifier = Modifier.padding(bottom = if (parsed.response.isNotEmpty()) 10.dp else 0.dp)
                    )
                }

                if (parsed.response.isNotEmpty()) {
                    MarkdownContent(
                        content = parsed.response,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(CyanNeon.copy(alpha = cursorAlpha))
                    )
                } else if (!parsed.isStillThinking) {
                    // Pulsing typing indicator dot while waiting for initial tokens
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = cursorAlpha))
                        )
                        Text(
                            text = "Locian is generating...",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyChatState(
    isModelLoaded: Boolean,
    onSelectModel: () -> Unit,
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Avatar Container
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            CyanNeon.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            LocianAvatar(
                size = 64.dp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        LocianFullLogo(
            height = 28.dp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Privacy Badge Pill
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isModelLoaded) CyanNeon else Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isModelLoaded) "100% On-Device • Private & Offline" else "No Model Loaded",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        if (!isModelLoaded) {
            Button(
                onClick = onSelectModel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanNeon,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Select & Load Model",
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = "Try asking:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            )

            val promptStarters = listOf(
                Pair("💡 Brainstorm Ideas", "Give me 5 creative ideas for on-device local AI applications."),
                Pair("🐍 Write Python Code", "Write a clean Python script demonstrating asynchronous task queues."),
                Pair("📝 Draft an Email", "Draft a polite and concise email requesting feedback on a proposal."),
                Pair("🔬 Explain Concepts", "Explain how large language models generate text simply.")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                promptStarters.forEach { (title, promptText) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPromptClick(promptText) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyanNeon
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = promptText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isGenerating: Boolean,
    onStop: () -> Unit,
    isModelLoaded: Boolean = true,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    val pillShape = RoundedCornerShape(26.dp)

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) CyanNeon.copy(alpha = 0.85f)
        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
        label = "inputBorderColor"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 1.2.dp else 0.8.dp,
        label = "inputBorderWidth"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(pillShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = pillShape
                )
                .padding(start = 16.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 36.dp)
                    .padding(top = 4.dp, bottom = 4.dp, end = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isFocused = it.isFocused },
                    maxLines = 5,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    cursorBrush = SolidColor(CyanNeon),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(onSend = {
                        if (text.isNotBlank() && !isGenerating) onSend()
                    }),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(
                                text = if (isModelLoaded) "Message Locian..." else "Load a model to chat...",
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    }
                )
            }

            AnimatedContent(
                targetState = isGenerating,
                transitionSpec = {
                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                },
                label = "chatActionTransition"
            ) { generating ->
                if (generating) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error)
                            .clickable { onStop() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    val canSend = text.isNotBlank()
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (canSend) CyanNeon
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .clickable(enabled = canSend) { onSend() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = if (canSend) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
