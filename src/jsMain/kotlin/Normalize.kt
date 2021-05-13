package doist.x.normalize

actual fun String.normalize(form: Form): String {
    return asDynamic().normalize(form.name) as String
}
