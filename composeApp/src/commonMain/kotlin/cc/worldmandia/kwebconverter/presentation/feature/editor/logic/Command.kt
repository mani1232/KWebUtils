package cc.worldmandia.kwebconverter.presentation.feature.editor.logic

interface Command {
    fun execute()
    fun undo()
}