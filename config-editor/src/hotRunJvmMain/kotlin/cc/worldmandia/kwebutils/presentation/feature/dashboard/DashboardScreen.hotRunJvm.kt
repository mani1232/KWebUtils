package cc.worldmandia.kwebutils.presentation.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent

@Composable
actual fun WebBackButton(modifier: Modifier) {}

actual fun onDropDragAndDropEvent(viewModel: DashboardViewModel): (DragAndDropEvent) -> Boolean = { event ->
    false
}

actual fun onDragAndDropEvent(): (DragAndDropEvent) -> Boolean = { event ->
    false
}