package cc.worldmandia.kwebconverter.model

import cc.worldmandia.kwebconverter.ParserType
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.serialization.Serializable

@Serializable
data class FileItemModel(
    val originalFile: PlatformFile,
    val cachedOriginalContent: String? = null,
    var cachedEditedContent: String? = null,
    val parserType: ParserType
)