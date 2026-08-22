package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.typography.FontHelper
import com.example.ui.theme.MarginGuideColor
import com.example.ui.theme.PaperSheetLight

@Composable
fun A4PageView(
    document: DocumentModel,
    pageIndex: Int,
    activeParagraphIndex: Int,
    selectedTextBox: TextBoxModel?,
    onParagraphFocused: (paraIndex: Int) -> Unit,
    onParagraphTextChanged: (paraIndex: Int, newText: String) -> Unit,
    onTextBoxSelected: (TextBoxModel?) -> Unit,
    onTextBoxUpdated: (TextBoxModel) -> Unit,
    onTextBoxEditRequest: (TextBoxModel) -> Unit,
    onTextBoxDeleted: (String) -> Unit,
    onDeletePage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val page = document.pages.getOrNull(pageIndex) ?: return
    val totalPages = document.pages.size
    val density = LocalDensity.current

    var pageSizePx by remember { mutableStateOf(IntSize.Zero) }

    val nastaleeqFont = FontHelper.getComposeFontFamily(document.defaultFontFamily)

    // Margins in dp (approx conversion from mm: 20mm = ~56dp on mobile standard density)
    val topMarginDp = (document.margins.topMm * 2.83f).dp
    val bottomMarginDp = (document.margins.bottomMm * 2.83f).dp
    val leftMarginDp = (document.margins.leftMm * 2.83f).dp
    val rightMarginDp = (document.margins.rightMm * 2.83f).dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Page Info Badge above the sheet
        Row(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "صفحہ ${pageIndex + 1} از $totalPages",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = nastaleeqFont,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }

            if (totalPages > 1) {
                TextButton(
                    onClick = onDeletePage,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("صفحہ حذف کریں", fontFamily = nastaleeqFont, fontSize = 13.sp)
                }
            }
        }

        // A4 Paper Sheet (White clean canvas with subtle border and realistic elevation shadow)
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .defaultMinSize(minHeight = 540.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(4.dp))
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .border(width = 1.dp, color = Color(0xFFE2E8F0), shape = RoundedCornerShape(4.dp))
                .onSizeChanged { pageSizePx = it }
                .drawBehind {
                    if (document.showMarginGuidelines) {
                        val stroke = Stroke(
                            width = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                        val leftPx = leftMarginDp.toPx()
                        val topPx = topMarginDp.toPx()
                        val rightPx = size.width - rightMarginDp.toPx()
                        val bottomPx = size.height - bottomMarginDp.toPx()

                        // Draw margin guidelines rectangle
                        drawRect(
                            color = Color(0xFFCBD5E1),
                            topLeft = Offset(leftPx, topPx),
                            size = androidx.compose.ui.geometry.Size(
                                width = (rightPx - leftPx).coerceAtLeast(0f),
                                height = (bottomPx - topPx).coerceAtLeast(0f)
                            ),
                            style = stroke
                        )
                    }
                }
        ) {
            // Document Header if not first page
            if (pageIndex > 0) {
                Text(
                    text = document.title,
                    style = TextStyle(
                        fontFamily = nastaleeqFont,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = leftMarginDp, end = rightMarginDp, top = 8.dp)
                )
            }

            // Paragraphs Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = leftMarginDp,
                        end = rightMarginDp,
                        top = topMarginDp,
                        bottom = bottomMarginDp + 24.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                page.paragraphs.forEachIndexed { pIdx, paragraph ->
                    val isActive = pIdx == activeParagraphIndex
                    val paraFont = FontHelper.getComposeFontFamily(document.defaultFontFamily)

                    val textAlign = when (paragraph.alignment) {
                        TextAlignment.RIGHT -> TextAlign.Right
                        TextAlignment.CENTER -> TextAlign.Center
                        TextAlignment.LEFT -> TextAlign.Left
                        TextAlignment.JUSTIFY -> TextAlign.Justify
                    }

                    val layoutDir = if (paragraph.direction == TextDirection.RTL) LayoutDirection.Rtl else LayoutDirection.Ltr

                    CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f) else Color.Transparent
                                )
                                .border(
                                    width = if (isActive) 1.dp else 0.dp,
                                    color = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .clickable { onParagraphFocused(pIdx) }
                        ) {
                            BasicTextField(
                                value = paragraph.text,
                                onValueChange = { onParagraphTextChanged(pIdx, it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 24.dp),
                                textStyle = TextStyle(
                                    fontFamily = paraFont,
                                    fontSize = paragraph.fontSizeSp.sp,
                                    fontWeight = if (paragraph.isBold) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (paragraph.isItalic) FontStyle.Italic else FontStyle.Normal,
                                    textDecoration = if (paragraph.isUnderline) TextDecoration.Underline else TextDecoration.None,
                                    color = Color(0xFF0F172A),
                                    textAlign = textAlign,
                                    lineHeight = (paragraph.fontSizeSp * paragraph.lineSpacing).sp
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                decorationBox = { innerTextField ->
                                    if (paragraph.text.isEmpty() && isActive) {
                                        Text(
                                            text = "یہاں تحریر لکھیں… (Type here…)",
                                            style = TextStyle(
                                                fontFamily = paraFont,
                                                fontSize = paragraph.fontSizeSp.sp,
                                                color = Color(0xFF94A3B8),
                                                textAlign = textAlign
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }
                }
            }

            // Floating Text Boxes on this page
            page.textBoxes.forEach { box ->
                val isSelected = selectedTextBox?.id == box.id
                val boxFontKey = if (box.fontFamily.isNotBlank()) box.fontFamily else document.defaultFontFamily
                val boxFont = FontHelper.getComposeFontFamily(boxFontKey)

                val boxWidthDp = (density.run { pageSizePx.width.toDp() } * box.widthPercent).coerceAtLeast(100.dp)
                val boxHeightDp = (density.run { pageSizePx.height.toDp() } * box.heightPercent).coerceAtLeast(40.dp)
                val offsetX = (density.run { pageSizePx.width.toDp() } * box.xPercent)
                val offsetY = (density.run { pageSizePx.height.toDp() } * box.yPercent)

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .size(width = boxWidthDp, height = boxHeightDp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (box.backgroundColorHex != "#00000000") {
                                try { Color(android.graphics.Color.parseColor(box.backgroundColorHex)) } catch (e: Exception) { Color.Transparent }
                            } else Color.White.copy(alpha = 0.9f)
                        )
                        .border(
                            width = if (isSelected) 2.dp else if (box.showBorder) 1.dp else 0.5.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else if (box.showBorder) Color(0xFF94A3B8) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .pointerInput(box.id) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                if (pageSizePx.width > 0 && pageSizePx.height > 0) {
                                    val deltaXPct = dragAmount.x / pageSizePx.width
                                    val deltaYPct = dragAmount.y / pageSizePx.height
                                    val newX = (box.xPercent + deltaXPct).coerceIn(0.02f, 0.90f - box.widthPercent)
                                    val newY = (box.yPercent + deltaYPct).coerceIn(0.02f, 0.95f - box.heightPercent)
                                    onTextBoxUpdated(box.copy(xPercent = newX, yPercent = newY))
                                }
                            }
                        }
                        .clickable {
                            onTextBoxSelected(box)
                        }
                        .padding(6.dp)
                ) {
                    val boxTextAlign = when (box.alignment) {
                        TextAlignment.RIGHT -> TextAlign.Right
                        TextAlignment.CENTER -> TextAlign.Center
                        TextAlignment.LEFT -> TextAlign.Left
                        TextAlignment.JUSTIFY -> TextAlign.Justify
                    }

                    Text(
                        text = box.text.ifBlank { "خانہ تحریر" },
                        style = TextStyle(
                            fontFamily = boxFont,
                            fontSize = box.fontSizeSp.sp,
                            fontWeight = if (box.isBold) FontWeight.Bold else FontWeight.Normal,
                            textDecoration = if (box.isUnderline) TextDecoration.Underline else TextDecoration.None,
                            color = Color(0xFF0F172A),
                            textAlign = boxTextAlign,
                            lineHeight = (box.fontSizeSp * 1.2f).sp
                        ),
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay edit button when selected
                    if (isSelected) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                .padding(2.dp)
                        ) {
                            IconButton(
                                onClick = { onTextBoxEditRequest(box) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Box",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            IconButton(
                                onClick = { onTextBoxDeleted(box.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Box",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Page Number Footer
            Text(
                text = "صفحہ ${pageIndex + 1} از $totalPages",
                style = TextStyle(
                    fontFamily = nastaleeqFont,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}
