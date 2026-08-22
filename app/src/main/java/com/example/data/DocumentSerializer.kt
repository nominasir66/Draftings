package com.example.data

import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

object DocumentSerializer {

    fun toJson(document: DocumentModel): String {
        val root = JSONObject()
        root.put("id", document.id)
        root.put("title", document.title)
        root.put("category", document.category.name)
        root.put("defaultFontFamily", document.defaultFontFamily)
        root.put("defaultFontSizeSp", document.defaultFontSizeSp.toDouble())
        root.put("defaultAlignment", document.defaultAlignment.name)
        root.put("defaultDirection", document.defaultDirection.name)
        root.put("defaultLineSpacing", document.defaultLineSpacing.toDouble())
        root.put("showMarginGuidelines", document.showMarginGuidelines)
        root.put("createdAt", document.createdAt)
        root.put("updatedAt", document.updatedAt)

        // Margins
        val marginsObj = JSONObject().apply {
            put("topMm", document.margins.topMm.toDouble())
            put("bottomMm", document.margins.bottomMm.toDouble())
            put("leftMm", document.margins.leftMm.toDouble())
            put("rightMm", document.margins.rightMm.toDouble())
        }
        root.put("margins", marginsObj)

        // Page Size
        val pageSizeObj = JSONObject().apply {
            put("widthMm", document.pageSize.widthMm.toDouble())
            put("heightMm", document.pageSize.heightMm.toDouble())
        }
        root.put("pageSize", pageSizeObj)

        // Pages
        val pagesArray = JSONArray()
        for (page in document.pages) {
            val pageObj = JSONObject()
            pageObj.put("pageNumber", page.pageNumber)

            // Paragraphs
            val parasArray = JSONArray()
            for (p in page.paragraphs) {
                val pObj = JSONObject().apply {
                    put("id", p.id)
                    put("text", p.text)
                    put("isBold", p.isBold)
                    put("isItalic", p.isItalic)
                    put("isUnderline", p.isUnderline)
                    put("fontSizeSp", p.fontSizeSp.toDouble())
                    put("alignment", p.alignment.name)
                    put("direction", p.direction.name)
                    put("lineSpacing", p.lineSpacing.toDouble())
                    put("isHeading", p.isHeading)
                    put("headingLevel", p.headingLevel)
                    put("isPageBreak", p.isPageBreak)
                }
                parasArray.put(pObj)
            }
            pageObj.put("paragraphs", parasArray)

            // TextBoxes
            val boxesArray = JSONArray()
            for (box in page.textBoxes) {
                val bObj = JSONObject().apply {
                    put("id", box.id)
                    put("pageIndex", box.pageIndex)
                    put("xPercent", box.xPercent.toDouble())
                    put("yPercent", box.yPercent.toDouble())
                    put("widthPercent", box.widthPercent.toDouble())
                    put("heightPercent", box.heightPercent.toDouble())
                    put("text", box.text)
                    put("fontSizeSp", box.fontSizeSp.toDouble())
                    put("isBold", box.isBold)
                    put("isUnderline", box.isUnderline)
                    put("alignment", box.alignment.name)
                    put("direction", box.direction.name)
                    put("showBorder", box.showBorder)
                    put("backgroundColorHex", box.backgroundColorHex)
                    put("borderColorHex", box.borderColorHex)
                }
                boxesArray.put(bObj)
            }
            pageObj.put("textBoxes", boxesArray)

            pagesArray.put(pageObj)
        }
        root.put("pages", pagesArray)

        return root.toString()
    }

    fun fromJson(jsonStr: String): DocumentModel {
        if (jsonStr.isBlank()) {
            return DocumentModel()
        }
        return try {
            val root = JSONObject(jsonStr)
            val id = root.optLong("id", 0L)
            val title = root.optString("title", "مسودہ")
            val categoryStr = root.optString("category", DocumentCategory.AFFIDAVIT.name)
            val category = try { DocumentCategory.valueOf(categoryStr) } catch (e: Exception) { DocumentCategory.CUSTOM }
            val defaultFontFamily = root.optString("defaultFontFamily", "Jameel Noori Nastaleeq")
            val defaultFontSizeSp = root.optDouble("defaultFontSizeSp", 18.0).toFloat()
            val defaultAlignmentStr = root.optString("defaultAlignment", TextAlignment.RIGHT.name)
            val defaultAlignment = try { TextAlignment.valueOf(defaultAlignmentStr) } catch (e: Exception) { TextAlignment.RIGHT }
            val defaultDirectionStr = root.optString("defaultDirection", TextDirection.RTL.name)
            val defaultDirection = try { TextDirection.valueOf(defaultDirectionStr) } catch (e: Exception) { TextDirection.RTL }
            val defaultLineSpacing = root.optDouble("defaultLineSpacing", 1.4).toFloat()
            val showMarginGuidelines = root.optBoolean("showMarginGuidelines", true)
            val createdAt = root.optLong("createdAt", System.currentTimeMillis())
            val updatedAt = root.optLong("updatedAt", System.currentTimeMillis())

            // Margins
            val marginsObj = root.optJSONObject("margins")
            val margins = if (marginsObj != null) {
                MarginsModel(
                    topMm = marginsObj.optDouble("topMm", 20.0).toFloat(),
                    bottomMm = marginsObj.optDouble("bottomMm", 20.0).toFloat(),
                    leftMm = marginsObj.optDouble("leftMm", 20.0).toFloat(),
                    rightMm = marginsObj.optDouble("rightMm", 20.0).toFloat()
                )
            } else MarginsModel()

            // Page Size
            val pageSizeObj = root.optJSONObject("pageSize")
            val pageSize = if (pageSizeObj != null) {
                PageSizeModel(
                    widthMm = pageSizeObj.optDouble("widthMm", 210.0).toFloat(),
                    heightMm = pageSizeObj.optDouble("heightMm", 297.0).toFloat()
                )
            } else PageSizeModel()

            // Pages
            val pagesList = mutableListOf<PageModel>()
            val pagesArray = root.optJSONArray("pages")
            if (pagesArray != null && pagesArray.length() > 0) {
                for (i in 0 until pagesArray.length()) {
                    val pageObj = pagesArray.getJSONObject(i)
                    val pageNum = pageObj.optInt("pageNumber", i + 1)

                    val parasList = mutableListOf<ParagraphModel>()
                    val parasArray = pageObj.optJSONArray("paragraphs")
                    if (parasArray != null) {
                        for (j in 0 until parasArray.length()) {
                            val pObj = parasArray.getJSONObject(j)
                            val alignStr = pObj.optString("alignment", TextAlignment.RIGHT.name)
                            val dirStr = pObj.optString("direction", TextDirection.RTL.name)
                            parasList.add(
                                ParagraphModel(
                                    id = pObj.optString("id", java.util.UUID.randomUUID().toString()),
                                    text = pObj.optString("text", ""),
                                    isBold = pObj.optBoolean("isBold", false),
                                    isItalic = pObj.optBoolean("isItalic", false),
                                    isUnderline = pObj.optBoolean("isUnderline", false),
                                    fontSizeSp = pObj.optDouble("fontSizeSp", 18.0).toFloat(),
                                    alignment = try { TextAlignment.valueOf(alignStr) } catch (e: Exception) { TextAlignment.RIGHT },
                                    direction = try { TextDirection.valueOf(dirStr) } catch (e: Exception) { TextDirection.RTL },
                                    lineSpacing = pObj.optDouble("lineSpacing", 1.4).toFloat(),
                                    isHeading = pObj.optBoolean("isHeading", false),
                                    headingLevel = pObj.optInt("headingLevel", 0),
                                    isPageBreak = pObj.optBoolean("isPageBreak", false)
                                )
                            )
                        }
                    }

                    val boxesList = mutableListOf<TextBoxModel>()
                    val boxesArray = pageObj.optJSONArray("textBoxes")
                    if (boxesArray != null) {
                        for (k in 0 until boxesArray.length()) {
                            val bObj = boxesArray.getJSONObject(k)
                            val alignStr = bObj.optString("alignment", TextAlignment.RIGHT.name)
                            val dirStr = bObj.optString("direction", TextDirection.RTL.name)
                            boxesList.add(
                                TextBoxModel(
                                    id = bObj.optString("id", java.util.UUID.randomUUID().toString()),
                                    pageIndex = bObj.optInt("pageIndex", i),
                                    xPercent = bObj.optDouble("xPercent", 0.08).toFloat(),
                                    yPercent = bObj.optDouble("yPercent", 0.08).toFloat(),
                                    widthPercent = bObj.optDouble("widthPercent", 0.4).toFloat(),
                                    heightPercent = bObj.optDouble("heightPercent", 0.12).toFloat(),
                                    text = bObj.optString("text", ""),
                                    fontSizeSp = bObj.optDouble("fontSizeSp", 16.0).toFloat(),
                                    isBold = bObj.optBoolean("isBold", false),
                                    isUnderline = bObj.optBoolean("isUnderline", false),
                                    alignment = try { TextAlignment.valueOf(alignStr) } catch (e: Exception) { TextAlignment.RIGHT },
                                    direction = try { TextDirection.valueOf(dirStr) } catch (e: Exception) { TextDirection.RTL },
                                    showBorder = bObj.optBoolean("showBorder", true),
                                    backgroundColorHex = bObj.optString("backgroundColorHex", "#00000000"),
                                    borderColorHex = bObj.optString("borderColorHex", "#94A3B8")
                                )
                            )
                        }
                    }

                    pagesList.add(
                        PageModel(
                            pageNumber = pageNum,
                            paragraphs = if (parasList.isEmpty()) listOf(ParagraphModel()) else parasList,
                            textBoxes = boxesList
                        )
                    )
                }
            }

            DocumentModel(
                id = id,
                title = title,
                category = category,
                pages = if (pagesList.isEmpty()) listOf(PageModel()) else pagesList,
                margins = margins,
                pageSize = pageSize,
                defaultFontFamily = defaultFontFamily,
                defaultFontSizeSp = defaultFontSizeSp,
                defaultAlignment = defaultAlignment,
                defaultDirection = defaultDirection,
                defaultLineSpacing = defaultLineSpacing,
                showMarginGuidelines = showMarginGuidelines,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        } catch (e: Exception) {
            DocumentModel(title = "مسودہ (بازیاب شدہ)")
        }
    }
}
