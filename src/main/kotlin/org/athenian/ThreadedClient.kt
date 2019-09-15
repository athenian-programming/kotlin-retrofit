package org.athenian

import org.athenian.Common.okHttpClient
import org.athenian.Common.requestCount
import org.athenian.Common.service
import kotlin.concurrent.thread
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    fun withThread(service: DelayedService) =
        thread {
            log("Launching request with thread")
            service.withoutSuspend().execute().body()
        }

    val (_, d) =
        measureTimedValue {
            val requests = List(requestCount) { withThread(service) }
            requests.forEach { it.join() }
        }
    println("Total time with thread: $d Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")
}