import com.goncalossilva.resources.Resource

actual fun loadTestData(): String =
    Resource("src/commonTest/resources/NormalizationTest.txt").readText()
