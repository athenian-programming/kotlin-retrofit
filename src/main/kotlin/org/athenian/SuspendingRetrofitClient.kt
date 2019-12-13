package org.athenian

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.Config.okHttpClient
import org.athenian.Config.requestCount
import org.athenian.Config.threadCount
import java.util.concurrent.Executors
import kotlin.system.exitProcess
import kotlin.time.measureTime

fun main() {
  Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()
    .use { dispatcher ->
      val service = Config.retrofit.create(DelayedService::class.java)
      val dur1 = measureTime {
        runBlocking {
          (1..requestCount)
            .map { id ->
              launch(dispatcher) {
                log("Launching suspending request $id")
                val dur2 = measureTime { service.withSuspend() }
                log("Suspending request $id time: $dur2")
              }
            }
            .joinAll()
        }
      }
      println("Total time with suspending: $dur1 Pool size: ${okHttpClient.connectionPool().connectionCount()}")
    }

  exitProcess(0)
}