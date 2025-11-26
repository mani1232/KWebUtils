package cc.worldmandia.kwebutils

import cc.worldmandia.kwebutils.router.Router
import cc.worldmandia.kwebutils.theme.ThemeModule
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.router.RouterProvider
import web.dom.document
import web.html.HtmlTagName.div


object IndexMenuApp {

    fun startIndexMenuApp() {
        val root = document.createElement(div)
            .also { document.body.appendChild(it) }

        createRoot(root)
            .render(indexMenuApp.create())
    }

    private val indexMenuApp = FC<Props> {
        ThemeModule {
            RouterProvider {
                router = Router
            }
        }
    }

}