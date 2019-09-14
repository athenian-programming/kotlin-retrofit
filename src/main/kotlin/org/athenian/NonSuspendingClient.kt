package org.athenian

import kotlinx.coroutines.*
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


@ExperimentalTime
fun main() {
    fun CoroutineScope.withoutSuspend(service: TestService, dispatcher: ExecutorCoroutineDispatcher) {
        launch(dispatcher) {
            log("Launching request without suspend")
            service.withoutSuspend().execute().body()
        }
    }

    println("Pool size = ${okHttpClient.connectionPool().connectionCount()}")

    Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()
        .use { poolDispatcher ->
            val (_, noSus) =
                measureTimedValue {
                    runBlocking {
                        repeat(requestCount) {
                            withoutSuspend(service, poolDispatcher)
                        }
                    }
                }
            println("Total time without suspending: $noSus Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")
        }

    exitProcess(0)
}