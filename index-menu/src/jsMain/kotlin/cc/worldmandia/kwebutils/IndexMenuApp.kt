package cc.worldmandia.kwebutils

import cc.worldmandia.kwebutils.material.DescriptionText
import cc.worldmandia.kwebutils.material.LaunchButton
import cc.worldmandia.kwebutils.material.PageContainer
import cc.worldmandia.kwebutils.theme.ThemeModule
import cc.worldmandia.kwebutils.theme.useAppTheme
import mui.icons.material.Brightness4
import mui.icons.material.Brightness7
import mui.icons.material.Construction
import mui.icons.material.Tune
import mui.material.*
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML
import react.raw.useEffectRaw
import react.useState
import web.cssom.*
import web.dom.document
import web.events.Event
import web.events.EventType
import web.events.addEventListener
import web.events.removeEventListener
import web.window.window

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
        var isLoading by useState(false)

        useEffectRaw(cleanup@{
            val handlePageShow = { event: Event ->
                if (event.asDynamic().persisted as Boolean) {
                    isLoading = false
                }
            }

            window.addEventListener(EventType("pageshow"), handlePageShow)

            return@cleanup {
                window.removeEventListener(EventType("pageshow"), handlePageShow)
            }
        })

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
                        onLaunch = { isLoading = true }
                    }

                    LaunchButton {
                        appName = "In dev"
                        folderName = "in-dev"
                        icon = Construction.create()
                        onLaunch = { isLoading = true }
                    }
                }
            }

            Backdrop {
                open = isLoading
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