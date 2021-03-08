package ru.mikhailskiy.intensiv.data

data class Actor(
    var profile_path: String? ="",
    var name: String? = ""
) {
    val imageUrl: String get() = "https://image.tmdb.org/t/p/w500$profile_path"
}
