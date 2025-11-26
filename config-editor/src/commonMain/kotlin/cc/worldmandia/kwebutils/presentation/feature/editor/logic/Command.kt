package cc.worldmandia.kwebutils.presentation.feature.editor.logic

interface Command {
    fun execute()
    fun undo()
}