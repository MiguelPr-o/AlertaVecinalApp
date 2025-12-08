package mx.edu.utng.alertavecinal.utils

import mx.edu.utng.alertavecinal.data.model.ReportType
import java.util.regex.Pattern

object ValidationUtils {

    // Patrón para validar email
    private const val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"

    // Patrón para validar contraseña (mínimo 6 caracteres, al menos una letra y un número)
    private const val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@\$!%*?&]{6,}\$"

    // Patrón para validar nombre (solo letras y espacios)
    private const val NAME_PATTERN = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}\$"

    // Patrón para validar teléfono (mexicano)
    private const val PHONE_PATTERN = "^[0-9]{10}\$"

    fun isValidEmail(email: String): Boolean {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()
    }

    fun isValidName(name: String): Boolean {
        return Pattern.compile(NAME_PATTERN).matcher(name).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        return Pattern.compile(PHONE_PATTERN).matcher(phone).matches()
    }

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("El email es requerido")
            !isValidEmail(email) -> ValidationResult.Error("Formato de email inválido")
            else -> ValidationResult.Success
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("La contraseña es requerida")
            password.length < 6 -> ValidationResult.Error("La contraseña debe tener al menos 6 caracteres")
            !isValidPassword(password) -> ValidationResult.Error("La contraseña debe contener al menos una letra y un número")
            else -> ValidationResult.Success
        }
    }

    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("El nombre es requerido")
            name.length < 2 -> ValidationResult.Error("El nombre debe tener al menos 2 caracteres")
            !isValidName(name) -> ValidationResult.Error("El nombre solo puede contener letras y espacios")
            else -> ValidationResult.Success
        }
    }

    fun validatePhone(phone: String): ValidationResult {
        return when {
            phone.isBlank() -> ValidationResult.Success // Teléfono es opcional
            !isValidPhone(phone) -> ValidationResult.Error("El teléfono debe tener 10 dígitos")
            else -> ValidationResult.Success
        }
    }

    fun validateReportTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Error("El título es requerido")
            title.length < 5 -> ValidationResult.Error("El título debe tener al menos 5 caracteres")
            title.length > Constants.MAX_REPORT_TITLE_LENGTH -> ValidationResult.Error("El título es demasiado largo")
            else -> ValidationResult.Success
        }
    }

    fun validateReportDescription(description: String): ValidationResult {
        return when {
            description.isBlank() -> ValidationResult.Error("La descripción es requerida")
            description.length < 10 -> ValidationResult.Error("La descripción debe tener al menos 10 caracteres")
            description.length > Constants.MAX_REPORT_DESCRIPTION_LENGTH -> ValidationResult.Error("La descripción es demasiado larga")
            else -> ValidationResult.Success
        }
    }

    fun validateReportType(reportType: ReportType?): ValidationResult {
        return when (reportType) {
            null -> ValidationResult.Error("Selecciona un tipo de incidente")
            else -> ValidationResult.Success
        }
    }

    fun validateLocation(latitude: Double, longitude: Double): ValidationResult {
        return when {
            latitude == 0.0 && longitude == 0.0 -> ValidationResult.Error("La ubicación es requerida")
            latitude < -90 || latitude > 90 -> ValidationResult.Error("Latitud inválida")
            longitude < -180 || longitude > 180 -> ValidationResult.Error("Longitud inválida")
            else -> ValidationResult.Success
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult.Error("Confirma tu contraseña")
            password != confirmPassword -> ValidationResult.Error("Las contraseñas no coinciden")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    val isValid: Boolean
        get() = this is Success
}