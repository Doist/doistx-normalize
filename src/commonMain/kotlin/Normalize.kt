package doist.x.normalize

/**
 * Normalize the string according to the specified normalization form.
 *
 * Note: This method does not change the original string.
 */
expect fun String.normalize(form: Form): String

/**
 * Unicode normalization form as described in
 * [Unicode® Standard Annex #15](https://unicode.org/reports/tr15/).
 */
enum class Form {
    /**
     * Canonical decomposition, followed by canonical composition.
     */
    NFC,

    /**
     * Canonical decomposition.
     */
    NFD,

    /**
     * Compatibility decomposition, followed by canonical composition.
     */
    NFKC,

    /**
     * Compatibility decomposition.
     */
    NFKD
}
