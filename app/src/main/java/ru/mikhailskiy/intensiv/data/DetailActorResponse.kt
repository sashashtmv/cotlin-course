package ru.mikhailskiy.intensiv.data

import com.google.gson.annotations.SerializedName

data class DetailActorResponse (
    var page: Int,
    var cast: List<Actor>,
    @SerializedName("total_results")
    var totalResults: Int,
    @SerializedName("total_pages")
    var totalPages: Int
)