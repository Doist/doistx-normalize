package doist.x.normalize

public actual fun String.normalize(form: Form): String = normalizeImpl(this, form.name)

private fun normalizeImpl(str: String, form: String): String = js("""str.normalize(form)""")
