package com.example.typography

import android.content.Context
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import java.io.File

object FontHelper {

    const val FONT_JAMEEL_NASTALEEQ = "Jameel Noori Nastaleeq"
    const val FONT_SYSTEM_SERIF = "System Serif"
    const val FONT_SYSTEM_SANS = "System Sans-Serif"
    const val FONT_MONOSPACE = "Monospace"

    val AVAILABLE_FONTS = listOf(
        FONT_JAMEEL_NASTALEEQ to "جمیل نوری نستعلیق (Jameel Noori Nastaleeq)",
        FONT_SYSTEM_SERIF to "خط نسخ (System Serif)",
        FONT_SYSTEM_SANS to "سادہ ماڈرن (System Sans-Serif)",
        FONT_MONOSPACE to "یکساں فاصلہ (Monospace)"
    )

    private var cachedNastaleeqTypeface: Typeface? = null

    fun getNastaleeqTypeface(context: Context): Typeface {
        cachedNastaleeqTypeface?.let { return it }
        return try {
            val tf = Typeface.createFromAsset(context.assets, "Jameel Noori Nastaleeq Regular.ttf")
            cachedNastaleeqTypeface = tf
            tf
        } catch (e: Exception) {
            try {
                Typeface.create("serif", Typeface.NORMAL)
            } catch (ex: Exception) {
                Typeface.DEFAULT
            }
        }
    }

    fun getTypefaceByName(context: Context, fontName: String, isBold: Boolean, isItalic: Boolean): Typeface {
        val baseTypeface = when (fontName) {
            FONT_JAMEEL_NASTALEEQ -> getNastaleeqTypeface(context)
            FONT_SYSTEM_SERIF -> Typeface.SERIF
            FONT_MONOSPACE -> Typeface.MONOSPACE
            else -> Typeface.SANS_SERIF
        }

        val style = when {
            isBold && isItalic -> Typeface.BOLD_ITALIC
            isBold -> Typeface.BOLD
            isItalic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }

        return try {
            Typeface.create(baseTypeface, style)
        } catch (e: Exception) {
            baseTypeface
        }
    }

    @Composable
    fun getComposeFontFamily(fontName: String): FontFamily {
        val context = LocalContext.current
        return remember(fontName) {
            when (fontName) {
                FONT_JAMEEL_NASTALEEQ -> {
                    try {
                        FontFamily(
                            androidx.compose.ui.text.font.Font(
                                "Jameel Noori Nastaleeq Regular.ttf",
                                context.assets,
                                FontWeight.Normal,
                                FontStyle.Normal
                            )
                        )
                    } catch (e: Exception) {
                        FontFamily.Serif
                    }
                }
                FONT_SYSTEM_SERIF -> FontFamily.Serif
                FONT_MONOSPACE -> FontFamily.Monospace
                else -> FontFamily.SansSerif
            }
        }
    }
}
