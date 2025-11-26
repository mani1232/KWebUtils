package cc.worldmandia.kwebutils.router

import cc.worldmandia.kwebutils.page.Page
import js.objects.unsafeJso
import react.router.dom.createHashRouter
import remix.run.router.Router

val Router: Router = createHashRouter(
    routes = arrayOf(
        unsafeJso {
            path = "/"
            //loader = PageLoader
            Component = Page
            //ErrorBoundary = ErrorPage
            children = arrayOf(
                //unsafeJso {
                //    path = ":showcaseId"
                //    loader = ShowcaseMaterialLoader
                //    Component = ShowcaseMaterial
                //    ErrorBoundary = ErrorPage
                //},
                //unsafeJso {
                //    path = "*"
                //    Component = ErrorPage
                //}
            )
        },
    ),
)