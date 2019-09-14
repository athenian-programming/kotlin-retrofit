package org.athenian

import kotlin.concurrent.thread
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


@ExperimentalTime
fun main() {
    fun withThread(service: TestService) =
        thread {
            log("Launching request with thread")
            service.withoutSuspend().execute().body()
        }

    val (_, d) =
        measureTimedValue {
            val reqs = List(requestCount) { withThread(service) }
            reqs.forEach { it.join() }
        }
    println("Total time with thread: $d Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")

    exitProcess(0)
}