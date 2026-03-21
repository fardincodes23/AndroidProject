package ca.hccis.perfumeshop // Check your package name!

import entity.PerfumeTransaction
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PerfumeApiService {

    // GET: Pulls the list of transactions from the API
    @GET("transactions")
    suspend fun getTransactions(): List<PerfumeTransaction>

    // POST: Sends a new transaction to the API
    @POST("transactions")
    suspend fun addTransaction(@Body transaction: PerfumeTransaction): PerfumeTransaction
}