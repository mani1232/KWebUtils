package cc.worldmandia.kwebconverter.presentation.feature.editor.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CommandManager(private val maxHistory: Int = 50) {
    private val _undoStack = ArrayDeque<Command>()
    private val _redoStack = ArrayDeque<Command>()

    var canUndo by mutableStateOf(false)
        private set
    var canRedo by mutableStateOf(false)
        private set

    fun execute(command: Command) {
        command.execute()
        _undoStack.addLast(command)
        if (_undoStack.size > maxHistory) _undoStack.removeFirst()
        _redoStack.clear()
        updateState()
    }

    fun undo() {
        if (_undoStack.isNotEmpty()) {
            val command = _undoStack.removeLast()
            command.undo()
            _redoStack.addLast(command)
            updateState()
        }
    }

    fun redo() {
        if (_redoStack.isNotEmpty()) {
            val command = _redoStack.removeLast()
            command.execute()
            _undoStack.addLast(command)
            updateState()
        }
    }

    fun clear() {
        _undoStack.clear()
        _redoStack.clear()
        updateState()
    }

    private fun updateState() {
        canUndo = _undoStack.isNotEmpty()
        canRedo = _redoStack.isNotEmpty()
    }
}