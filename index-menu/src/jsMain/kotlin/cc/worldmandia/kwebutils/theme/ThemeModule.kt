package cc.worldmandia.kwebutils.theme

import mui.material.CssBaseline
import mui.material.styles.ThemeProvider
import react.FC
import react.PropsWithChildren
import react.useState

val ThemeModule = FC<PropsWithChildren> { props ->
    val state = useState(Themes.Dark)
    val (theme) = state

    ThemeContext(state) {
        ThemeProvider {
            this.theme = theme

            CssBaseline()
            +props.children
        }
    }
}