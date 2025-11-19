package cc.worldmandia.kwebconverter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.worldmandia.kwebconverter.model.FileItemModel
import cc.worldmandia.kwebconverter.parserType
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FilesViewModel : ViewModel() {

    private val _files = MutableStateFlow<List<FileItemModel>>(emptyList())
    val files = _files.asStateFlow()

    fun loadFile(file: PlatformFile) {
        _files.update { currentList ->
            currentList + FileItemModel(file, parserType = file.parserType())
        }
    }

    fun loadFile(files: List<PlatformFile>) {
        _files.update { currentList ->
            currentList + files.map { FileItemModel(it, parserType = it.parserType()) }
        }
    }

    fun removeString(index: Int) {
        _files.update { currentList ->
            if (index in currentList.indices) {
                currentList.toMutableList().apply { removeAt(index) }
            } else {
                currentList
            }
        }
    }

    fun loadFilesContent() {
        viewModelScope.launch {
            _files.update { current ->
                current.map { file ->
                    file.copy(
                        cachedOriginalContent = file.originalFile.readString()
                    )
                }
            }
        }
    }
}