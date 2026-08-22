package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ParagraphModel
import com.example.model.TextAlignment
import com.example.model.TextDirection
import com.example.typography.FontHelper

@Composable
fun FormattingToolbar(
    activeParagraph: ParagraphModel?,
    currentFontFamily: String,
    canUndo: Boolean,
    canRedo: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleUnderline: () -> Unit,
    onToggleItalic: () -> Unit,
    onFontSizeChanged: (Float) -> Unit,
    onAlignmentChanged: (TextAlignment) -> Unit,
    onDirectionChanged: (TextDirection) -> Unit,
    onLineSpacingChanged: (Float) -> Unit,
    onFontFamilyChanged: (String) -> Unit,
    onHeadingChanged: (Int) -> Unit,
    onInsertParagraph: () -> Unit,
    onDeleteParagraph: () -> Unit,
    onAddTextBox: () -> Unit,
    onOpenLegalClauses: () -> Unit,
    onAddNewPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showFontMenu by remember { mutableStateOf(false) }
    var showLineSpacingMenu by remember { mutableStateOf(false) }
    var showHeadingMenu by remember { mutableStateOf(false) }

    val nastaleeqFont = FontHelper.getComposeFontFamily(FontHelper.FONT_JAMEEL_NASTALEEQ)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Undo / Redo group
            IconButton(
                onClick = onUndo,
                enabled = canUndo,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = if (canUndo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
            IconButton(
                onClick = onRedo,
                enabled = canRedo,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    tint = if (canRedo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }

            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 2.dp))

            // Font Family Selector
            Box {
                OutlinedButton(
                    onClick = { showFontMenu = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = when (currentFontFamily) {
                            FontHelper.FONT_JAMEEL_NASTALEEQ -> "نستعلیق"
                            FontHelper.FONT_SYSTEM_SERIF -> "نسخ"
                            FontHelper.FONT_MONOSPACE -> "یکساں"
                            else -> "سادہ"
                        },
                        fontSize = 13.sp,
                        fontFamily = nastaleeqFont
                    )
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = showFontMenu, onDismissRequest = { showFontMenu = false }) {
                    FontHelper.AVAILABLE_FONTS.forEach { (fontKey, fontLabel) ->
                        DropdownMenuItem(
                            text = { Text(fontLabel, fontFamily = nastaleeqFont, fontSize = 15.sp) },
                            onClick = {
                                onFontFamilyChanged(fontKey)
                                showFontMenu = false
                            }
                        )
                    }
                }
            }

            // Font Size Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(34.dp)
                    .padding(horizontal = 2.dp)
            ) {
                FilledTonalIconButton(
                    onClick = {
                        val currentSize = activeParagraph?.fontSizeSp ?: 18f
                        if (currentSize > 10f) onFontSizeChanged(currentSize - 2f)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Text(
                    text = "${activeParagraph?.fontSizeSp?.toInt() ?: 18}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                FilledTonalIconButton(
                    onClick = {
                        val currentSize = activeParagraph?.fontSizeSp ?: 18f
                        if (currentSize < 36f) onFontSizeChanged(currentSize + 2f)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 2.dp))

            // Bold Toggle
            val isBold = activeParagraph?.isBold == true
            FilledIconToggleButton(
                checked = isBold,
                onCheckedChange = { onToggleBold() },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconToggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("B", fontWeight = FontWeight.Black, fontSize = 15.sp)
            }

            // Underline Toggle
            val isUnderline = activeParagraph?.isUnderline == true
            FilledIconToggleButton(
                checked = isUnderline,
                onCheckedChange = { onToggleUnderline() },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconToggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("U", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline))
            }

            // Italic Toggle
            val isItalic = activeParagraph?.isItalic == true
            FilledIconToggleButton(
                checked = isItalic,
                onCheckedChange = { onToggleItalic() },
                modifier = Modifier.size(34.dp),
                colors = IconButtonDefaults.filledIconToggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("I", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 2.dp))

            // Alignment Group
            val alignment = activeParagraph?.alignment ?: TextAlignment.RIGHT
            IconButton(
                onClick = { onAlignmentChanged(TextAlignment.RIGHT) },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatAlignRight,
                    contentDescription = "Right",
                    tint = if (alignment == TextAlignment.RIGHT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { onAlignmentChanged(TextAlignment.CENTER) },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatAlignCenter,
                    contentDescription = "Center",
                    tint = if (alignment == TextAlignment.CENTER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { onAlignmentChanged(TextAlignment.LEFT) },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatAlignLeft,
                    contentDescription = "Left",
                    tint = if (alignment == TextAlignment.LEFT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = { onAlignmentChanged(TextAlignment.JUSTIFY) },
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FormatAlignJustify,
                    contentDescription = "Justify",
                    tint = if (alignment == TextAlignment.JUSTIFY) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Direction Toggle (RTL / LTR)
            val isRtl = activeParagraph?.direction != TextDirection.LTR
            FilledTonalButton(
                onClick = { onDirectionChanged(if (isRtl) TextDirection.LTR else TextDirection.RTL) },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(if (isRtl) "RTL (دائیں)" else "LTR (بائیں)", fontSize = 12.sp, fontFamily = nastaleeqFont)
            }

            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 2.dp))

            // Line Spacing
            Box {
                IconButton(
                    onClick = { showLineSpacingMenu = true },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.FormatLineSpacing, contentDescription = "Line Spacing")
                }
                DropdownMenu(expanded = showLineSpacingMenu, onDismissRequest = { showLineSpacingMenu = false }) {
                    listOf(1.0f to "1.0x (عام)", 1.2f to "1.2x", 1.4f to "1.4x (مناسب اردو)", 1.6f to "1.6x (کشادہ)", 2.0f to "2.0x (دوہرا)").forEach { (spacing, label) ->
                        DropdownMenuItem(
                            text = { Text(label, fontFamily = nastaleeqFont) },
                            onClick = {
                                onLineSpacingChanged(spacing)
                                showLineSpacingMenu = false
                            }
                        )
                    }
                }
            }

            // Headings Menu
            Box {
                IconButton(
                    onClick = { showHeadingMenu = true },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(imageVector = Icons.Default.Title, contentDescription = "Heading")
                }
                DropdownMenu(expanded = showHeadingMenu, onDismissRequest = { showHeadingMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("مرکزی عنوان (Title - 24pt)", fontFamily = nastaleeqFont) },
                        onClick = { onHeadingChanged(1); showHeadingMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("ذیلی عنوان (Subheading - 20pt)", fontFamily = nastaleeqFont) },
                        onClick = { onHeadingChanged(2); showHeadingMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("عام متن (Normal Body - 18pt)", fontFamily = nastaleeqFont) },
                        onClick = { onHeadingChanged(0); showHeadingMenu = false }
                    )
                }
            }

            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 2.dp))

            // Insert Legal Clause Action
            Button(
                onClick = onOpenLegalClauses,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(imageVector = Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("قانونی کلاز", fontFamily = nastaleeqFont, fontSize = 14.sp)
            }

            // Insert Text Box Action
            FilledTonalButton(
                onClick = onAddTextBox,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(imageVector = Icons.Default.CropPortrait, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("خانہ تحریر", fontFamily = nastaleeqFont, fontSize = 14.sp)
            }

            // Paragraph insert & delete
            IconButton(onClick = onInsertParagraph, modifier = Modifier.size(34.dp)) {
                Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "New Paragraph", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDeleteParagraph, modifier = Modifier.size(34.dp)) {
                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Paragraph", tint = MaterialTheme.colorScheme.error)
            }

            // Add Page Action
            FilledTonalButton(
                onClick = onAddNewPage,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(imageVector = Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ صفحہ", fontFamily = nastaleeqFont, fontSize = 14.sp)
            }
        }
    }
}
