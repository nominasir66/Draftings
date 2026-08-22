package com.example.model

import java.util.UUID

enum class TextAlignment {
    RIGHT,
    CENTER,
    LEFT,
    JUSTIFY
}

enum class TextDirection {
    RTL,
    LTR
}

enum class DocumentCategory(val displayNameUrdu: String, val displayNameEnglish: String) {
    ALL("تمام مسودات", "All"),
    AFFIDAVIT("بیان حلفی", "Affidavits"),
    AGREEMENT("معاہدہ جات", "Agreements"),
    APPLICATION("درخواستیں", "Applications"),
    POWER_OF_ATTORNEY("مختار نامہ", "Power of Attorney"),
    LEGAL_NOTICE("قانونی نوٹس", "Legal Notices"),
    CUSTOM("دیگر", "Custom")
}

data class MarginsModel(
    val topMm: Float = 20f,
    val bottomMm: Float = 20f,
    val leftMm: Float = 20f,
    val rightMm: Float = 20f
)

data class PageSizeModel(
    val widthMm: Float = 210f, // Standard A4 width
    val heightMm: Float = 297f // Standard A4 height
) {
    // Points at 72 DPI (Standard PDF Points)
    val widthPt: Float get() = (widthMm / 25.4f) * 72f
    val heightPt: Float get() = (heightMm / 25.4f) * 72f
}

data class TextRunModel(
    val text: String = "",
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val fontSizeSp: Float = 18f,
    val colorHex: String = "#0F172A"
)

data class ParagraphModel(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val fontSizeSp: Float = 18f,
    val alignment: TextAlignment = TextAlignment.RIGHT,
    val direction: TextDirection = TextDirection.RTL,
    val lineSpacing: Float = 1.4f,
    val isHeading: Boolean = false,
    val headingLevel: Int = 0, // 1 = Main Title, 2 = Subheading, 0 = Normal Body
    val isPageBreak: Boolean = false
)

data class TextBoxModel(
    val id: String = UUID.randomUUID().toString(),
    val pageIndex: Int = 0,
    val xPercent: Float = 0.08f, // Relative to page width (0.0 to 1.0)
    val yPercent: Float = 0.08f, // Relative to page height (0.0 to 1.0)
    val widthPercent: Float = 0.40f,
    val heightPercent: Float = 0.12f,
    val text: String = "خانہ تحریر / نوٹ",
    val fontFamily: String = "",
    val fontSizeSp: Float = 16f,
    val isBold: Boolean = false,
    val isUnderline: Boolean = false,
    val alignment: TextAlignment = TextAlignment.RIGHT,
    val direction: TextDirection = TextDirection.RTL,
    val showBorder: Boolean = true,
    val backgroundColorHex: String = "#00000000", // Transparent by default
    val borderColorHex: String = "#94A3B8"
)

data class PageModel(
    val pageNumber: Int = 1,
    val paragraphs: List<ParagraphModel> = listOf(ParagraphModel()),
    val textBoxes: List<TextBoxModel> = emptyList()
)

data class DocumentModel(
    val id: Long = 0,
    val title: String = "نیا قانونی مسودہ",
    val category: DocumentCategory = DocumentCategory.AFFIDAVIT,
    val pages: List<PageModel> = listOf(PageModel(pageNumber = 1)),
    val margins: MarginsModel = MarginsModel(),
    val pageSize: PageSizeModel = PageSizeModel(),
    val defaultFontFamily: String = "Jameel Noori Nastaleeq",
    val defaultFontSizeSp: Float = 18f,
    val defaultAlignment: TextAlignment = TextAlignment.RIGHT,
    val defaultDirection: TextDirection = TextDirection.RTL,
    val defaultLineSpacing: Float = 1.4f,
    val showMarginGuidelines: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
