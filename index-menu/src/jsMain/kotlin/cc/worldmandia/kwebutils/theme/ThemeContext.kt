package cc.worldmandia.kwebutils.theme

import js.objects.unsafeJso
import mui.material.CssBaseline
import mui.material.PaletteMode
import mui.material.styles.Theme
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import react.*

val LightTheme = createTheme(unsafeJso {
    palette = unsafeJso { mode = PaletteMode.light }
})

val DarkTheme = createTheme(unsafeJso {
    palette = unsafeJso { mode = PaletteMode.dark }
})

data class ThemeContextType(
    val theme: Theme,
    val toggleTheme: () -> Unit
)

val ThemeContext = createContext<ThemeContextType>()

val ThemeModule = FC<PropsWithChildren> { props ->
    var isDark by useState(false)

    val currentTheme = if (isDark) DarkTheme else LightTheme

    val contextValue = ThemeContextType(
        theme = currentTheme,
        toggleTheme = { isDark = !isDark }
    )

    ThemeContext.Provider(contextValue) {
        ThemeProvider {
            this.theme = currentTheme
            CssBaseline()
            +props.children
        }
    }
}

fun useAppTheme() = use(ThemeContext) ?: error("ThemeContext not found")