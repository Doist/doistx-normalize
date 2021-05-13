package doist.x.normalize

import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.windows.*

actual fun String.normalize(form: Form): String = memScoped {
    val winForm = when (form) {
        Form.NFC -> NormalizationC
        Form.NFD -> NormalizationD
        Form.NFKC -> NormalizationKC
        Form.NFKD -> NormalizationKD
    }
    val size = NormalizeString(winForm, this@normalize, -1, null, 0)
    val result = allocArray<WCHARVar>(size)
    NormalizeString(winForm, this@normalize, -1, result, size)
    result.toKString()
}
