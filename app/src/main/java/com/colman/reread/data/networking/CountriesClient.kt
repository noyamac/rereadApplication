package com.colman.reread.data.networking

import com.colman.reread.model.Country
import retrofit2.http.GET
import retrofit2.http.Query


interface CountriesClient {
    @GET("v2/all")
    suspend fun getCountries(@Query("fields") fields: String = "name"): retrofit2.Response<List<Country>>
}