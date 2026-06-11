data class VedicChartResponse(
    val ascendant: Ascendant,
    val planets: List<Planet>
)

data class Ascendant(
    val sign: String,
    val degree: Double
)

data class Planet(
    val name: String,
    val sign: String,
    val house: Int
)