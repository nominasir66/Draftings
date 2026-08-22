package com.example.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.model.*
import com.example.typography.FontHelper
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    // A4 Standard Dimensions at 72 DPI (Points)
    const val PAGE_WIDTH_PT = 595
    const val PAGE_HEIGHT_PT = 842

    fun exportToPdf(context: Context, document: DocumentModel): File {
        val pdfDocument = PdfDocument()

        val topMarginPt = (document.margins.topMm / 25.4f) * 72f
        val bottomMarginPt = (document.margins.bottomMm / 25.4f) * 72f
        val leftMarginPt = (document.margins.leftMm / 25.4f) * 72f
        val rightMarginPt = (document.margins.rightMm / 25.4f) * 72f

        val printableWidth = PAGE_WIDTH_PT - leftMarginPt - rightMarginPt
        val printableHeight = PAGE_HEIGHT_PT - topMarginPt - bottomMarginPt

        val totalPages = document.pages.size

        for (pageIndex in 0 until totalPages) {
            val pageModel = document.pages[pageIndex]
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH_PT, PAGE_HEIGHT_PT, pageIndex + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Fill white background
            canvas.drawColor(Color.WHITE)

            // Render Page Header / Document Rule if on subsequent pages
            if (pageIndex > 0) {
                val headerPaint = TextPaint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 9f
                    isAntiAlias = true
                    typeface = FontHelper.getTypefaceByName(context, document.defaultFontFamily, isBold = false, isItalic = false)
                }
                canvas.drawText(document.title, rightMarginPt + printableWidth - headerPaint.measureText(document.title), topMarginPt - 15f, headerPaint)
                
                val rulePaint = Paint().apply {
                    color = Color.parseColor("#E2E8F0")
                    strokeWidth = 0.5f
                }
                canvas.drawLine(leftMarginPt, topMarginPt - 8f, leftMarginPt + printableWidth, topMarginPt - 8f, rulePaint)
            }

            // Render Paragraphs
            var currentY = topMarginPt
            for (para in pageModel.paragraphs) {
                if (para.text.isBlank() && !para.isPageBreak) {
                    currentY += (para.fontSizeSp * 0.8f)
                    continue
                }

                val typeface = FontHelper.getTypefaceByName(
                    context,
                    document.defaultFontFamily,
                    isBold = para.isBold,
                    isItalic = para.isItalic
                )

                val textPaint = TextPaint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = para.fontSizeSp * 0.95f // Scaled to pt
                    isAntiAlias = true
                    this.typeface = typeface
                    isUnderlineText = para.isUnderline
                }

                val alignment = when (para.alignment) {
                    TextAlignment.CENTER -> Layout.Alignment.ALIGN_CENTER
                    TextAlignment.LEFT -> Layout.Alignment.ALIGN_NORMAL
                    TextAlignment.RIGHT, TextAlignment.JUSTIFY -> Layout.Alignment.ALIGN_OPPOSITE
                }

                val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    StaticLayout.Builder.obtain(para.text, 0, para.text.length, textPaint, printableWidth.toInt())
                        .setAlignment(alignment)
                        .setLineSpacing(0f, para.lineSpacing)
                        .setIncludePad(true)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    StaticLayout(
                        para.text,
                        textPaint,
                        printableWidth.toInt(),
                        alignment,
                        para.lineSpacing,
                        0f,
                        true
                    )
                }

                canvas.save()
                canvas.translate(leftMarginPt, currentY)
                staticLayout.draw(canvas)
                canvas.restore()

                val paraSpacing = if (para.isHeading) 14f else 8f
                currentY += staticLayout.height + paraSpacing
            }

            // Render Text Boxes on this page
            for (box in pageModel.textBoxes) {
                val boxX = box.xPercent * PAGE_WIDTH_PT
                val boxY = box.yPercent * PAGE_HEIGHT_PT
                val boxWidth = box.widthPercent * PAGE_WIDTH_PT
                val boxHeight = box.heightPercent * PAGE_HEIGHT_PT

                val boxRect = RectF(boxX, boxY, boxX + boxWidth, boxY + boxHeight)

                // Box background if set
                if (box.backgroundColorHex != "#00000000") {
                    try {
                        val bgPaint = Paint().apply {
                            color = Color.parseColor(box.backgroundColorHex)
                            style = Paint.Style.FILL
                        }
                        canvas.drawRoundRect(boxRect, 4f, 4f, bgPaint)
                    } catch (e: Exception) { }
                }

                // Box border if enabled
                if (box.showBorder) {
                    val borderPaint = Paint().apply {
                        color = try { Color.parseColor(box.borderColorHex) } catch (e: Exception) { Color.parseColor("#94A3B8") }
                        style = Paint.Style.STROKE
                        strokeWidth = 1f
                    }
                    canvas.drawRoundRect(boxRect, 4f, 4f, borderPaint)
                }

                // Draw Text Box Content
                val boxPadding = 6f
                val innerWidth = (boxWidth - (boxPadding * 2)).coerceAtLeast(10f)
                val boxFontFamily = if (box.fontFamily.isNotBlank()) box.fontFamily else document.defaultFontFamily
                val boxTypeface = FontHelper.getTypefaceByName(
                    context,
                    boxFontFamily,
                    isBold = box.isBold,
                    isItalic = false
                )

                val boxTextPaint = TextPaint().apply {
                    color = Color.parseColor("#0F172A")
                    textSize = box.fontSizeSp * 0.9f
                    isAntiAlias = true
                    typeface = boxTypeface
                    isUnderlineText = box.isUnderline
                }

                val boxAlignment = when (box.alignment) {
                    TextAlignment.CENTER -> Layout.Alignment.ALIGN_CENTER
                    TextAlignment.LEFT -> Layout.Alignment.ALIGN_NORMAL
                    TextAlignment.RIGHT, TextAlignment.JUSTIFY -> Layout.Alignment.ALIGN_OPPOSITE
                }

                val boxLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    StaticLayout.Builder.obtain(box.text, 0, box.text.length, boxTextPaint, innerWidth.toInt())
                        .setAlignment(boxAlignment)
                        .setIncludePad(true)
                        .build()
                } else {
                    @Suppress("DEPRECATION")
                    StaticLayout(
                        box.text,
                        boxTextPaint,
                        innerWidth.toInt(),
                        boxAlignment,
                        1.2f,
                        0f,
                        true
                    )
                }

                canvas.save()
                canvas.translate(boxX + boxPadding, boxY + boxPadding)
                boxLayout.draw(canvas)
                canvas.restore()
            }

            // Render Page Number in Footer
            val footerPaint = TextPaint().apply {
                color = Color.parseColor("#94A3B8")
                textSize = 9f
                isAntiAlias = true
                typeface = FontHelper.getTypefaceByName(context, document.defaultFontFamily, isBold = false, isItalic = false)
            }
            val pageNumText = "صفحہ ${pageIndex + 1} از $totalPages"
            val textWidth = footerPaint.measureText(pageNumText)
            val footerX = (PAGE_WIDTH_PT - textWidth) / 2f
            val footerY = PAGE_HEIGHT_PT - (bottomMarginPt / 2f)
            canvas.drawText(pageNumText, footerX, footerY, footerPaint)

            pdfDocument.finishPage(page)
        }

        val sanitizedTitle = document.title.replace(Regex("[^a-zA-Z0-9\\u0600-\\u06FF_-]"), "_").take(40)
        val fileName = "Draftings_${sanitizedTitle}_${System.currentTimeMillis()}.pdf"
        val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val outputFile = File(cacheDir, fileName)

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }
}
