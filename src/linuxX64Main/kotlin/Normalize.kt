package doist.x.normalize

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.utf8
import platform.posix.size_tVar
import uninorm.UNINORM_NFC
import uninorm.UNINORM_NFD
import uninorm.UNINORM_NFKC
import uninorm.UNINORM_NFKD
import uninorm.u8_normalize

actual fun String.normalize(form: Form) = memScoped {
    val str = this@normalize.utf8
    val uninormForm = when (form) {
        Form.NFC -> UNINORM_NFC
        Form.NFD -> UNINORM_NFD
        Form.NFKC -> UNINORM_NFKC
        Form.NFKD -> UNINORM_NFKD
    }
    val result = u8_normalize(
        uninormForm, str.ptr.reinterpret(), str.size.toULong(), null, alloc<size_tVar>().ptr
    )!!
    result.reinterpret<ByteVar>().toKString()
}
