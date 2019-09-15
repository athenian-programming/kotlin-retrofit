package org.athenian

import kotlinx.coroutines.*
import org.athenian.Common.okHttpClient
import org.athenian.Common.requestCount
import org.athenian.Common.service
import org.athenian.Common.threadCount
import java.util.concurrent.Executors
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


@ExperimentalTime
fun main() {
    fun CoroutineScope.withoutSuspend(service: DelayedService, dispatcher: ExecutorCoroutineDispatcher) {
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
}