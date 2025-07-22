package com.epn.polibet.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epn.polibet.data.models.Prediction
import com.epn.polibet.data.models.PredictionType
import com.epn.polibet.data.repository.AuthRepository
import com.epn.polibet.data.repository.PredictionsRepository
import com.epn.polibet.ui.components.*
import com.epn.polibet.ui.viewmodels.AuthViewModel
import com.epn.polibet.ui.viewmodels.DashboardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToSports: (String) -> Unit,
    onNavigateToPredictions: () -> Unit,
    onNavigateToProfile: () -> Unit,
    dashboardViewModel: DashboardViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Estados para el dialog de apuesta rápida
    var showQuickBetDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<com.epn.polibet.data.models.Event?>(null) }
    var selectedOption by remember { mutableStateOf("") }
    var selectedOdds by remember { mutableStateOf(0.0) }
    var selectedPredictionType by remember { mutableStateOf(PredictionType.MATCH_RESULT) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val predictionsRepository = remember { PredictionsRepository.getInstance() }
    val authRepository = remember { AuthRepository.getInstance() }

    // Función para crear apuesta rápida
    fun createQuickBet(amount: Double) {
        scope.launch {
            try {
                val currentUserValue = authRepository.currentUser.value
                if (currentUserValue == null || selectedEvent == null) {
                    successMessage = "Error: Usuario no autenticado"
                    showSuccessMessage = true
                    showQuickBetDialog = false
                    return@launch
                }

                // Verificar fondos suficientes
                if (!authRepository.hasEnoughBalance(currentUserValue.id, amount)) {
                    val currentBalance = authRepository.getCurrentBalance(currentUserValue.id)
                    successMessage = "Fondos insuficientes. Balance: $${String.format("%.2f", currentBalance)}"
                    showSuccessMessage = true
                    showQuickBetDialog = false
                    return@launch
                }

                val prediction = Prediction(
                    id = "",
                    userId = currentUserValue.id,
                    eventId = selectedEvent!!.id,
                    predictionType = selectedPredictionType,
                    selectedOption = selectedOption,
                    odds = selectedOdds,
                    amount = amount,
                    potentialWin = amount * selectedOdds
                )

                val result = predictionsRepository.createPrediction(prediction, authRepository)
                result.fold(
                    onSuccess = {
                        val newBalance = authRepository.getCurrentBalance(currentUserValue.id)
                        successMessage = "¡Apuesta creada! Nuevo balance: $${String.format("%.2f", newBalance)}"
                        showSuccessMessage = true
                        dashboardViewModel.refreshPredictionsCount()
                        dashboardViewModel.refreshPredictionsSummary()

                        // Agregar un delay y refrescar nuevamente para asegurar que se actualice
                        kotlinx.coroutines.delay(500)
                        dashboardViewModel.refreshPredictionsSummary()
                    },
                    onFailure = { exception ->
                        successMessage = "Error: ${exception.message}"
                        showSuccessMessage = true
                    }
                )
            } catch (e: Exception) {
                successMessage = "Error inesperado: ${e.message}"
                showSuccessMessage = true
            } finally {
                showQuickBetDialog = false
            }
        }
    }

    // Función para validar cantidad de apuesta
    fun validateBetAmount(amount: Double): String? {
        val currentUserValue = authRepository.currentUser.value ?: return "Usuario no autenticado"
        val currentBalance = authRepository.getCurrentBalance(currentUserValue.id)

        return when {
            amount <= 0 -> "La cantidad debe ser mayor a 0"
            amount > currentBalance -> "Fondos insuficientes. Balance: $${String.format("%.2f", currentBalance)}"
            amount > 1000 -> "Cantidad máxima por apuesta: $1000"
            else -> null
        }
    }

    // Mostrar mensaje de éxito
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            kotlinx.coroutines.delay(4000)
            showSuccessMessage = false
        }
    }

    // Agregar después de los otros LaunchedEffect existentes
    LaunchedEffect(Unit) {
        // Refrescar estadísticas cuando se carga la pantalla
        dashboardViewModel.refreshPredictionsSummary()
    }

    // También agregar un efecto para refrescar cuando cambie el usuario actual
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            dashboardViewModel.refreshPredictionsSummary()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "¡Hola, ${currentUser?.username ?: "Usuario"}!",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Balance: $${String.format("%.2f", currentUser?.balance ?: 0.0)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            actions = {
                IconButton(onClick = onNavigateToPredictions) {
                    Icon(Icons.Default.List, contentDescription = "Mis Pronósticos")
                }
                IconButton(onClick = onNavigateToProfile) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil")
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mensaje de éxito/error
                if (showSuccessMessage) {
                    item {
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
                }

                // Eventos en vivo
                if (uiState.liveEvents.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "🔴 En Vivo",
                            subtitle = "${uiState.liveEvents.size} eventos"
                        )
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            items(uiState.liveEvents) { event ->
                                LiveEventCard(
                                    event = event,
                                    onClick = { onNavigateToSports(event.sportId) },
                                    onBetClick = { option, odds, predictionType ->
                                        selectedEvent = event
                                        selectedOption = option
                                        selectedOdds = odds
                                        selectedPredictionType = predictionType
                                        showQuickBetDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                // Deportes disponibles
                item {
                    SectionHeader(
                        title = "Deportes",
                        subtitle = "Explora todas las categorías"
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(uiState.sports) { sport ->
                            SportCard(
                                sport = sport,
                                onClick = { onNavigateToSports(sport.id) }
                            )
                        }
                    }
                }

                // Eventos destacados
                if (uiState.featuredEvents.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Eventos Destacados",
                            subtitle = "Los más populares"
                        )
                    }

                    items(uiState.featuredEvents) { event ->
                        FeaturedEventCard(
                            event = event,
                            onClick = { onNavigateToSports(event.sportId) },
                            onBetClick = { option, odds, predictionType ->
                                selectedEvent = event
                                selectedOption = option
                                selectedOdds = odds
                                selectedPredictionType = predictionType
                                showQuickBetDialog = true
                            }
                        )
                    }
                }

                // Estadísticas rápidas
                item {
                    QuickStatsCard(
                        onNavigateToPredictions = onNavigateToPredictions,
                        predictionsSummary = uiState.predictionsSummary
                    )
                }
            }
        }
    }

    // Dialog de apuesta rápida
    if (showQuickBetDialog && selectedEvent != null) {
        EnhancedQuickBetDialog(
            eventName = "${selectedEvent!!.homeTeam} vs ${selectedEvent!!.awayTeam}",
            selectedOption = selectedOption,
            odds = selectedOdds,
            currentBalance = currentUser?.balance ?: 0.0,
            onDismiss = { showQuickBetDialog = false },
            onConfirm = { amount -> createQuickBet(amount) },
            validateAmount = { amount -> validateBetAmount(amount) }
        )
    }
}

@Composable
fun EnhancedQuickBetDialog(
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
                text = "🎯 Apuesta Rápida",
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
                            text = "💰 Balance:",
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
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
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
                                    text = "Ganancia potencial:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format("%.2f", potentialWin)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Balance después:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "$${String.format("%.2f", currentBalance - amountValue!!)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentBalance - amountValue < 100) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
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
                    Text("Apostar")
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
