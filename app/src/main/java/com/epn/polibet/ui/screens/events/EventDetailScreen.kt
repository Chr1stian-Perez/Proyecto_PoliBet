package com.epn.polibet.ui.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epn.polibet.data.models.Prediction
import com.epn.polibet.data.models.PredictionType
import com.epn.polibet.ui.components.OddsButton
import com.epn.polibet.ui.viewmodels.EventDetailViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    onCreatePrediction: (Prediction) -> Unit,
    viewModel: EventDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPredictionDialog by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("") }
    var selectedOdds by remember { mutableStateOf(0.0) }
    var predictionType by remember { mutableStateOf(PredictionType.MATCH_RESULT) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    // Mostrar mensaje de éxito
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            kotlinx.coroutines.delay(4000)
            showSuccessMessage = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Detalle del Evento",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                IconButton(onClick = { /* Agregar a favoritos */ }) {
                    Icon(Icons.Default.Star, contentDescription = "Favorito")
                }
            }
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            uiState.event?.let { event ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mensaje de éxito/error
                    if (showSuccessMessage) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (successMessage.contains("Error") || successMessage.contains("insuficientes"))
                                    MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (successMessage.contains("Error") || successMessage.contains("insuficientes")) "❌" else "✅",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = successMessage,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Balance actual
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💰 Balance Disponible:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$${String.format("%.2f", viewModel.getCurrentUserBalance())}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Información del evento
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
                                text = event.league,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "${event.homeTeam} vs ${event.awayTeam}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Fecha: ${formatEventDateTime(event.startTime)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = "Estado: ${event.status.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Opciones de apuesta - Resultado del partido
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Resultado del Partido",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OddsButton(
                                    label = event.homeTeam.take(8),
                                    odds = event.odds.homeWin,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedOption = "Victoria ${event.homeTeam}"
                                        selectedOdds = event.odds.homeWin
                                        predictionType = PredictionType.MATCH_RESULT
                                        showPredictionDialog = true
                                    }
                                )

                                event.odds.draw?.let { draw ->
                                    OddsButton(
                                        label = "Empate",
                                        odds = draw,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            selectedOption = "Empate"
                                            selectedOdds = draw
                                            predictionType = PredictionType.MATCH_RESULT
                                            showPredictionDialog = true
                                        }
                                    )
                                }

                                OddsButton(
                                    label = event.awayTeam.take(8),
                                    odds = event.odds.awayWin,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedOption = "Victoria ${event.awayTeam}"
                                        selectedOdds = event.odds.awayWin
                                        predictionType = PredictionType.MATCH_RESULT
                                        showPredictionDialog = true
                                    }
                                )
                            }
                        }
                    }

                    // Estadísticas del evento (simuladas)
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Estadísticas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatColumn(label = "Forma Local", value = "W-W-D")
                                StatColumn(label = "Forma Visitante", value = "L-W-W")
                                StatColumn(label = "H2H", value = "2-1-2")
                            }
                        }
                    }

                    // Información adicional
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "ℹ️ Información Importante",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "• Balance actualizado en tiempo real\n" +
                                        "• Las apuestas se resuelven automáticamente\n" +
                                        "• Límite máximo por apuesta: $1000\n" +
                                        "• Proyecto académico - EPN 2025A",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog para crear pronóstico
    if (showPredictionDialog) {
        EnhancedPredictionDialog(
            eventName = "${uiState.event?.homeTeam} vs ${uiState.event?.awayTeam}",
            selectedOption = selectedOption,
            odds = selectedOdds,
            currentBalance = viewModel.getCurrentUserBalance(),
            onDismiss = { showPredictionDialog = false },
            onConfirm = { amount ->
                uiState.event?.let { event ->
                    val prediction = Prediction(
                        id = "",
                        userId = "", // Se asignará en el ViewModel
                        eventId = event.id,
                        predictionType = predictionType,
                        selectedOption = selectedOption,
                        odds = selectedOdds,
                        amount = amount,
                        potentialWin = amount * selectedOdds
                    )
                    viewModel.createPrediction(prediction) { success, message ->
                        showPredictionDialog = false // Cerrar dialog inmediatamente
                        successMessage = message ?: if (success) "¡Pronóstico creado exitosamente!" else "Error al crear el pronóstico"
                        showSuccessMessage = true
                    }
                }
            },
            validateAmount = { amount -> viewModel.validateBetAmount(amount) }
        )
    }
}

@Composable
fun StatColumn(
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EnhancedPredictionDialog(
    eventName: String,
    selectedOption: String,
    odds: Double,
    currentBalance: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit,
    validateAmount: (Double) -> String?
) {
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val potentialWin = amount.toDoubleOrNull()?.let { it * odds } ?: 0.0
    val amountValue = amount.toDoubleOrNull()

    // Validar en tiempo real
    LaunchedEffect(amount) {
        validationError = amountValue?.let { validateAmount(it) }
    }

    val isValidAmount = amountValue != null && validationError == null

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "🎯 Crear Pronóstico",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Balance actual
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💰 Balance disponible:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$${String.format("%.2f", currentBalance)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Información del evento
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = eventName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Selección: $selectedOption",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Cuota: $odds",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Input de cantidad
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            amount = newValue
                        }
                    },
                    label = { Text("Cantidad a apostar") },
                    placeholder = { Text("Ej: 10.00") },
                    leadingIcon = {
                        Text(
                            text = "$",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    isError = validationError != null,
                    supportingText = {
                        Text(
                            text = validationError ?: when {
                                amount.isEmpty() -> "Ingresa una cantidad"
                                isValidAmount -> "Cantidad válida ✓"
                                else -> "Cantidad inválida"
                            },
                            color = if (validationError != null) MaterialTheme.colorScheme.error
                            else if (isValidAmount) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Información de ganancia potencial
                if (isValidAmount) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Apuesta:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "$${String.format("%.2f", amountValue!!)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ganancia potencial:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "$${String.format("%.2f", potentialWin)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Balance después:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format("%.2f", currentBalance - amountValue)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentBalance - amountValue < 100) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Información importante
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = "ℹ️ Tu balance se actualizará inmediatamente. Las apuestas se resuelven automáticamente.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValidAmount && !isLoading) {
                        isLoading = true
                        onConfirm(amountValue!!)
                    }
                },
                enabled = isValidAmount && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Confirmar Apuesta")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatEventDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
