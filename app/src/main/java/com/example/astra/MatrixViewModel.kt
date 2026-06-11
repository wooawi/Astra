import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astra.model.VedicChartRequest
import kotlinx.coroutines.launch

class MatrixViewModel : ViewModel() {

    private val repository =
        MatrixRepository(
            RetrofitProvider.vedicApi,
            RetrofitProvider.westernApi
        )

    var description by mutableStateOf("")
        private set

    var svgChart by mutableStateOf("")
        private set

    var loading by mutableStateOf(false)
        private set

    fun load(
        date: String,
        time: String,
        city: String
    ) {

        viewModelScope.launch {

            loading = true

            try {

                val d = date.split(".")
                val t = time.split(":")

                val request =
                    VedicChartRequest(
                        year = d[2].toInt(),
                        month = d[1].toInt(),
                        day = d[0].toInt(),
                        hour = t[0].toInt(),
                        minute = t[1].toInt(),
                        city = city
                    )

                val result =
                    repository.generate(request)

                val vedic = result.first
                val svg = result.second

                svgChart = svg

                description =
                    buildString {

                        appendLine(
                            "Асцендент: ${
                                vedic.ascendant.sign
                            }"
                        )

                        appendLine()

                        vedic.planets.forEach {

                            appendLine(
                                "${it.name} — ${it.sign}, дом ${it.house}"
                            )
                        }
                    }

            } catch (e: Exception) {

                description =
                    e.message ?: "Ошибка"

            }

            loading = false
        }
    }
}