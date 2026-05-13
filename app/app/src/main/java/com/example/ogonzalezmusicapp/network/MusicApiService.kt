package com.example.ogonzalezmusicapp.network

import com.example.ogonzalezmusicapp.models.Album
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicApiService {
    @GET("api/albums")
    suspend fun getAlbums(): List<Album>

    // ¡AQUÍ ESTÁ EL CAMBIO! Pasamos de Int a String
    @GET("api/albums/{id}")
    suspend fun getAlbumById(@Path("id") id: String): Album
}

object RetrofitClient {
    private const val BASE_URL = "https://musicapi.pjasoft.com/"

    val instance: MusicApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApiService::class.java)
    }
}