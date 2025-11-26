package cc.worldmandia.kwebutils.presentation.feature.editor.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.worldmandia.kwebutils.domain.model.FileFormat
import cc.worldmandia.kwebutils.presentation.feature.editor.logic.CommandManager
import cc.worldmandia.kwebutils.presentation.feature.editor.logic.ReorderItemCommand
import cc.worldmandia.kwebutils.presentation.feature.editor.logic.ReorderMapEntryCommand
import cc.worldmandia.kwebutils.presentation.feature.editor.model.*
import cc.worldmandia.kwebutils.presentation.model.EditableList
import cc.worldmandia.kwebutils.presentation.model.EditableMap
import cc.worldmandia.kwebutils.presentation.model.EditableMapEntry
import cc.worldmandia.kwebutils.presentation.model.EditableNode
import com.mohamedrejeb.compose.dnd.annotation.ExperimentalDndApi
import com.mohamedrejeb.compose.dnd.drag.DropStrategy
import com.mohamedrejeb.compose.dnd.reorder.ReorderContainer
import com.mohamedrejeb.compose.dnd.reorder.ReorderableItem
import com.mohamedrejeb.compose.dnd.reorder.rememberReorderState

@OptIn(ExperimentalDndApi::class)
@Composable
fun EditorContent(
    rootNode: EditableNode,
    cmdManager: CommandManager,
    parserType: FileFormat
) {
    // Логика Flattening
    val uiItems by remember(rootNode, cmdManager.canUndo, cmdManager.canRedo) {
        derivedStateOf { flattenTree(rootNode, cmd = cmdManager) }
    }

    var focusedNode by remember { mutableStateOf<EditableNode?>(null) }
    val reorderState = rememberReorderState<UiNode>()

    Column(Modifier.fillMaxSize()) {
        ReorderContainer(
            state = reorderState,
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(items = uiItems, key = { it.id }) { item ->
                    when (item) {
                        is UiNode -> {
                            ReorderableItem(
                                state = reorderState,
                                key = item.id,
                                data = item,
                                dropStrategy = DropStrategy.CenterDistance,
                                onDrop = {},
                                onDragEnter = { dropState ->
                                    val fromItem = dropState.data
                                    val toItem = item

                                    // ПРОВЕРКА: Разрешаем перестановку только внутри одного родителя (List или Map)
                                    val fromNode = fromItem.node
                                    val toNode = toItem.node
                                    val parent = toNode.parent

                                    // 1. Если родитель - Список
                                    if (parent is EditableList && fromNode.parent === parent) {
                                        val fromIndex = parent.items.indexOf(fromNode)
                                        val toIndex = parent.items.indexOf(toNode)
                                        if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
                                            cmdManager.execute(ReorderItemCommand(parent, fromIndex, toIndex))
                                        }
                                    }
                                    // 2. Если родитель - Карта (Map)
                                    if (parent is EditableMapEntry && fromNode.parent is EditableMapEntry) {
                                        val targetMap = parent.parentMap
                                        val draggedMap = (fromNode.parent as EditableMapEntry).parentMap
                                        if (targetMap === draggedMap) {
                                            val fromIndex = targetMap.entries.indexOf(fromNode.parent)
                                            val toIndex = targetMap.entries.indexOf(parent)
                                            if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
                                                cmdManager.execute(
                                                    ReorderMapEntryCommand(
                                                        targetMap,
                                                        fromIndex,
                                                        toIndex
                                                    )
                                                )
                                            }
                                        }
                                    }
                                },
                                draggableContent = {
                                    val isContainer = item.node is EditableList || item.node is EditableMap

                                    Surface(
                                        shadowElevation = 12.dp,
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        border = BorderStroke(
                                            1.dp,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(unbounded = true, align = Alignment.Top)
                                    ) {
                                        Column(Modifier.padding(8.dp)) {
                                            NodeRow(
                                                item = item,
                                                isDuplicate = false,
                                                cmdManager = cmdManager,
                                                isDragging = false,
                                                onFocus = {}
                                            )

                                            if (isContainer) {
                                                val node = item.node
                                                val isExpanded = (node as? EditableList)?.isExpanded == true ||
                                                        (node as? EditableMap)?.isExpanded == true

                                                if (isExpanded) {
                                                    HorizontalDivider(
                                                        modifier = Modifier.padding(vertical = 4.dp),
                                                        color = MaterialTheme.colorScheme.outlineVariant
                                                    )

                                                    val previewLimit = 5
                                                    val (children, totalCount) = when (node) {
                                                        is EditableList -> {
                                                            val items = node.items
                                                            items.take(previewLimit).mapIndexed { index, child ->
                                                                UiNode(
                                                                    id = "${item.id}_preview_$index",
                                                                    node = child,
                                                                    keyInfo = ListIndex(index),
                                                                    level = item.level + 1,
                                                                    onDelete = {}
                                                                )
                                                            } to items.size
                                                        }

                                                        is EditableMap -> {
                                                            val entries = node.entries
                                                            entries.take(previewLimit).map { entry ->
                                                                UiNode(
                                                                    id = "${item.id}_preview_${entry.id}",
                                                                    node = entry.value,
                                                                    keyInfo = MapKey(entry.keyState, entry),
                                                                    level = item.level + 1,
                                                                    onDelete = {}
                                                                )
                                                            } to entries.size
                                                        }
                                                    }

                                                    children.forEach { childUi ->
                                                        NodeRow(
                                                            item = childUi,
                                                            isDuplicate = false,
                                                            cmdManager = cmdManager,
                                                            isDragging = false, // Важно: контент видим
                                                            onFocus = {}
                                                        )
                                                    }

                                                    if (totalCount > previewLimit) {
                                                        val indentDp =
                                                            ((item.level + 1) * 20).dp
                                                        Text(
                                                            text = "... еще ${totalCount - previewLimit} ...",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(
                                                                start = indentDp + 8.dp,
                                                                top = 4.dp,
                                                                bottom = 4.dp
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            ) {
                                NodeRow(
                                    item = item,
                                    isDuplicate = false, // Реализуйте проверку дубликатов если нужно
                                    cmdManager = cmdManager,
                                    isDragging = isDragging,
                                    onFocus = { focusedNode = it }
                                )
                            }
                        }

                        is UiAddAction -> {
                            // Используйте parserType (FileFormat) для фильтрации типов
                            AddActionRow(item, parserType)
                        }
                    }
                }
            }
        }

        // Bottom Breadcrumbs
        Surface(tonalElevation = 3.dp) {
            Row(Modifier.fillMaxWidth().padding(8.dp)) {
                Breadcrumbs(focusedNode) { target ->
                    focusedNode = target
                    target.requestFocus = true
                }
            }
        }
    }
}