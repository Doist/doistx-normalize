package doist.x.normalize

import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import platform.windows.GetLastError
import platform.windows.NormalizationC
import platform.windows.NormalizationD
import platform.windows.NormalizationKC
import platform.windows.NormalizationKD
import platform.windows.NormalizeString
import platform.windows.WCHARVar
import platform.windows.ERROR_INSUFFICIENT_BUFFER

public actual fun String.normalize(form: Form): String = memScoped {
    val winForm = when (form) {
        Form.NFC -> NormalizationC
        Form.NFD -> NormalizationD
        Form.NFKC -> NormalizationKC
        Form.NFKD -> NormalizationKD
    }
    val sourceLength = this@normalize.length
    var sizeEstimate = NormalizeString(winForm, this@normalize, sourceLength, null, 0)
    while (sizeEstimate > 0) {
        val result = allocArray<WCHARVar>(sizeEstimate)
        val size = NormalizeString(winForm, this@normalize, sourceLength, result, sizeEstimate)
        if (size <= 0) {
            if (GetLastError() == ERROR_INSUFFICIENT_BUFFER.convert<UInt>()) {
                // Updated size estimate is `-size`.
                // See: https://learn.microsoft.com/en-us/windows/win32/intl/nls--unicode-normalization-sample
                sizeEstimate = -size
                continue
            }
            return this@normalize
        }

        val chars = CharArray(size)
        for (i in 0 until size) {
            chars[i] = result[i].toInt().toChar()
        }
        return chars.concatToString()
    }
    return this@normalize
}
