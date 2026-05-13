package com.example.ogonzalezmusicapp.models

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("image") val image: String,
    @SerializedName("description") val description: String? = null
)