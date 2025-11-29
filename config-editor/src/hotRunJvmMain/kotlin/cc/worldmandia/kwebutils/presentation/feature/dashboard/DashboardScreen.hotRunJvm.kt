package cc.worldmandia.kwebutils.presentation.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.draganddrop.DragAndDropEvent

@Composable
actual fun WebBackButton() {}

actual fun onDropDragAndDropEvent(viewModel: DashboardViewModel): (DragAndDropEvent) -> Boolean = { event ->
    false
}

actual fun onDragAndDropEvent(): (DragAndDropEvent) -> Boolean = { event ->
    false
}