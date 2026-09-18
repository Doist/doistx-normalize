package doist.x.normalize

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UShortVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.posix.free
import platform.posix.memcpy
import platform.posix.size_tVar
import uninorm.UNINORM_NFC
import uninorm.UNINORM_NFD
import uninorm.UNINORM_NFKC
import uninorm.UNINORM_NFKD
import uninorm.u16_normalize

public actual fun String.normalize(form: Form): String {
    if (isEmpty()) return this

    return usePinned { pinnedInput ->
        memScoped {
            val length = alloc<size_tVar>()
            val result = u16_normalize(
                nf = form.nativeForm(),
                s = pinnedInput.addressOf(0).reinterpret(),
                n = this@normalize.length.convert(),
                resultbuf = null,
                lengthp = length.ptr,
            ) ?: return@memScoped this@normalize

            try {
                check(length.value <= Int.MAX_VALUE.toULong()) { "Normalized string is too long" }
                result.readString(length.value.toInt())
            } finally {
                free(result)
            }
        }
    }
}

private fun CPointer<UShortVar>.readString(length: Int): String {
    val chars = CharArray(length)
    if (length != 0) {
        chars.usePinned {
            memcpy(
                __dest = it.addressOf(0),
                __src = this,
                __n = (length.toLong() * sizeOf<UShortVar>()).convert(),
            )
        }
    }
    return chars.concatToString()
}

private fun Form.nativeForm() = when (this) {
    Form.NFC -> UNINORM_NFC
    Form.NFD -> UNINORM_NFD
    Form.NFKC -> UNINORM_NFKC
    Form.NFKD -> UNINORM_NFKD
}
