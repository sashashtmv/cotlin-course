package ru.mikhailskiy.intensiv.data

data class Movie(
    var title: String? = "",
    var name: String? = "",
    var backdrop_path: String? = "",
    var poster_path: String? = "",
    var id: Int?,
    var voteAverage: Double = 0.0
) {
    val rating: Float
        get() = voteAverage.div(2).toFloat()
    val imageUrl: String get() = if (backdrop_path != null) "https://image.tmdb.org/t/p/w500$backdrop_path" else "https://image.tmdb.org/t/p/w500$poster_path"
}
