package cc.worldmandia.kwebconverter.presentation.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.worldmandia.kwebconverter.domain.model.ProjectFile
import cc.worldmandia.kwebconverter.presentation.common.MainFont
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onFileOpen: (ProjectFile) -> Unit
) {
    val files by viewModel.files.collectAsStateWithLifecycle()

    val launcher = rememberFilePickerLauncher(
        mode = FileKitMode.MultipleWithState(maxItems = 5),
        type = FileKitType.File(extensions = listOf("yml", "yaml", "json", "json5")),
        title = "Open config files"
    ) { state ->
        if (state is io.github.vinceglb.filekit.dialogs.FileKitPickerState.Completed) {
            viewModel.onFilesSelected(state.result)
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { launcher.launch() },
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Open File", fontFamily = MainFont) },
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Projects", style = MaterialTheme.typography.headlineMedium, fontFamily = MainFont)
            Spacer(Modifier.height(16.dp))

            if (files.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No open files.\nClick + to start.", fontFamily = MainFont)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(files, key = { it.id }) { file ->
                        FileCard(file, onClick = { onFileOpen(file) })
                    }
                }
            }
        }
    }
}

@Composable
fun FileCard(file: ProjectFile, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Description, null, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.titleMedium, fontFamily = MainFont)
                Text(file.format.name, style = MaterialTheme.typography.bodySmall)
            }
            FilledTonalButton(onClick = onClick) {
                Icon(Icons.Default.Edit, null, Modifier.size(16.dp))
                Text("Edit", Modifier.padding(start = 8.dp))
            }
        }
    }
}