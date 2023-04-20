package doist.x.normalize

import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
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
    var sizeEstimate = NormalizeString(winForm, this@normalize, -1, null, 0)
    while (sizeEstimate > 0) {
        val result = allocArray<WCHARVar>(sizeEstimate)
        val size = NormalizeString(winForm, this@normalize, -1, result, sizeEstimate)
        if (size <= 0 && GetLastError() == ERROR_INSUFFICIENT_BUFFER.convert<UInt>()) {
            // Updated size estimate is `-size`.
            // See: https://learn.microsoft.com/en-us/windows/win32/intl/nls--unicode-normalization-sample
            sizeEstimate = -size
        } else {
            return result.toKString()
        }
    }
    return this@normalize
}
