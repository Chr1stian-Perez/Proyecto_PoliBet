package com.epn.polibet.ui.screens.predictions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epn.polibet.ui.components.SectionHeader
import com.epn.polibet.ui.viewmodels.PredictionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PredictionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPredictions()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Mis Pronósticos",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.loadPredictions() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                }
            }
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando pronósticos...")
                    }
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "❌",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error al cargar pronósticos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadPredictions() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            uiState.predictions.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes pronósticos aún",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Explora los eventos y crea tu primer pronóstico",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Resumen de estadísticas
                    item {
                        uiState.summary?.let { summary ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Resumen",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        SummaryItem(
                                            label = "Total",
                                            value = summary.totalPredictions.toString()
                                        )
                                        SummaryItem(
                                            label = "Ganados",
                                            value = summary.wonPredictions.toString()
                                        )
                                        SummaryItem(
                                            label = "Tasa Éxito",
                                            value = "${String.format("%.1f", summary.winRate)}%"
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        SummaryItem(
                                            label = "Apostado",
                                            value = "$${String.format("%.2f", summary.totalStaked)}"
                                        )
                                        SummaryItem(
                                            label = "Ganado",
                                            value = "$${String.format("%.2f", summary.totalWon)}"
                                        )
                                        SummaryItem(
                                            label = "Balance",
                                            value = "$${String.format("%.2f", summary.totalWon - summary.totalStaked)}"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        SectionHeader(
                            title = "Historial de Pronósticos",
                            subtitle = "${uiState.predictions.size} pronósticos"
                        )
                    }

                    items(uiState.predictions) { prediction ->
                        PredictionCard(
                            prediction = prediction,
                            onCancel = { viewModel.cancelPrediction(prediction.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionCard(
    prediction: com.epn.polibet.data.models.Prediction,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pronóstico #${prediction.id.takeLast(6)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                StatusChip(status = prediction.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Selección: ${prediction.selectedOption}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Cuota: ${prediction.odds}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Apostado: $${String.format("%.2f", prediction.amount)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Ganancia potencial: $${String.format("%.2f", prediction.potentialWin)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (prediction.status == com.epn.polibet.data.models.PredictionStatus.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@Composable
fun StatusChip(
    status: com.epn.polibet.data.models.PredictionStatus,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        com.epn.polibet.data.models.PredictionStatus.PENDING -> "Pendiente" to MaterialTheme.colorScheme.primary
        com.epn.polibet.data.models.PredictionStatus.WON -> "Ganado" to MaterialTheme.colorScheme.primary
        com.epn.polibet.data.models.PredictionStatus.LOST -> "Perdido" to MaterialTheme.colorScheme.error
        com.epn.polibet.data.models.PredictionStatus.CANCELLED -> "Cancelado" to MaterialTheme.colorScheme.outline
        com.epn.polibet.data.models.PredictionStatus.VOID -> "Anulado" to MaterialTheme.colorScheme.outline
    }

    AssistChip(
        onClick = { },
        label = { Text(text) },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            labelColor = color
        )
    )
}
