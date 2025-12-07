package cc.worldmandia.kwebutils.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.DynamicMaterialThemeState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(
    state: DynamicMaterialThemeState,
    content: @Composable () -> Unit
) {
    DynamicMaterialExpressiveTheme(
        state = state,
        content = content,
        animate = true,
    )
}

data class BrandColorOption(val name: String, val color: Color)

val availableBrandColors = listOf(
    BrandColorOption("Orange", Color(0xFFFF9800)),
    BrandColorOption("Blue", Color(0xFF2196F3)),
    BrandColorOption("Red", Color(0xFFF44336)),
    BrandColorOption("Green", Color(0xFF4CAF50)),
    BrandColorOption("Purple", Color(0xFF9C27B0)),
    BrandColorOption("Teal", Color(0xFF009688))
)