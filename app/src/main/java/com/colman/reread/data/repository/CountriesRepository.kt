package com.colman.reread.data.repository

import com.colman.reread.model.Country

interface CountriesRepository {
    suspend fun getCountries(): List<Country>
}