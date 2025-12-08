package cc.worldmandia.kwebutils

import cc.worldmandia.kwebutils.material.DescriptionText
import cc.worldmandia.kwebutils.material.LaunchButton
import cc.worldmandia.kwebutils.material.PageContainer
import cc.worldmandia.kwebutils.theme.ThemeModule
import cc.worldmandia.kwebutils.theme.useAppTheme
import mui.icons.material.*
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import web.cssom.*
import web.dom.document

object IndexMenuApp {

    fun startIndexMenuApp() {
        val root = document.createElement("div").also { document.body.appendChild(it) }

        createRoot(root).render(
            ThemeModule.create {
                MenuContent()
            }
        )
    }

    private val MenuContent = FC<Props> {
        val (currentTheme, toggleTheme) = useAppTheme()
        PageContainer {
            Box {
                sx {
                    position = Position.absolute
                    top = 20.px
                    right = 20.px
                }
                IconButton {
                    onClick = { toggleTheme() }
                    if (currentTheme.palette.mode == PaletteMode.dark) {
                        Brightness7()
                    } else {
                        Brightness4()
                    }
                }
            }

            Container {
                maxWidth = "sm"
                sx { textAlign = TextAlign.center }

                Typography {
                    variant = TypographyVariant.h2
                    component = ReactHTML.h1
                    sx { marginBottom = 40.px; fontWeight = FontWeight.bold }
                    +"KWebUtils"
                }

                DescriptionText {
                    variant = TypographyVariant.body1

                    +"Welcome to the demo showcase! "
                    +"Here you can explore example applications written in Kotlin Multiplatform "
                    +"and compiled to WebAssembly."
                }

                Stack {
                    sx { alignItems = AlignItems.center }

                    LaunchButton {
                        appName = "Config Editor"
                        folderName = "config-editor"
                        icon = Tune.create()
                    }

                    LaunchButton {
                        appName = "In dev"
                        folderName = "ai-chat"
                        icon = Chat.create()
                    }

                    LaunchButton {
                        appName = "In dev"
                        folderName = "other-things"
                        icon = Construction.create()
                    }
                }
            }

            Backdrop {
                sx { zIndex = integer(9999); color = Color("#fff") }

                Stack {
                    sx { alignItems = AlignItems.center; gap = 20.px }
                    CircularProgress { color = CircularProgressColor.inherit }
                    Typography { variant = TypographyVariant.h6; +"Launch..." }
                }
            }
        }
    }
}