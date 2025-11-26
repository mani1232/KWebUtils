package cc.worldmandia.kwebutils.material

import emotion.styled.styled
import kotlinx.browser.window
import mui.icons.material.Launch
import mui.material.Box
import mui.material.Button
import mui.material.ButtonColor
import mui.material.ButtonVariant
import mui.material.styles.Theme
import react.FC
import react.Props
import react.ReactNode
import react.create
import web.cssom.*

val PageContainer = Box.styled { props ->
    val theme = props.asDynamic().theme.unsafeCast<Theme>()

    display = Display.flex
    flexDirection = FlexDirection.column
    alignItems = AlignItems.center
    justifyContent = JustifyContent.center
    height = 100.vh

    backgroundColor = theme.palette.background.default.asDynamic()
    color = theme.palette.text.primary

    transition = Transition(PropertyName.backgroundColor, 0.3.s, TransitionTimingFunction.ease)
    transition = Transition(PropertyName.color, 0.3.s, TransitionTimingFunction.ease)
}

val StyledLaunchBtn = Button.styled {
    width = 220.px
    height = 50.px
    margin = 10.px
}

external interface LaunchButtonProps : Props {
    var appName: String
    var folderName: String
    var icon: ReactNode?
    var onLaunch: () -> Unit
}

val LaunchButton = FC<LaunchButtonProps> { props ->
    StyledLaunchBtn {
        variant = ButtonVariant.contained
        color = ButtonColor.primary
        size = mui.material.Size.large
        startIcon = props.icon ?: Launch.create()

        onClick = {
            props.onLaunch()
            window.setTimeout({
                window.location.href = "/${props.folderName}/"
            }, 500)
        }
        +props.appName
    }
}