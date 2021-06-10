package doist.x.normalize

import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.windows.NormalizationC
import platform.windows.NormalizationD
import platform.windows.NormalizationKC
import platform.windows.NormalizationKD
import platform.windows.NormalizeString
import platform.windows.WCHARVar

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
