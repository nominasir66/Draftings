package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.export.PrintAndShareHelper
import com.example.model.DocumentModel
import com.example.typography.FontHelper
import java.io.File

@Composable
fun PdfPreviewDialog(
    document: DocumentModel,
    pdfFile: File?,
    onDismiss: () -> Unit,
    onExportDocx: () -> Unit
) {
    val context = LocalContext.current
    val nastaleeqFont = FontHelper.getComposeFontFamily(FontHelper.FONT_JAMEEL_NASTALEEQ)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Icon
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = "پی ڈی ایف مسودہ تیار ہے!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = nastaleeqFont,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "عنوان: ${document.title}\nصفحات: ${document.pages.size} | سائز: A4 (210 x 297 mm)\nفونٹ: جمیل نوری نستعلیق",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = nastaleeqFont,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (pdfFile != null) {
                    val fileSizeKb = pdfFile.length() / 1024
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "فائل سائز: ${fileSizeKb} KB",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Print Action
                    Button(
                        onClick = {
                            if (pdfFile != null) {
                                PrintAndShareHelper.printPdf(context, pdfFile, document.title)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("پرنٹ کریں (Print A4 PDF)", fontFamily = nastaleeqFont, fontSize = 16.sp)
                    }

                    // Share PDF Action
                    FilledTonalButton(
                        onClick = {
                            if (pdfFile != null) {
                                PrintAndShareHelper.sharePdf(context, pdfFile, document.title)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("پی ڈی ایف شیئر کریں (Share PDF)", fontFamily = nastaleeqFont, fontSize = 16.sp)
                    }

                    // Open in External PDF Viewer
                    OutlinedButton(
                        onClick = {
                            if (pdfFile != null) {
                                PrintAndShareHelper.openFile(context, pdfFile, "application/pdf")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("پی ڈی ایف ویور میں کھولیں", fontFamily = nastaleeqFont, fontSize = 16.sp)
                    }

                    // Export DOCX Action
                    OutlinedButton(
                        onClick = {
                            onExportDocx()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مائیکروسافٹ ورڈ (DOCX) ایکسپورٹ", fontFamily = nastaleeqFont, fontSize = 16.sp)
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("بند کریں (Close)", fontFamily = nastaleeqFont, fontSize = 15.sp)
                }
            }
        }
    }
}
