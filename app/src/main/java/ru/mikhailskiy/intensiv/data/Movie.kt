package ru.mikhailskiy.intensiv.data

class Movie(
    var title: String? = "",
    var backdrop_path: String? = "",
    var id: Int?,
    var voteAverage: Double = 0.0
) {
    val rating: Float
        get() = voteAverage.div(2).toFloat()
    val imageUrl: String get() = "https://image.tmdb.org/t/p/w500$backdrop_path"
}
