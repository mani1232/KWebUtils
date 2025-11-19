package cc.worldmandia.kwebconverter.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.worldmandia.kwebconverter.FileParser
import cc.worldmandia.kwebconverter.NodeSerializer
import cc.worldmandia.kwebconverter.getNodePath
import cc.worldmandia.kwebconverter.logic.CommandManager
import cc.worldmandia.kwebconverter.model.FileItemModel
import cc.worldmandia.kwebconverter.ui.*
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileEditorScreen(
    file: FileItemModel,
    back: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val cmdManager = remember { CommandManager() }

    // --- State ---
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var focusedPath by remember { mutableStateOf("root") }

    // Debounce Search
    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedQuery = searchQuery
    }

    val editableRoot = remember(file) { FileParser.parseFile(file) }

    val uiItems by remember(editableRoot?.rootNode, cmdManager.canUndo, cmdManager.canRedo) {
        derivedStateOf {
            editableRoot?.rootNode?.let { flattenTree(it, cmd = cmdManager) } ?: emptyList()
        }
    }

    val displayItems by remember(uiItems, debouncedQuery) {
        derivedStateOf {
            if (debouncedQuery.isEmpty()) uiItems
            else uiItems.filter { (it as? UiNode)?.matches(debouncedQuery) ?: true }
        }
    }

    val duplicateIds by remember(uiItems) {
        derivedStateOf {
            uiItems.asSequence().filterIsInstance<UiNode>()
                .filter { it.keyInfo is MapKey && it.mapId != null }
                .groupBy { Pair(it.mapId, (it.keyInfo as MapKey).state.text.toString()) }
                .filter { it.value.size > 1 }
                .flatMap { it.value }
                .map { it.id }
                .toSet()
        }
    }

    val generateContent: () -> String? = remember(editableRoot, file.parserType) {
        {
            if (editableRoot != null) {
                NodeSerializer.serialize(editableRoot.rootNode, file.parserType)
            } else null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EditorTopBar(
                title = file.originalFile.name,
                type = file.parserType,
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                cmdManager = cmdManager,
                onBack = back,
                onSave = {

                },
                onGenerateContent = generateContent
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.height(32.dp)) {
                Text(
                    text = focusedPath,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    ) { padding ->
        if (editableRoot == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Parse Error", color = MaterialTheme.colorScheme.error)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(items = displayItems, key = { it.id }) { item ->
                    when (item) {
                        is UiNode -> NodeRow(
                            item = item,
                            isDuplicate = duplicateIds.contains(item.id),
                            cmdManager = cmdManager,
                            onFocus = { getNodePath(it) }
                        )

                        is UiAddAction -> AddActionRow(item, file.parserType)
                    }
                }
            }
        }
    }
}