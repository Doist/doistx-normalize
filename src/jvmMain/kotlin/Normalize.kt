package doist.x.normalize

import java.text.Normalizer

public actual fun String.normalize(form: Form): String {
    return Normalizer.normalize(this, when (form) {
        Form.NFC -> Normalizer.Form.NFC
        Form.NFD -> Normalizer.Form.NFD
        Form.NFKC -> Normalizer.Form.NFKC
        Form.NFKD -> Normalizer.Form.NFKD
    })
}
