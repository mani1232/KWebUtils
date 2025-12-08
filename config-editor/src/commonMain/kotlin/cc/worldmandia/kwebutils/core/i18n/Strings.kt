package cc.worldmandia.kwebutils.core.i18n

import androidx.compose.ui.text.AnnotatedString

data class Strings(
    val simple: String,
    val annotated: AnnotatedString,
    val parameter: (locale: String) -> String,
    val plural: (count: Int) -> String,
    val list: List<String>,
)
