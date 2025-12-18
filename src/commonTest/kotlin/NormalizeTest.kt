import com.goncalossilva.resources.Resource
import doist.x.normalize.Form
import doist.x.normalize.normalize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

fun loadTestData(): String = Resource("NormalizationTest.txt").readText()

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
    @Suppress("DestructuringDeclarationWithTooManyEntries")
    fun normalizeAnnex15() {
        // Ref: https://www.unicode.org/Public/10.0.0/ucd/NormalizationTest.txt
        // Version 10.0.0 is the latest version of the Unicode Standard that is mostly supported across all platforms,
        // except for a couple of lines skipped below. JDK 11 is especially problematic in newer versions.
        val fixtures = loadTestData()
        val skipLines = arrayOf(
            // Fails on all Darwin platforms.
            67, 68,
        )
        fixtures.lines().forEachIndexed { i, line ->
            if (line.startsWith("#") || line.startsWith("@") || line.isEmpty()) {
                return@forEachIndexed
            }

            val lineno = i + 1
            if (skipLines.contains(lineno)) {
                return@forEachIndexed
            }

            val (source, nfc, nfd, nfkc, nfkd) =
                line.split(";", limit = 6).dropLast(1).map { column ->
                    column.split(" ").flatMap { codepoint ->
                        val codePointInt = codepoint.toInt(radix = 16)
                        if (codePointInt <= 0xFFFF) {
                            sequenceOf(codePointInt.toChar())
                        } else {
                            val high = (codePointInt - 0x10000) / 0x400 + 0xD800
                            val low = (codePointInt - 0x10000) % 0x400 + 0xDC00
                            sequenceOf(high.toChar(), low.toChar())
                        }
                    }.joinToString(separator = "")
                }

            assertEquals(nfc, source.normalize(Form.NFC), "Line $lineno: NFC: $source: ")
            assertEquals(nfc, nfc.normalize(Form.NFC), "Line $lineno: NFC: $nfc: ")
            assertEquals(nfc, nfd.normalize(Form.NFC), "Line $lineno: NFC: $nfd: ")
            assertEquals(nfkc, nfkc.normalize(Form.NFC), "Line $lineno: NFC: $nfkc: ")
            assertEquals(nfkc, nfkd.normalize(Form.NFC), "Line $lineno: NFC: $nfkd: ")

            assertEquals(nfd, source.normalize(Form.NFD), "Line $lineno: NFD: $source: ")
            assertEquals(nfd, nfc.normalize(Form.NFD), "Line $lineno: NFD: $nfc: ")
            assertEquals(nfd, nfd.normalize(Form.NFD), "Line $lineno: NFD: $nfd: ")
            assertEquals(nfkd, nfkc.normalize(Form.NFD), "Line $lineno: NFD: $nfkc: ")
            assertEquals(nfkd, nfkd.normalize(Form.NFD), "Line $lineno: NFD: $nfkd: ")

            assertEquals(nfkc, source.normalize(Form.NFKC), "Line $lineno: NFKC: $source: ")
            assertEquals(nfkc, nfc.normalize(Form.NFKC), "Line $lineno: NFKC: $nfc: ")
            assertEquals(nfkc, nfd.normalize(Form.NFKC), "Line $lineno: NFKC: $nfd: ")
            assertEquals(nfkc, nfkc.normalize(Form.NFKC), "Line $lineno: NFKC: $nfkc: ")
            assertEquals(nfkc, nfkd.normalize(Form.NFKC), "Line $lineno: NFKC: $nfkd: ")

            assertEquals(nfkd, source.normalize(Form.NFKD), "Line $lineno: NFKD: $source: ")
            assertEquals(nfkd, nfc.normalize(Form.NFKD), "Line $lineno: NFKD: $nfc: ")
            assertEquals(nfkd, nfd.normalize(Form.NFKD), "Line $lineno: NFKD: $nfd: ")
            assertEquals(nfkd, nfkc.normalize(Form.NFKD), "Line $lineno: NFKD: $nfkc: ")
            assertEquals(nfkd, nfkd.normalize(Form.NFKD), "Line $lineno: NFKD: $nfkd: ")
        }
    }
}
