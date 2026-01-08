package doist.x.normalize

import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.utf8
import kotlinx.cinterop.value
import platform.posix.free
import platform.posix.size_tVar
import uninorm.UNINORM_NFC
import uninorm.UNINORM_NFD
import uninorm.UNINORM_NFKC
import uninorm.UNINORM_NFKD
import uninorm.u8_normalize

public actual fun String.normalize(form: Form): String = memScoped {
    val str = this@normalize.utf8
    val uninormForm = when (form) {
        Form.NFC -> UNINORM_NFC
        Form.NFD -> UNINORM_NFD
        Form.NFKC -> UNINORM_NFKC
        Form.NFKD -> UNINORM_NFKD
    }
    val lengthVar = alloc<size_tVar>()
    val result = u8_normalize(
        uninormForm,
        str.ptr.reinterpret(),
        (str.size - 1).convert(),
        null,
        lengthVar.ptr,
    )
        ?: return this@normalize
    try {
        result.readBytes(lengthVar.value.toInt()).decodeToString()
    } finally {
        free(result)
    }
}
