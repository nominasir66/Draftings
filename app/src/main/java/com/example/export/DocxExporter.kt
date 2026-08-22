package com.example.export

import android.content.Context
import com.example.model.DocumentModel
import com.example.model.TextAlignment
import com.example.model.TextDirection
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object DocxExporter {

    fun exportToDocx(context: Context, document: DocumentModel): File {
        val sanitizedTitle = document.title.replace(Regex("[^a-zA-Z0-9\\u0600-\\u06FF_-]"), "_").take(40)
        val fileName = "Draftings_${sanitizedTitle}_${System.currentTimeMillis()}.docx"
        val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val outputFile = File(cacheDir, fileName)

        FileOutputStream(outputFile).use { fos ->
            ZipOutputStream(fos).use { zos ->
                // 1. [Content_Types].xml
                addZipEntry(zos, "[Content_Types].xml", getContentTypesXml())

                // 2. _rels/.rels
                addZipEntry(zos, "_rels/.rels", getRelsXml())

                // 3. word/_rels/document.xml.rels
                addZipEntry(zos, "word/_rels/document.xml.rels", getDocRelsXml())

                // 4. word/fontTable.xml
                addZipEntry(zos, "word/fontTable.xml", getFontTableXml())

                // 5. word/styles.xml
                addZipEntry(zos, "word/styles.xml", getStylesXml())

                // 6. word/document.xml
                addZipEntry(zos, "word/document.xml", getDocumentXml(document))
            }
        }

        return outputFile
    }

    private fun addZipEntry(zos: ZipOutputStream, entryName: String, content: String) {
        val entry = ZipEntry(entryName)
        zos.putNextEntry(entry)
        zos.write(content.toByteArray(StandardCharsets.UTF_8))
        zos.closeEntry()
    }

    private fun escapeXml(str: String): String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun getDocumentXml(doc: DocumentModel): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
        sb.append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n")
        sb.append("<w:body>\n")

        for ((pageIndex, page) in doc.pages.withIndex()) {
            if (pageIndex > 0) {
                // Page break before next page
                sb.append("<w:p><w:r><w:br w:type=\"page\"/></w:r></w:p>\n")
            }

            for (para in page.paragraphs) {
                sb.append("<w:p>\n")
                sb.append("  <w:pPr>\n")

                if (para.direction == TextDirection.RTL) {
                    sb.append("    <w:bidi/>\n")
                }

                val jcVal = when (para.alignment) {
                    TextAlignment.CENTER -> "center"
                    TextAlignment.LEFT -> "left"
                    TextAlignment.JUSTIFY -> "both"
                    TextAlignment.RIGHT -> "right"
                }
                sb.append("    <w:jc w:val=\"$jcVal\"/>\n")

                // Line spacing in twips (240 twips = 1.0x)
                val lineVal = (para.lineSpacing * 240).toInt()
                sb.append("    <w:spacing w:line=\"$lineVal\" w:lineRule=\"auto\" w:after=\"120\"/>\n")
                sb.append("  </w:pPr>\n")

                // Run
                sb.append("  <w:r>\n")
                sb.append("    <w:rPr>\n")
                sb.append("      <w:rFonts w:ascii=\"Jameel Noori Nastaleeq\" w:hAnsi=\"Jameel Noori Nastaleeq\" w:cs=\"Jameel Noori Nastaleeq\"/>\n")
                if (para.direction == TextDirection.RTL) {
                    sb.append("      <w:rtl/>\n")
                }
                if (para.isBold) {
                    sb.append("      <w:b/>\n")
                    sb.append("      <w:bCs/>\n")
                }
                if (para.isItalic) {
                    sb.append("      <w:i/>\n")
                    sb.append("      <w:iCs/>\n")
                }
                if (para.isUnderline) {
                    sb.append("      <w:u w:val=\"single\"/>\n")
                }
                // Font size in half-points (18sp -> 36 half points)
                val szVal = (para.fontSizeSp * 2).toInt()
                sb.append("      <w:sz w:val=\"$szVal\"/>\n")
                sb.append("      <w:szCs w:val=\"$szVal\"/>\n")
                sb.append("    </w:rPr>\n")
                sb.append("    <w:t xml:space=\"preserve\">${escapeXml(para.text)}</w:t>\n")
                sb.append("  </w:r>\n")

                sb.append("</w:p>\n")
            }

            // Also append floating text box paragraphs if present
            for (box in page.textBoxes) {
                sb.append("<w:p>\n")
                sb.append("  <w:pPr><w:bidi/><w:jc w:val=\"right\"/><w:pBdr><w:bottom w:val=\"single\" w:sz=\"4\" w:space=\"1\" w:color=\"94A3B8\"/></w:pBdr></w:pPr>\n")
                sb.append("  <w:r>\n")
                sb.append("    <w:rPr><w:rFonts w:cs=\"Jameel Noori Nastaleeq\"/><w:rtl/><w:sz w:val=\"28\"/></w:rPr>\n")
                sb.append("    <w:t xml:space=\"preserve\">[خانہ تحریر: ${escapeXml(box.text)}]</w:t>\n")
                sb.append("  </w:r>\n")
                sb.append("</w:p>\n")
            }
        }

        // Section properties (A4 size: 11906 x 16838 twips; margins: ~1134 twips for 20mm)
        val topTwips = (doc.margins.topMm * 56.7f).toInt()
        val rightTwips = (doc.margins.rightMm * 56.7f).toInt()
        val bottomTwips = (doc.margins.bottomMm * 56.7f).toInt()
        val leftTwips = (doc.margins.leftMm * 56.7f).toInt()

        sb.append("<w:sectPr>\n")
        sb.append("  <w:pgSz w:w=\"11906\" w:h=\"16838\"/>\n")
        sb.append("  <w:pgMar w:top=\"$topTwips\" w:right=\"$rightTwips\" w:bottom=\"$bottomTwips\" w:left=\"$leftTwips\" w:header=\"720\" w:footer=\"720\" w:gutter=\"0\"/>\n")
        sb.append("  <w:bidi/>\n")
        sb.append("</w:sectPr>\n")

        sb.append("</w:body>\n")
        sb.append("</w:document>")
        return sb.toString()
    }

    private fun getContentTypesXml(): String =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" +
        "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n" +
        "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" +
        "  <Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>\n" +
        "  <Override PartName=\"/word/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml\"/>\n" +
        "  <Override PartName=\"/word/fontTable.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.fontTable+xml\"/>\n" +
        "</Types>"

    private fun getRelsXml(): String =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
        "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>\n" +
        "</Relationships>"

    private fun getDocRelsXml(): String =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
        "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n" +
        "  <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/fontTable\" Target=\"fontTable.xml\"/>\n" +
        "</Relationships>"

    private fun getFontTableXml(): String =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<w:fonts xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n" +
        "  <w:font w:name=\"Jameel Noori Nastaleeq\">\n" +
        "    <w:charset w:val=\"B2\"/>\n" +
        "    <w:family w:val=\"auto\"/>\n" +
        "    <w:pitch w:val=\"variable\"/>\n" +
        "  </w:font>\n" +
        "</w:fonts>"

    private fun getStylesXml(): String =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "<w:styles xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n" +
        "  <w:docDefaults>\n" +
        "    <w:rPrDefault>\n" +
        "      <w:rPr>\n" +
        "        <w:rFonts w:ascii=\"Jameel Noori Nastaleeq\" w:hAnsi=\"Jameel Noori Nastaleeq\" w:cs=\"Jameel Noori Nastaleeq\"/>\n" +
        "        <w:sz w:val=\"36\"/>\n" +
        "        <w:szCs w:val=\"36\"/>\n" +
        "        <w:rtl/>\n" +
        "      </w:rPr>\n" +
        "    </w:rPrDefault>\n" +
        "  </w:docDefaults>\n" +
        "</w:styles>"
}
