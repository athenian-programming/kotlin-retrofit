package org.athenian

import kotlinx.coroutines.*
import org.athenian.Common.okHttpClient
import org.athenian.Common.requestCount
import org.athenian.Common.service
import org.athenian.Common.threadCount
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    fun CoroutineScope.withSuspend(service: DelayedService, dispatcher: ExecutorCoroutineDispatcher) {
        launch(dispatcher) {
            log("Launching request with suspend")
            val (_, d) =
                measureTimedValue {
                    service.withSuspend()
                    //delay(1.seconds.toLongMilliseconds())
                }
            println("Suspending call time: $d")
        }
    }

    println("Pool size = ${okHttpClient.connectionPool().connectionCount()}")

    Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()
        .use { poolDispatcher ->
            val (_, d) =
                measureTimedValue {
                    runBlocking {
                        repeat(requestCount) {
                            withSuspend(service, poolDispatcher)
                        }
                    }
                }
            println("Total time with suspending: $d Pool size: ${okHttpClient.connectionPool().connectionCount()}")
        }

    exitProcess(0)
}