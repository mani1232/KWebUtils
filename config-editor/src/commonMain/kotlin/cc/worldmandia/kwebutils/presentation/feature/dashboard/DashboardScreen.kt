package cc.worldmandia.kwebutils.presentation.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.lyricist.strings
import cc.worldmandia.kwebutils.domain.model.ProjectFile
import cc.worldmandia.kwebutils.presentation.common.MainFont
import cc.worldmandia.kwebutils.presentation.feature.dashboard.components.CustomDashBoardAppBar
import cc.worldmandia.kwebutils.presentation.feature.dashboard.components.FileCard
import cc.worldmandia.kwebutils.presentation.feature.dashboard.components.FileUploadCard
import com.materialkolor.DynamicMaterialThemeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.CupertinoMaterials
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.rememberHazeState
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitPickerState
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@OptIn(
    ExperimentalMaterial3ExpressiveApi::class, ExperimentalComposeUiApi::class, ExperimentalHazeMaterialsApi::class
)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel, onFileOpen: (ProjectFile) -> Unit, themeState: DynamicMaterialThemeState
) {
    val files by viewModel.files.collectAsStateWithLifecycle()

    var isBlurEnabled by remember { mutableStateOf(false) }
    val hazeState = rememberHazeState(isBlurEnabled)

    val launcher = rememberFilePickerLauncher(
        mode = FileKitMode.MultipleWithState(maxItems = 5),
        type = FileKitType.File(extensions = listOf("yml", "yaml", "json", "json5")),
        title = "Open config files"
    ) { state ->
        if (state is FileKitPickerState.Completed) {
            viewModel.onFilesSelected(state.result)
        }
    }

    val callback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                return onDropDragAndDropEvent(viewModel).invoke(event)
            }

            override fun onStarted(event: DragAndDropEvent) {
                isBlurEnabled = true
                hazeState.blurEnabled = isBlurEnabled
            }

            override fun onEnded(event: DragAndDropEvent) {
                isBlurEnabled = false
                hazeState.blurEnabled = isBlurEnabled
            }


            override fun onExited(event: DragAndDropEvent) {
                isBlurEnabled = false
                hazeState.blurEnabled = isBlurEnabled
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { launcher.launch() },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Open File", fontFamily = MainFont) },
            )
        }, modifier = Modifier.fillMaxSize().dragAndDropTarget(
            shouldStartDragAndDrop = onDragAndDropEvent(), target = callback
        )
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize().hazeSource(hazeState)) {
            CustomDashBoardAppBar(
                onAmoledClick = {
                    themeState.isAmoled = it
                },
                onColorClick = {
                    themeState.seedColor = it
                }
            )

            Spacer(Modifier.height(16.dp))

            if (files.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    // TODO Temporary
                    Column {
                        Text("No open files.\nClick + to start.", fontFamily = MainFont)
                        Text(strings.annotated)
                    }
                    //Column(modifier = Modifier.width(400.dp).height(400.dp).align(Alignment.CenterStart)) {
                    //    TestChangeColor(MaterialTheme.colorScheme.surface)
                    //    GltfExample(MaterialTheme.colorScheme.surface)
                    //}
                    //Column(modifier = Modifier.width(400.dp).height(400.dp).align(Alignment.CenterEnd)) {
                    //    ObjFileExample(MaterialTheme.colorScheme.surface)
                    //}
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(files, key = { it.id }) { file ->
                        FileCard(file = file, onClick = { onFileOpen(file) })
                    }
                }
            }
        }

        if (isBlurEnabled) {
            Column(
                modifier = Modifier.fillMaxSize().background(
                    color = Color.Transparent,
                ).padding(64.dp).clip(RoundedCornerShape(16.dp))
                    .hazeEffect(state = hazeState, style = CupertinoMaterials.ultraThin())
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    FileUploadCard()
                }
            }
        }
    }
}

@Composable
expect fun WebBackButton(modifier: Modifier = Modifier)
expect fun onDragAndDropEvent(): (DragAndDropEvent) -> Boolean
expect fun onDropDragAndDropEvent(viewModel: DashboardViewModel): (DragAndDropEvent) -> Boolean