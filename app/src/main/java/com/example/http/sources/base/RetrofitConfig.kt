package com.example.http.sources.base

import com.squareup.moshi.Moshi
import retrofit2.Retrofit


/**
 * All stuffs required for making HTTP-request with Retrofit client and
 * for parsing JSON-message.
 */
class RetrofitConfig(
    val retrofit: Retrofit,
    val moshi: Moshi
)
