package cc.worldmandia.kwebutils.presentation.feature.dashboard

import androidx.compose.ui.draganddrop.DragAndDropEvent

actual fun onDropDragAndDropEvent(viewModel: DashboardViewModel): (DragAndDropEvent) -> Boolean = { event ->
    false
}

actual fun onDragAndDropEvent(): (DragAndDropEvent) -> Boolean = { event ->
    false
}