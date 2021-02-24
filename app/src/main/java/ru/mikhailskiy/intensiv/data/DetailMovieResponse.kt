package ru.mikhailskiy.intensiv.data

import com.google.gson.annotations.SerializedName

data class DetailMovieResponse (
    var page: Int,
    var original_title: String,
    var name: String = "",
    var overview: String,
    var poster_path: String,
    var release_date: String,
    var vote_average: Double,
    var genres: Array<Genre>,
    var production_companies: Array<Company>,
    @SerializedName("total_results")
    var totalResults: Int,
    @SerializedName("total_pages")
    var totalPages: Int
)
data class Genre(
    val name: String?
)
data class Company(
    val name: String?
)