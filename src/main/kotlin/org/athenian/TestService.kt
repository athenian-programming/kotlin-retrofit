package org.athenian

import retrofit2.Call
import retrofit2.http.GET

interface TestService {
    @GET("delayed")
    fun withoutSuspend(): Call<Map<String, String>>

    @GET("delayed")
    suspend fun withSuspend(): Map<String, String>

}