import com.example.astra.model.VedicChartRequest
import com.squareup.okhttp.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface WesternApi {

    @POST("api/v1/western/chart-svg")
    suspend fun generateSvgChart(
        @Header("x-api-key") apiKey: String,
        @Body request: VedicChartRequest
    ): ResponseBody
}