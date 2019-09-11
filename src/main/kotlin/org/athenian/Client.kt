package org.athenian

import kotlinx.coroutines.*
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

interface TestService {
    @GET("delayed")
    fun noCoroutine(): Call<Map<String, String>>

    @GET("delayed")
    suspend fun withCoroutine(): Map<String, String>
}

fun CoroutineScope.noCoroutine(service: TestService, dispatcher: ExecutorCoroutineDispatcher) {
    launch(dispatcher) {
        log("Launching no coutine request")
        service.noCoroutine().execute().body()
    }
}

@ExperimentalTime
fun CoroutineScope.withCoroutine(service: TestService, dispatcher: ExecutorCoroutineDispatcher) {
    launch(dispatcher) {
        log("Launching coroutine request")
        val (_, dur) =
            measureTimedValue {
                service.withCoroutine()
                //delay(1.seconds.toLongMilliseconds())
            }
        println("Suspending call time: $dur")
    }
}


@ExperimentalTime
fun main() {

    val connectionPoolSize = 20
    val threadCount = 20
    val requestCount = 12

    val okHttpClient =
        OkHttpClient.Builder()
            .run {
                connectionPool(ConnectionPool(connectionPoolSize, 5, TimeUnit.MINUTES))
                build()
            }

    println("Pool size = ${okHttpClient.connectionPool().connectionCount()}")

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(TestService::class.java)

    Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()
        .use { poolDispatcher ->

            val (_, noDur) =
                measureTimedValue {
                    runBlocking {
                        repeat(requestCount) {
                            noCoroutine(service, poolDispatcher)
                        }
                    }
                }
            println("Total no coroutine time: $noDur  pool size: ${okHttpClient.connectionPool().connectionCount()}\n")

            val (_, withDur) =
                measureTimedValue {
                    runBlocking {
                        repeat(requestCount) {
                            withCoroutine(service, poolDispatcher)
                        }
                    }
                }
            println("Total coroutine time: $withDur pool size: ${okHttpClient.connectionPool().connectionCount()}")
        }

    exitProcess(0)
}