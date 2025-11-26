package cc.worldmandia.kwebutils.theme

import js.objects.unsafeJso
import kotlinx.browser.localStorage
import mui.material.CssBaseline
import mui.material.PaletteMode
import mui.material.styles.Theme
import mui.material.styles.ThemeProvider
import mui.material.styles.createTheme
import mui.system.useMediaQuery
import react.*

val LightTheme = createTheme(unsafeJso {
    palette = unsafeJso { mode = PaletteMode.light }
})

val DarkTheme = createTheme(unsafeJso {
    palette = unsafeJso { mode = PaletteMode.dark }
})

data class ThemeContextType(
    val theme: Theme,
    val toggleTheme: () -> Unit,
    val isAuto: Boolean
)

val ThemeContext = createContext<ThemeContextType>()

val ThemeModule = FC<PropsWithChildren> { props ->
    val systemPrefersDark = useMediaQuery("(prefers-color-scheme: dark)")

    var userPrefersDark by useState {
        val savedTheme = localStorage.getItem("app_theme_pref")
        when (savedTheme) {
            "dark" -> true
            "light" -> false
            else -> null
        }
    }

    useEffect(userPrefersDark) {
        when (userPrefersDark) {
            true -> localStorage.setItem("app_theme_pref", "dark")
            false -> localStorage.setItem("app_theme_pref", "light")
            null -> localStorage.removeItem("app_theme_pref")
        }
    }

    val isDark = userPrefersDark ?: systemPrefersDark

    val currentTheme = useMemo(isDark) {
        if (isDark) DarkTheme else LightTheme
    }

    val contextValue = useMemo(isDark, userPrefersDark, systemPrefersDark) {
        ThemeContextType(
            theme = currentTheme,
            toggleTheme = {
                userPrefersDark = !isDark
            },
            isAuto = userPrefersDark == null
        )
    }

    ThemeContext.Provider(contextValue) {
        ThemeProvider {
            this.theme = currentTheme
            CssBaseline()
            +props.children
        }
    }
}

fun useAppTheme() = use(ThemeContext) ?: error("ThemeContext not found")