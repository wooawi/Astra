import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitProvider {

    private const val BASE_URL =
        "https://json.freeastroapi.com/"

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val vedicApi: VedicApi =
        retrofit.create(VedicApi::class.java)

    val westernApi: WesternApi =
        retrofit.create(WesternApi::class.java)
}