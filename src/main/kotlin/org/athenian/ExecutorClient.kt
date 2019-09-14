package org.athenian

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


@ExperimentalTime
fun main() {
    fun withService(service: TestService, executor: ExecutorService) =
        executor.submit {
            log("Launching request with thread")
            service.withoutSuspend().execute().body()
        }

    val executor = Executors.newFixedThreadPool(threadCount)

    val (_, d) = measureTimedValue {
        val reqs = List(requestCount) { withService(service, executor) }
        reqs.forEach { it.get() }
    }
    println("Total time with service: $d Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")

    exitProcess(0)
}