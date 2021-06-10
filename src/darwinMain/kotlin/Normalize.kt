package doist.x.normalize

import platform.Foundation.NSString
import platform.Foundation.decomposedStringWithCanonicalMapping
import platform.Foundation.decomposedStringWithCompatibilityMapping
import platform.Foundation.precomposedStringWithCanonicalMapping
import platform.Foundation.precomposedStringWithCompatibilityMapping

actual fun String.normalize(form: Form): String {
    @Suppress("CAST_NEVER_SUCCEEDS")
    val str = this as NSString
    return when (form) {
        Form.NFC -> str.precomposedStringWithCanonicalMapping
        Form.NFD -> str.decomposedStringWithCanonicalMapping
        Form.NFKC -> str.precomposedStringWithCompatibilityMapping
        Form.NFKD -> str.decomposedStringWithCompatibilityMapping
    }
}
