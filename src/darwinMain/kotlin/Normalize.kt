package doist.x.normalize

import platform.Foundation.*

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
