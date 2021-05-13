package doist.x.normalize

import kotlinx.cinterop.*
import platform.posix.size_tVar
import uninorm.*

actual fun String.normalize(form: Form) = memScoped {
    val str = this@normalize.utf8
    val uninormForm = when (form) {
        Form.NFC -> UNINORM_NFC
        Form.NFD -> UNINORM_NFD
        Form.NFKC -> UNINORM_NFKC
        Form.NFKD -> UNINORM_NFKD
    }
    val result = u8_normalize(
        uninormForm, str.ptr.reinterpret(), str.size.toULong(), null, alloc<size_tVar>().ptr)!!
    result.reinterpret<ByteVar>().toKString()
}
