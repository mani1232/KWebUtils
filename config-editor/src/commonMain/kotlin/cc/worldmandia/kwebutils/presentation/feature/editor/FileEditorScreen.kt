package cc.worldmandia.kwebutils.presentation.feature.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cc.worldmandia.kwebutils.presentation.feature.editor.components.EditorContent
import cc.worldmandia.kwebutils.presentation.feature.editor.components.EditorTopBar
import cc.worldmandia.kwebutils.presentation.feature.editor.components.ParseError
import cc.worldmandia.kwebutils.presentation.feature.editor.mapper.NodeSerializer

@Composable
fun FileEditorScreen(
    viewModel: EditorViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize().onPreviewKeyEvent { event ->
            if (event.type == KeyEventType.KeyDown && event.isCtrlPressed) {
                when (event.key) {
                    Key.Z -> {
                        if (event.isShiftPressed) viewModel.redo() else viewModel.undo(); true
                    }

                    Key.Y -> {
                        viewModel.redo(); true
                    }

                    Key.S -> {
                        viewModel.saveFile(); true
                    }

                    else -> false
                }
            } else false
        },
        topBar = {
            (uiState as? EditorUiState.Content)?.let { state ->
                EditorTopBar(
                    title = state.fileInfo.name,
                    type = state.fileInfo.format,
                    cmdManager = viewModel.commandManager,
                    onBack = onBack,
                    onSave = viewModel::saveFile,
                    onReset = viewModel::resetFile,
                    onGenerateContent = {
                        NodeSerializer.serializeForClipboard(state.root.rootNode)
                    },
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    rootNode = state.root.rootNode
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is EditorUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is EditorUiState.Error -> ParseError(state.message, onBack)
                is EditorUiState.Content -> {
                    EditorContent(
                        rootNode = state.root.rootNode,
                        cmdManager = viewModel.commandManager,
                        parserType = state.fileInfo.format
                    )
                }
            }
        }
    }
}