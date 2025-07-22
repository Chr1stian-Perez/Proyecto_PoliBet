package com.epn.polibet.data.repository

import com.epn.polibet.data.models.LoginRequest
import com.epn.polibet.data.models.RegisterRequest
import com.epn.polibet.data.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Base de datos simulada de usuarios registrados
    private val registeredUsers = mutableListOf<User>(
        // Usuarios de prueba predefinidos
        User(
            id = "user_demo1",
            email = "demo@polibet.com",
            username = "demo",
            fullName = "Usuario Demo",
            balance = 1000.0
        ),
        User(
            id = "user_admin",
            email = "admin@polibet.com",
            username = "admin",
            fullName = "Administrador",
            balance = 5000.0
        ),
        User(
            id = "user_test",
            email = "test@polibet.com",
            username = "test",
            fullName = "Usuario Test",
            balance = 500.0
        )
    )

    // Mapa de credenciales: username -> password
    private val userCredentials = mutableMapOf(
        "demo" to "123456",
        "admin" to "admin123",
        "test" to "test123"
    )

    suspend fun login(request: LoginRequest): Result<User> {
        delay(1000) // Simular llamada de red

        // Validación básica
        if (request.email.isBlank() || request.password.isBlank()) {
            return Result.failure(Exception("Usuario y contraseña son requeridos"))
        }

        // Buscar por username (usando el campo email como username)
        val username = request.email.lowercase()
        val password = request.password

        // Verificar credenciales
        val storedPassword = userCredentials[username]
        if (storedPassword == null) {
            return Result.failure(Exception("Usuario no encontrado"))
        }

        if (storedPassword != password) {
            return Result.failure(Exception("Contraseña incorrecta"))
        }

        // Buscar el usuario
        val user = registeredUsers.find { it.username.lowercase() == username }
        if (user == null) {
            return Result.failure(Exception("Error interno: usuario no encontrado"))
        }

        _currentUser.value = user
        _isAuthenticated.value = true

        return Result.success(user)
    }

    suspend fun register(request: RegisterRequest): Result<User> {
        delay(1000) // Simular llamada de red

        // Validaciones
        if (request.username.isBlank() || request.password.isBlank() || request.fullName.isBlank()) {
            return Result.failure(Exception("Todos los campos son requeridos"))
        }

        if (request.password != request.confirmPassword) {
            return Result.failure(Exception("Las contraseñas no coinciden"))
        }

        if (request.password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        val username = request.username.lowercase()

        // Verificar si el usuario ya existe
        if (userCredentials.containsKey(username)) {
            return Result.failure(Exception("El nombre de usuario ya está registrado"))
        }

        if (registeredUsers.any { it.email.lowercase() == request.email.lowercase() }) {
            return Result.failure(Exception("El email ya está registrado"))
        }

        val newUser = User(
            id = "user_${System.currentTimeMillis()}",
            email = request.email,
            username = request.username,
            fullName = request.fullName,
            balance = 1000.0 // Balance inicial
        )

        // Agregar a la base de datos simulada
        registeredUsers.add(newUser)
        userCredentials[username] = request.password

        _currentUser.value = newUser
        _isAuthenticated.value = true

        return Result.success(newUser)
    }

    // Método para actualizar el balance del usuario
    suspend fun updateUserBalance(userId: String, newBalance: Double): Result<User> {
        delay(200) // Simular transacción

        val userIndex = registeredUsers.indexOfFirst { it.id == userId }
        if (userIndex == -1) {
            return Result.failure(Exception("Usuario no encontrado"))
        }

        val updatedUser = registeredUsers[userIndex].copy(balance = newBalance)
        registeredUsers[userIndex] = updatedUser

        // Actualizar el usuario actual si es el mismo
        if (_currentUser.value?.id == userId) {
            _currentUser.value = updatedUser
        }

        return Result.success(updatedUser)
    }

    // Método para debitar dinero (hacer apuesta)
    suspend fun debitBalance(userId: String, amount: Double): Result<User> {
        val currentUser = registeredUsers.find { it.id == userId }
            ?: return Result.failure(Exception("Usuario no encontrado"))

        if (currentUser.balance < amount) {
            return Result.failure(Exception("Fondos insuficientes. Balance actual: $${String.format("%.2f", currentUser.balance)}"))
        }

        val newBalance = currentUser.balance - amount
        return updateUserBalance(userId, newBalance)
    }

    // Método para acreditar dinero (ganar apuesta)
    suspend fun creditBalance(userId: String, amount: Double): Result<User> {
        val currentUser = registeredUsers.find { it.id == userId }
            ?: return Result.failure(Exception("Usuario no encontrado"))

        val newBalance = currentUser.balance + amount
        return updateUserBalance(userId, newBalance)
    }

    // Método para verificar si el usuario tiene fondos suficientes
    fun hasEnoughBalance(userId: String, amount: Double): Boolean {
        val user = registeredUsers.find { it.id == userId }
        return user?.balance?.let { it >= amount } ?: false
    }

    // Método para obtener el balance actual
    fun getCurrentBalance(userId: String): Double {
        return registeredUsers.find { it.id == userId }?.balance ?: 0.0
    }

    fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    // Método para obtener usuario por ID (útil para persistencia)
    fun getUserById(userId: String): User? {
        return registeredUsers.find { it.id == userId }
    }

    // Método para obtener todos los usuarios registrados (para debug)
    fun getAllUsers(): List<User> {
        return registeredUsers.toList()
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository().also { INSTANCE = it }
            }
        }
    }
}
