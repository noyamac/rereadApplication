package com.colman.reread.data.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    val countriesApiClient: CountriesClient by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://restcountries.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(CountriesClient::class.java)

    }
}