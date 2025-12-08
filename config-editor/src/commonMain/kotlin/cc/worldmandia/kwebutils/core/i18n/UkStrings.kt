package cc.worldmandia.kwebutils.core.i18n

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "uk", default = false) // TODO Support com.vanniktech.locale.Language.UKRAINIAN
val UkStrings = Strings(
    simple = "Привіт, Compose!",

    annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) {
            append("Привіт, ")
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
            append("Compose!")
        }
    },

    parameter = { locale ->
        "Поточна локаль: $locale"
    },

    plural = { count ->
        val value = when (count) {
            0 -> "немає"
            1, 2 -> "кілька"
            in 3..10 -> "купа"
            else -> "багато"
        }
        "У мене $value яблук"
    },

    list = listOf("Авокадо", "Ананас", "Слива")
)