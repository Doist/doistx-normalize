import doist.x.normalize.Form
import doist.x.normalize.normalize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

class NormalizeTest {
    @Test
    fun formValues() {
        val forms = Form.values()
        assertEquals(4, forms.size)
        assertTrue(forms.contains(Form.NFC))
        assertTrue(forms.contains(Form.NFD))
        assertTrue(forms.contains(Form.NFKC))
        assertTrue(forms.contains(Form.NFKD))
    }

    @Test
    fun testFormValueOf() {
        assertEquals(Form.NFC, Form.valueOf("NFC"))
        assertEquals(Form.NFD, Form.valueOf("NFD"))
        assertEquals(Form.NFKC, Form.valueOf("NFKC"))
        assertEquals(Form.NFKD, Form.valueOf("NFKD"))

        assertFails { Form.valueOf("nope") }
        assertFails { Form.valueOf("nfc") }
        assertFails { Form.valueOf("NFC ") }
    }

    @Test
    fun normalizeAnnex15() {
        val fixtures = arrayOf(
            // [source, nfc, nfd, nfkc, nfkd]
            arrayOf("\u00c1", "\u00c1", "\u0041\u0301", "\u00c1", "\u0041\u0301"),
            arrayOf("\u0041\u0301", "\u00c1", "\u0041\u0301", "\u00c1", "\u0041\u0301"),
            arrayOf("\ufb03", "\ufb03", "\ufb03", "\u0066\u0066\u0069", "\u0066\u0066\u0069"),
            arrayOf(
                "\u0066\u0066\u0069",
                "\u0066\u0066\u0069",
                "\u0066\u0066\u0069",
                "\u0066\u0066\u0069",
                "\u0066\u0066\u0069"
            ),
            arrayOf("", "", "", "", ""),
            arrayOf("schön", "schön", "scho\u0308n", "schön", "scho\u0308n"),
            arrayOf("Äffin", "Äffin", "A\u0308ffin", "Äffin", "A\u0308ffin"),
            arrayOf("Ä\uFB03n", "Ä\uFB03n", "A\u0308\uFB03n", "Äffin", "A\u0308ffin"),
            arrayOf("Henry IV", "Henry IV", "Henry IV", "Henry IV", "Henry IV"),
            arrayOf("Henry \u2163", "Henry \u2163", "Henry \u2163", "Henry IV", "Henry IV"),
        )

        fixtures.forEachIndexed { i, (src, nfc, nfd, nfkc, nfkd) ->
            assertEquals(nfc, src.normalize(Form.NFC), "NFC test ${i + 1}")
            assertEquals(nfd, src.normalize(Form.NFD), "NFD test ${i + 1}")
            assertEquals(nfkc, src.normalize(Form.NFKC), "NFKC test ${i + 1}")
            assertEquals(nfkd, src.normalize(Form.NFKD), "NFKD test ${i + 1}")
        }
    }

    @Test
    fun normalizeMaxExpansion() {
        val a = "(ﷺ)"
        val b = "(صلى الله عليه وسلم)"
        assertEquals(b, a.normalize(Form.NFKD))
        assertEquals(b.repeat(4), a.repeat(4).normalize(Form.NFKD))
    }
}
