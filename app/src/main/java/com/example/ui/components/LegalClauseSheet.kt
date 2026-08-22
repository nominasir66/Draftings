package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.typography.FontHelper

data class LegalClauseCategory(
    val titleUrdu: String,
    val clauses: List<String>
)

val LEGAL_CLAUSE_CATEGORIES = listOf(
    LegalClauseCategory(
        titleUrdu = "شناخت و کوائف (Identity & Address)",
        clauses = listOf(
            "منکہ مسمی ________________ ولد ________________ ساکن ________________",
            "شناختی کارڈ نمبر: ________________ موبائل نمبر: ________________",
            "مسمات ________________ زوجہ / دختر ________________ ساکن ________________",
            "بحیثیت قانونی وارث / مجاز نمائندہ مسمی ________________"
        )
    ),
    LegalClauseCategory(
        titleUrdu = "بیان و حلف (Oath & Affirmation)",
        clauses = listOf(
            "حلفاً حسب ذیل بیان کرتا ہوں کہ:",
            "یہ کہ بیان کنندہ کے تمام کوائف اور بیانات بالکل درست اور مبنی بر حقیقت ہیں۔",
            "یہ کہ میں مذکورہ بالا پتے کا مستقل رہائشی ہوں اور ہوش و حواس خمسہ میں یہ تحریر لکھ رہا ہوں۔",
            "یہ کہ مجھ سے کوئی امر مخفی یا پوشیدہ نہیں رکھا گیا ہے۔",
            "یہ کہ بیان کنندہ کے خلاف کبھی کوئی فوجداری مقدمہ درج نہیں ہوا۔"
        )
    ),
    LegalClauseCategory(
        titleUrdu = "عدالتی و قانونی القابات (Court & Official Titles)",
        clauses = listOf(
            "بعدالت جناب ڈسٹرکٹ اینڈ سیشن جج صاحب ________________",
            "بعدالت جناب سول جج صاحب درجہ اول ________________",
            "بخدمت جناب ایس ایچ او صاحب تھانہ ________________",
            "عنوان: درخواست برائے ________________",
            "جناب عالی! سائل حسب ذیل عرض گزار ہے:"
        )
    ),
    LegalClauseCategory(
        titleUrdu = "استدعا و احکامات (Prayers & Requests)",
        clauses = listOf(
            "لہٰذا استدعا ہے کہ سائل کی درخواست منظور فرمائی جائے۔",
            "استدعا ہے کہ انصاف کے تقاضے پورے کرتے ہوئے ریلیف فراہم کی جائے۔",
            "بصورت دیگر سائل کو ناقابل تلافی نقصان پہنچنے کا اندیشہ ہے۔"
        )
    ),
    LegalClauseCategory(
        titleUrdu = "تصدیق و گواہان (Verification & Signatures)",
        clauses = listOf(
            "تصدیق: تصدیق کی جاتی ہے کہ مندرجہ بالا بیان حلفی کے جملہ مندرجات میرے علم و یقین کے مطابق بالکل درست ہیں۔",
            "دستخط و نشان انگوٹھا بیان کنندہ: ________________",
            "گواہ اول: ________________ شناختی کارڈ: ________________",
            "گواہ دوم: ________________ شناختی کارڈ: ________________",
            "مورخہ: ____/____/۲۰۲ء بمقام: ________________",
            "المرقوم / بحضور اوتھ کمشنر / نوٹری پبلک"
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalClauseSheet(
    onDismiss: () -> Unit,
    onClauseSelected: (String) -> Unit
) {
    val nastaleeqFont = FontHelper.getComposeFontFamily(FontHelper.FONT_JAMEEL_NASTALEEQ)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قانونی جملے و کلازز (Legal Clauses)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = nastaleeqFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                text = "اپنی مطلوبہ قانونی عبارت پر کلک کریں تاکہ وہ فعال پیراگراف میں داخل ہو جائے:",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = nastaleeqFont,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider()

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(LEGAL_CLAUSE_CATEGORIES) { category ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = category.titleUrdu,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            category.clauses.forEach { clause ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onClauseSelected(clause)
                                            onDismiss()
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = null
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = clause,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontFamily = nastaleeqFont,
                                                fontSize = 17.sp,
                                                lineHeight = 24.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Insert",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
