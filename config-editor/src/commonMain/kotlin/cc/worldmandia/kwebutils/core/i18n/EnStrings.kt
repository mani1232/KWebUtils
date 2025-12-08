package cc.worldmandia.kwebutils.core.i18n

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import cafe.adriel.lyricist.LyricistStrings

@LyricistStrings(languageTag = "en", default = true) // TODO Support com.vanniktech.locale.Language.ENGLISH
val EnStrings = Strings(
    simple = "Hello Compose!",

    annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = Color.Red)) {
            append("Hello ")
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Light)) {
            append("Compose!")
        }
    },

    parameter = { locale ->
        "Current locale: $locale"
    },

    plural = { count ->
        val value = when (count) {
            0 -> "no"
            1, 2 -> "a few"
            in 3..10 -> "a bunch of"
            else -> "a lot of"
        }
        "I have $value apples"
    },

    list = listOf("Avocado", "Pineapple", "Plum")
)