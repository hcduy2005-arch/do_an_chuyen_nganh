package com.example.doanckapi.data.api

import com.example.doanckapi.data.model.PredictionRequest
import com.example.doanckapi.data.model.PredictionResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("predict")
    suspend fun predictDiabetes(@Body request: PredictionRequest): PredictionResponse
}

object RetrofitClient {
    // Replace with actual Python API URL (e.g. 10.0.2.2 for Android Emulator pointing to localhost)
    private const val BASE_URL = "http://10.0.2.2:8000/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
