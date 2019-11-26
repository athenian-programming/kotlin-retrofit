package org.athenian

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Config {
  val threadCount = 20
  val requestCount = 12

  val okHttpClient =
    OkHttpClient.Builder()
      .run {
        connectionPool(ConnectionPool(20, 10, TimeUnit.MINUTES))
        build()
      }

  val retrofit =
    Retrofit.Builder()
      .run {
        baseUrl("http://localhost:8080/")
        client(okHttpClient)
        addConverterFactory(GsonConverterFactory.create())
        build()
      }
}

