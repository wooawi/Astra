data class MatrixRequest(
    val name: String,
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val city: String,
    val format: String = "png",
    val theme_type: String = "dark"
)