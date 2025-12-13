import com.goncalossilva.resources.Resource

actual fun loadTestData(): String = Resource("NormalizationTest.txt").readText()
