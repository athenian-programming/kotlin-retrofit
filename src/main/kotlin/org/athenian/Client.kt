package org.athenian

import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

interface TestService {
    @GET("delayed")
    fun noCoroutine(): Call<Map<String, String>>

    @GET("delayed")
    suspend fun withCoroutine(): Map<String, String>
}

fun CoroutineScope.noCoroutine(count: Int, service: TestService, dispatcher: ExecutorCoroutineDispatcher) {
    repeat(count) {
        launch(dispatcher) {
            log("Launching no coutine version")
            service.noCoroutine().execute().body()
        }
    }
}

@ExperimentalTime
fun CoroutineScope.withCoroutine(count: Int, service: TestService, dispatcher: ExecutorCoroutineDispatcher) {
    repeat(count) {
        launch(dispatcher) {
            log("Launching with coutine version")
            val (_, dur) =
                measureTimedValue {
                    service.withCoroutine()
                }
            println("Suspending call sub-time = $dur")
        }
    }
}


@ExperimentalTime
fun main() {

    val threadCount = 5
    val requestCount = 10

    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(TestService::class.java)

    Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()
        .use { poolDispatcher ->

            val (_, noCoDur) =
                measureTimedValue {
                    runBlocking {
                        noCoroutine(requestCount, service, poolDispatcher)
                    }
                }
            println("No coroutine: $noCoDur")

            val (_, withCoDur) =
                measureTimedValue {
                    runBlocking {
                        withCoroutine(requestCount, service, poolDispatcher)
                    }
                }
            println("With coroutine: $withCoDur")
        }

    exitProcess(0)
}