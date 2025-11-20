package cc.worldmandia.kwebconverter.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FileFormat {
    JSON, JSON5, YAML, UNSUPPORTED
}

@Serializable
data class ProjectFile(
    val id: String,
    val name: String,
    val extension: String,
    val content: String,
    val nameWithExtension: String = "$name.$extension",
    val format: FileFormat
)