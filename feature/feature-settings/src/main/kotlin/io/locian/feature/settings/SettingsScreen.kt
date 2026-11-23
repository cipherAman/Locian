package io.locian.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.locian.core.ui.theme.CyanNeon
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val config = uiState.config

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Inference Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetToHardwareDefaults() }) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Reset defaults")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // System Prompt Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System Prompt",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sets instructions and persona for the AI model.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = config.systemPrompt,
                        onValueChange = { viewModel.updateConfig(config.copy(systemPrompt = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Generation Parameters
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Sampling Parameters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Temperature
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Temperature", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = String.format(Locale.US, "%.2f", config.temperature),
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = config.temperature,
                            onValueChange = { viewModel.updateConfig(config.copy(temperature = it)) },
                            valueRange = 0.1f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                        )
                    }

                    // Top P
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Top-P", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = String.format(Locale.US, "%.2f", config.topP),
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = config.topP,
                            onValueChange = { viewModel.updateConfig(config.copy(topP = it)) },
                            valueRange = 0.1f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                        )
                    }

                    // Min P
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Min-P", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = String.format(Locale.US, "%.2f", config.minP),
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = config.minP,
                            onValueChange = { viewModel.updateConfig(config.copy(minP = it)) },
                            valueRange = 0.0f..0.5f,
                            colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                        )
                    }
                }
            }

            // Hardware & Performance
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Hardware & Threads",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Threads
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Inference Threads", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${config.nThreads} threads",
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = config.nThreads.toFloat(),
                            onValueChange = { viewModel.updateConfig(config.copy(nThreads = it.toInt())) },
                            valueRange = 1f..uiState.maxCpuCores.toFloat().coerceAtLeast(1f),
                            steps = (uiState.maxCpuCores - 2).coerceAtLeast(0),
                            colors = SliderDefaults.colors(thumbColor = CyanNeon, activeTrackColor = CyanNeon)
                        )
                    }

                    // Context Length
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Context Window Size", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "${config.contextLength} tokens",
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val contextOptions = listOf(1024, 2048, 4096, 8192)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            contextOptions.forEach { length ->
                                val selected = config.contextLength == length
                                OutlinedButton(
                                    onClick = { viewModel.updateConfig(config.copy(contextLength = length)) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (selected) 1.5.dp else 0.5.dp,
                                        color = if (selected) CyanNeon else MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    Text(
                                        text = "${length / 1024}k",
                                        color = if (selected) CyanNeon else MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    // Memory Mmap switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Memory Mapping (mmap)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                "Loads model pages on-demand; prevents OOM crashes on low RAM devices.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = config.useMmap,
                            onCheckedChange = { viewModel.updateConfig(config.copy(useMmap = it)) }
                        )
                    }
                }
            }

            // Reset button
            OutlinedButton(
                onClick = { viewModel.resetToHardwareDefaults() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Device Optimal Settings")
            }
        }
    }
}
