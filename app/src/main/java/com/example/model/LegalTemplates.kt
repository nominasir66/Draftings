package com.example.model

data class LegalTemplate(
    val id: String,
    val titleUrdu: String,
    val titleEnglish: String,
    val category: DocumentCategory,
    val descriptionUrdu: String,
    val documentModel: DocumentModel
)

object LegalTemplates {

    val allTemplates: List<LegalTemplate> by lazy {
        listOf(
            createAffidavitTemplate(),
            createPowerOfAttorneyTemplate(),
            createTenancyAgreementTemplate(),
            createLegalNoticeTemplate(),
            createBailApplicationTemplate(),
            createPoliceApplicationTemplate(),
            createPromissoryNoteTemplate()
        )
    }

    private fun createAffidavitTemplate(): LegalTemplate {
        val paragraphs = listOf(
            ParagraphModel(
                text = "بیان حلفی",
                isBold = true,
                fontSizeSp = 24f,
                alignment = TextAlignment.CENTER,
                isHeading = true,
                headingLevel = 1
            ),
            ParagraphModel(
                text = "منکہ مسمی ________________ ولد ________________ ساکن ________________ شناختی کارڈ نمبر ________________ کا رہائشی ہوں اور حلفاً حسب ذیل بیان کرتا ہوں:",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.6f
            ),
            ParagraphModel(
                text = "۱۔ یہ کہ میں مذکورہ بالا پتے کا مستقل رہائشی ہوں اور ہوش و حواس خمسہ میں بلا جبر و اکراہ یہ بیان حلفی تحریر کر رہا ہوں۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۲۔ یہ کہ بیان کنندہ کے تمام کوائف اور بیانات بالکل درست اور مبنی بر حقیقت ہیں۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۳۔ یہ کہ مجھ سے کوئی امر مخفی یا پوشیدہ نہیں رکھا گیا ہے۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "تصدیق:",
                isBold = true,
                fontSizeSp = 20f,
                alignment = TextAlignment.RIGHT,
                isHeading = true,
                headingLevel = 2
            ),
            ParagraphModel(
                text = "تصدیق کی جاتی ہے کہ مندرجہ بالا بیان حلفی کے جملہ مندرجات میرے علم و یقین کے مطابق بالکل درست اور سچ ہیں اور اس میں کوئی امر خلاف واقعہ نہیں ہے۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "المحلف / بیان کنندہ: ________________\nدستخط و انگوٹھا: ________________\nتاریخ: ____/____/۲۰۲ء",
                fontSizeSp = 18f,
                alignment = TextAlignment.LEFT,
                lineSpacing = 1.6f
            )
        )

        val stampBox = TextBoxModel(
            pageIndex = 0,
            xPercent = 0.05f,
            yPercent = 0.04f,
            widthPercent = 0.35f,
            heightPercent = 0.09f,
            text = "خانہ تصدیق اوتھ کمشنر\nنوٹری پبلک / مہر",
            fontSizeSp = 14f,
            alignment = TextAlignment.CENTER,
            showBorder = true
        )

        val doc = DocumentModel(
            title = "بیان حلفی (Affidavit)",
            category = DocumentCategory.AFFIDAVIT,
            pages = listOf(PageModel(pageNumber = 1, paragraphs = paragraphs, textBoxes = listOf(stampBox)))
        )

        return LegalTemplate(
            id = "template_affidavit",
            titleUrdu = "بیان حلفی",
            titleEnglish = "General Affidavit",
            category = DocumentCategory.AFFIDAVIT,
            descriptionUrdu = "عدالتی و سرکاری امور کے لیے جامع بیان حلفی مع تصدیق اوتھ کمشنر",
            documentModel = doc
        )
    }

    private fun createPowerOfAttorneyTemplate(): LegalTemplate {
        val paragraphs = listOf(
            ParagraphModel(
                text = "مختار نامہ عام",
                isBold = true,
                fontSizeSp = 24f,
                alignment = TextAlignment.CENTER,
                isHeading = true,
                headingLevel = 1
            ),
            ParagraphModel(
                text = "منکہ مسمی ________________ ولد ________________ ساکن ________________ شناختی کارڈ نمبر ________________ (مقر / پرنسپل)",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "مقررہ مسمی ________________ ولد ________________ ساکن ________________ شناختی کارڈ نمبر ________________ کو اپنا مختار عام مقرر کرتا ہوں:",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۱۔ یہ کہ مختار موصوف میری طرف سے جملہ جائیداد منقولہ و غیر منقولہ کا انتظام و انصرام کرنے کا مکمل مجاز ہوگا۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۲۔ یہ کہ مختار موصوف تمام عدالتی کارروائی، وکیل مقرر کرنے، جواب دعویٰ داخل کرنے اور بیان دینے کا مجاز ہوگا۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۳۔ مختار موصوف کا ہر وہ فعل جو اس مختار نامے کے تحت سرانجام دیا جائے گا، ایسا تصور ہوگا گویا کہ خود مقر نے انجام دیا ہے۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "مقر / دستخط کنندہ: ________________\nشناختی کارڈ: ________________\nگواہ اول: ________________\nگواہ دوم: ________________",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.6f
            )
        )

        val doc = DocumentModel(
            title = "مختار نامہ عام (Power of Attorney)",
            category = DocumentCategory.POWER_OF_ATTORNEY,
            pages = listOf(PageModel(pageNumber = 1, paragraphs = paragraphs))
        )

        return LegalTemplate(
            id = "template_poa",
            titleUrdu = "مختار نامہ عام",
            titleEnglish = "General Power of Attorney",
            category = DocumentCategory.POWER_OF_ATTORNEY,
            descriptionUrdu = "جائیداد اور عدالتی معاملات کی نمائندگی کے لیے مستند مختار نامہ",
            documentModel = doc
        )
    }

    private fun createTenancyAgreementTemplate(): LegalTemplate {
        val paragraphs = listOf(
            ParagraphModel(
                text = "معاہدہ کرایہ داری",
                isBold = true,
                fontSizeSp = 24f,
                alignment = TextAlignment.CENTER,
                isHeading = true,
                headingLevel = 1
            ),
            ParagraphModel(
                text = "فریق اول (مالک مکان): مسمی ________________ شناختی کارڈ ________________\nفریق دوم (کرایہ دار): مسمی ________________ شناختی کارڈ ________________",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.6f
            ),
            ParagraphModel(
                text = "۱۔ یہ کہ فریق اول نے اپنا مکان واقع ________________ فریق دوم کو بمبلغ ________________ روپے ماہوار کرایہ پر دیا ہے۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۲۔ یہ کہ کرایہ داری کی مدت ۱۱ ماہ ہوگی جو کہ تاریخ ____/____/۲۰۲ء سے نافذ العمل ہوگی۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۳۔ فریق دوم نے بطور زرضمانت (سیکیورٹی) مبلغ ________________ روپے فریق اول کے پاس جمع کروائے ہیں۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "دستخط فریق اول (مالک): ________________\nدستخط فریق دوم (کرایہ دار): ________________\nگواہ شد ۱: ________________\nگواہ شد ۲: ________________",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.6f
            )
        )

        val doc = DocumentModel(
            title = "معاہدہ کرایہ داری (Tenancy Agreement)",
            category = DocumentCategory.AGREEMENT,
            pages = listOf(PageModel(pageNumber = 1, paragraphs = paragraphs))
        )

        return LegalTemplate(
            id = "template_tenancy",
            titleUrdu = "معاہدہ کرایہ داری",
            titleEnglish = "Tenancy / Rent Agreement",
            category = DocumentCategory.AGREEMENT,
            descriptionUrdu = "رہائشی و کمرشل جائیداد کرایہ پر دینے کا قانونی اقرار نامہ",
            documentModel = doc
        )
    }

    private fun createLegalNoticeTemplate(): LegalTemplate {
        val paragraphs = listOf(
            ParagraphModel(
                text = "قانونی نوٹس",
                isBold = true,
                fontSizeSp = 24f,
                alignment = TextAlignment.CENTER,
                isHeading = true,
                headingLevel = 1
            ),
            ParagraphModel(
                text = "بخدمت: جناب ________________ ولد ________________ ساکن ________________",
                fontSizeSp = 18f,
                isBold = true,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "ذریعہ: ایڈووکیٹ ہائی کورٹ / سیشن کورٹ ________________",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "میرے موکل مسمی ________________ کی ہدایات کے مطابق آپ کو بذریعہ ہذا نوٹس دیا جاتا ہے کہ:",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۱۔ یہ کہ آپ نے میرے موکل کے ساتھ معاہدہ مورخہ ____/____/۲۰۲ء کی صریحاً خلاف ورزی کی ہے۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "۲۔ لہٰذا آپ کو متنبہ کیا جاتا ہے کہ اندر میعاد ۱۴ یوم اپنے واجبات ادا کریں بصورت دیگر آپ کے خلاف قانونی چارہ جوئی عمل میں لائی جائے گی۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "العارض:\nکونسل برائے سائل: ________________ ایڈووکیٹ\nمورخہ: ____/____/۲۰۲ء",
                fontSizeSp = 18f,
                alignment = TextAlignment.LEFT,
                lineSpacing = 1.6f
            )
        )

        val doc = DocumentModel(
            title = "قانونی نوٹس (Legal Notice)",
            category = DocumentCategory.LEGAL_NOTICE,
            pages = listOf(PageModel(pageNumber = 1, paragraphs = paragraphs))
        )

        return LegalTemplate(
            id = "template_notice",
            titleUrdu = "قانونی نوٹس",
            titleEnglish = "Legal Notice",
            category = DocumentCategory.LEGAL_NOTICE,
            descriptionUrdu = "وکلاء و قانونی چارہ جوئی کے لیے رسمی قانونی نوٹس کا فارمیٹ",
            documentModel = doc
        )
    }

    private fun createBailApplicationTemplate(): LegalTemplate {
        val paragraphs = listOf(
            ParagraphModel(
                text = "بعدالت جناب ایڈیشنل سیشن جج صاحب ________________",
                isBold = true,
                fontSizeSp = 22f,
                alignment = TextAlignment.CENTER,
                isHeading = true,
                headingLevel = 1
            ),
            ParagraphModel(
                text = "مقدمہ نمبر: ________________ بجرم دفعہ: ________________ تھانہ: ________________",
                fontSizeSp = 18f,
                alignment = TextAlignment.CENTER,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "مسمی ________________ ولد ________________ (سائل / ملزم)\nبمقابلہ\nسرکار (رسپانڈنٹ)",
                fontSizeSp = 18f,
                isBold = true,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.6f
            ),
            ParagraphModel(
                text = "درخواست برائے ضمانت قبل از گرفتاری / بعد از گرفتاری",
                isBold = true,
                fontSizeSp = 20f,
                alignment = TextAlignment.CENTER,
                isHeading = true,
                headingLevel = 2
            ),
            ParagraphModel(
                text = "جناب عالی! سائل حسب ذیل عرض گزار ہے:\n۱۔ یہ کہ سائل بے گناہ ہے اور اسے بدنیتی کی بنیاد پر جھوٹے مقدمے میں ملوث کیا گیا ہے۔\n۲۔ یہ کہ سائل سے کوئی برآمدگی مطلوب نہیں ہے اور مقدمہ مزید انکوائری کا متقاضی ہے۔\n۳۔ یہ کہ سائل عدالت عالیہ کے روبرو ہر تاریخ پر پیش ہونے کا پابند رہے گا۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.6f
            ),
            ParagraphModel(
                text = "استدعا ہے کہ سائل کی ضمانت منظور فرمائی جائے۔",
                isBold = true,
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "سائل بذریعہ کونسل: ________________ ایڈووکیٹ\nمورخہ: ____/____/۲۰۲ء",
                fontSizeSp = 18f,
                alignment = TextAlignment.LEFT,
                lineSpacing = 1.6f
            )
        )

        val doc = DocumentModel(
            title = "درخواست ضمانت (Bail Application)",
            category = DocumentCategory.APPLICATION,
            pages = listOf(PageModel(pageNumber = 1, paragraphs = paragraphs))
        )

        return LegalTemplate(
            id = "template_bail",
            titleUrdu = "درخواست ضمانت",
            titleEnglish = "Bail Application",
            category = DocumentCategory.APPLICATION,
            descriptionUrdu = "عدالت عالیہ و ماتحت عدالتوں کے لیے باقاعدہ درخواست ضمانت",
            documentModel = doc
        )
    }

    private fun createPoliceApplicationTemplate(): LegalTemplate {
        val paragraphs = listOf(
            ParagraphModel(
                text = "بخدمت جناب ایس ایچ او صاحب تھانہ ________________",
                isBold = true,
                fontSizeSp = 22f,
                alignment = TextAlignment.RIGHT,
                isHeading = true,
                headingLevel = 1
            ),
            ParagraphModel(
                text = "عنوان: درخواست برائے اندراج مقدمہ و کارروائی قانونی",
                isBold = true,
                fontSizeSp = 20f,
                alignment = TextAlignment.CENTER,
                isHeading = true,
                headingLevel = 2
            ),
            ParagraphModel(
                text = "جناب عالی! گزارش ہے کہ سائل مسمی ________________ ولد ________________ ساکن ________________ کا رہائشی ہے۔ وقوعہ یہ ہے کہ مورخہ ____/____/۲۰۲ء بوقت ____ بجے مسمیان ________________ نے سائل کے ساتھ نامناسب رویہ اختیار کیا اور سنگین نتائج کی دھمکیاں دیں۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.6f
            ),
            ParagraphModel(
                text = "لہٰذا استدعا ہے کہ ملزمان کے خلاف فوری قانونی کارروائی عمل میں لائی جائے اور مقدمہ درج کیا جائے۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "عرضے: ________________\nشناختی کارڈ: ________________\nموبائل نمبر: ________________",
                fontSizeSp = 18f,
                alignment = TextAlignment.LEFT,
                lineSpacing = 1.6f
            )
        )

        val doc = DocumentModel(
            title = "درخواست برائے اندراج مقدمہ (Police Complaint)",
            category = DocumentCategory.APPLICATION,
            pages = listOf(PageModel(pageNumber = 1, paragraphs = paragraphs))
        )

        return LegalTemplate(
            id = "template_police",
            titleUrdu = "درخواست اندراج مقدمہ",
            titleEnglish = "Police / FIR Complaint",
            category = DocumentCategory.APPLICATION,
            descriptionUrdu = "تھانے و پولیس افسران کو قانونی شکایت و کارروائی کی باقاعدہ درخواست",
            documentModel = doc
        )
    }

    private fun createPromissoryNoteTemplate(): LegalTemplate {
        val paragraphs = listOf(
            ParagraphModel(
                text = "اقرار نامہ و رسید امانت",
                isBold = true,
                fontSizeSp = 24f,
                alignment = TextAlignment.CENTER,
                isHeading = true,
                headingLevel = 1
            ),
            ParagraphModel(
                text = "منکہ مسمی ________________ ولد ________________ شناختی کارڈ ________________ کا رہائشی ہوں۔ اقرار کرتا ہوں کہ میں نے مسمی ________________ سے مبلغ ________________ روپے بحساب قرض حسنہ / امانت وصول پائے ہیں۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.6f
            ),
            ParagraphModel(
                text = "اور وعدہ کرتا ہوں کہ مذکورہ رقم مورخہ ____/____/۲۰۲ء تک لازماً واپس ادا کر دوں گا۔",
                fontSizeSp = 18f,
                alignment = TextAlignment.JUSTIFY,
                lineSpacing = 1.5f
            ),
            ParagraphModel(
                text = "المقر / وصول کنندہ: ________________\nدستخط و نشان انگوٹھا: ________________\nگواہ شد ۱: ________________\nگواہ شد ۲: ________________",
                fontSizeSp = 18f,
                alignment = TextAlignment.RIGHT,
                lineSpacing = 1.6f
            )
        )

        val doc = DocumentModel(
            title = "اقرار نامہ و رسید امانت (Promissory Note)",
            category = DocumentCategory.AGREEMENT,
            pages = listOf(PageModel(pageNumber = 1, paragraphs = paragraphs))
        )

        return LegalTemplate(
            id = "template_promissory",
            titleUrdu = "اقرار نامہ و رسید امانت",
            titleEnglish = "Promissory Note & Receipt",
            category = DocumentCategory.AGREEMENT,
            descriptionUrdu = "رقم کی وصولی، ادھار و لین دین کا باضابطہ اقرار نامہ",
            documentModel = doc
        )
    }
}
