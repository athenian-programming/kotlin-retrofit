package org.athenian

import retrofit2.Call
import retrofit2.http.GET

interface DelayedService {
  @GET("delayed")
  fun withBlock(): Call<Map<String, String>>

  @GET("delayed")
  suspend fun withSuspend(): Map<String, String>
}