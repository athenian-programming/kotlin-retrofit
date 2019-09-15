package org.athenian

import org.athenian.Common.okHttpClient
import org.athenian.Common.requestCount
import org.athenian.Common.service
import org.athenian.Common.threadCount
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main() {
    fun withService(id: Int, service: DelayedService, executor: ExecutorService) =
        executor.submit {
            log("Launching request $id with executor")
            service.withoutSuspend().execute().body()
        }

    val (_, d) =
        measureTimedValue {
            val executor = Executors.newFixedThreadPool(threadCount)
            val reqs = List(requestCount) { withService(it, service, executor) }
        reqs.forEach { it.get() }
    }

    println("Total time with executor: $d Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")

    exitProcess(0)
}