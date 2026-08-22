package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TextBoxModel
import com.example.model.TextAlignment
import com.example.typography.FontHelper

@Composable
fun TextBoxEditDialog(
    textBox: TextBoxModel,
    onDismiss: () -> Unit,
    onSave: (TextBoxModel) -> Unit,
    onDelete: (String) -> Unit
) {
    var text by remember(textBox.id) { mutableStateOf(textBox.text) }
    var fontSize by remember(textBox.id) { mutableStateOf(textBox.fontSizeSp) }
    var isBold by remember(textBox.id) { mutableStateOf(textBox.isBold) }
    var isUnderline by remember(textBox.id) { mutableStateOf(textBox.isUnderline) }
    var showBorder by remember(textBox.id) { mutableStateOf(textBox.showBorder) }
    var alignment by remember(textBox.id) { mutableStateOf(textBox.alignment) }

    val nastaleeqFont = FontHelper.getComposeFontFamily(FontHelper.FONT_JAMEEL_NASTALEEQ)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "خانہ تحریر کی ترتیبات (Text Box Settings)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = nastaleeqFont,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("متن (Text)", fontFamily = nastaleeqFont) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = nastaleeqFont,
                            fontSize = 17.sp,
                            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
                        )
                    )

                    // Font Size & Style Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سائز: ${fontSize.toInt()}pt",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = nastaleeqFont)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilledTonalIconButton(
                                onClick = { if (fontSize > 10) fontSize -= 2f },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            FilledTonalIconButton(
                                onClick = { if (fontSize < 32) fontSize += 2f },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }

                    // Bold, Underline & Alignment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = isBold,
                                onClick = { isBold = !isBold },
                                label = { Text("B", fontWeight = FontWeight.Bold) }
                            )
                            FilterChip(
                                selected = isUnderline,
                                onClick = { isUnderline = !isUnderline },
                                label = { Text("U", style = MaterialTheme.typography.labelMedium.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) }
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { alignment = TextAlignment.RIGHT },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatAlignRight,
                                    contentDescription = "Right",
                                    tint = if (alignment == TextAlignment.RIGHT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { alignment = TextAlignment.CENTER },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatAlignCenter,
                                    contentDescription = "Center",
                                    tint = if (alignment == TextAlignment.CENTER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { alignment = TextAlignment.LEFT },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatAlignLeft,
                                    contentDescription = "Left",
                                    tint = if (alignment == TextAlignment.LEFT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Border toggle switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "بارڈر دکھائیں (Show Border)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = nastaleeqFont)
                        )
                        Switch(
                            checked = showBorder,
                            onCheckedChange = { showBorder = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        textBox.copy(
                            text = text,
                            fontSizeSp = fontSize,
                            isBold = isBold,
                            isUnderline = isUnderline,
                            showBorder = showBorder,
                            alignment = alignment
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("محفوظ کریں", fontFamily = nastaleeqFont)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(
                    onClick = {
                        onDelete(textBox.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف کریں", fontFamily = nastaleeqFont)
                }
                TextButton(onClick = onDismiss) {
                    Text("منسوخ", fontFamily = nastaleeqFont)
                }
            }
        }
    )
}
