package mx.edu.utng.alertavecinal.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Formas y esquinas redondeadas para la aplicación
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Formas personalizadas para componentes específicos
val AppShapes = Shapes(
    // Para botones
    small = RoundedCornerShape(12.dp),

    // Para tarjetas y diálogos
    medium = RoundedCornerShape(16.dp),

    // Para sheets modales
    large = RoundedCornerShape(24.dp),

    // Para chips y badges
    extraSmall = RoundedCornerShape(8.dp)
)

// Formas específicas para componentes
val ButtonShape = RoundedCornerShape(12.dp)
val CardShape = RoundedCornerShape(16.dp)
val DialogShape = RoundedCornerShape(20.dp)
val TextFieldShape = RoundedCornerShape(12.dp)
val ChipShape = RoundedCornerShape(16.dp)
val BadgeShape = RoundedCornerShape(8.dp)
val FabShape = RoundedCornerShape(16.dp)