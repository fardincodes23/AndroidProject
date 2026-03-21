package ca.hccis.perfumeshop

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // PASTE YOUR MOCKAPI URL HERE (Make sure it ends with a forward slash /)
    private const val BASE_URL = "https://69bebb2c17c3d7d97792d8a4.mockapi.io/api/v1/"

    val apiService: PerfumeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PerfumeApiService::class.java)
    }
}