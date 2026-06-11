import com.example.astra.BuildConfig
import com.example.astra.model.VedicChartRequest

class MatrixRepository(
    private val vedicApi: VedicApi,
    private val westernApi: WesternApi
){

    suspend fun generate(
        request: VedicChartRequest
    ): Pair<VedicChartResponse, String> {

        val chart =
            vedicApi.generateVedicChart(
                "\"f3505cf2f8e65bd9d9bbf75cbe25f0ec0caa80ad5a77bfa09e1cb779a31c9167\"",
                request
            )

        val svg =
            westernApi.generateSvgChart(
                "\"f3505cf2f8e65bd9d9bbf75cbe25f0ec0caa80ad5a77bfa09e1cb779a31c9167\"",
                request
            ).string()

        return chart to svg
    }
}