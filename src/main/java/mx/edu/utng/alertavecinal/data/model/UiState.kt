package mx.edu.utng.alertavecinal.data.model

/*
Clase UiState: Esta es una clase sellada que representa los posibles
estados de la interfaz de usuario para operaciones asíncronas:
Cargando, Éxito (con datos) y Error (con mensaje). Proporciona una
forma tipo-segura de manejar estados de carga en toda la aplicación.
*/

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

/*
Clase AuthState: Esta clase representa el estado de autenticación
del usuario, almacenando información sobre si el usuario está
autenticado, los datos del usuario actual, y el estado de carga o
error durante operaciones de autenticación.
*/

data class AuthState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)