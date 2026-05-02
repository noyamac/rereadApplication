package com.colman.reread.data.repository

import com.colman.reread.data.networking.NetworkClient
import com.colman.reread.model.Country

class RemoteCountryRepository: CountriesRepository {
    companion object {
        val shared = RemoteCountryRepository()
    }

    override suspend fun getCountries(): List<Country> {
        return try {
            val response = NetworkClient.countriesApiClient.getCountries()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}