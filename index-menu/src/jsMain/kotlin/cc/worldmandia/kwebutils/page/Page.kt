package cc.worldmandia.kwebutils.page

import emotion.styled.styled
import mui.material.Button
import mui.material.ButtonGroup
import mui.material.ButtonGroupVariant
import mui.system.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.onChange
import react.router.dom.NavLink
import web.cssom.*

val Page = FC {
    Box {
        sx {
            display = Display.grid
            height = 100.pct
        }
        ButtonGroupUtilsSelector
    }
}

val ButtonGroupUtilsSelector = FC<Props> {
    ButtonGroup {
        sx {
           alignItems = AlignItems.center
        }
        variant = ButtonGroupVariant.contained
        ariaLabel = "outlined primary button group"

        LinkButton {
            to = "config-editor"
            +"Config Editor"
        }

        Button {
            +"In dev"
        }
    }
}

private val LinkButton = NavLink.styled {
    textDecoration = None.none
    color = Color.currentColor
}