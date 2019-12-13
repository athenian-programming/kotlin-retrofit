package org.athenian

import io.ktor.client.HttpClient
import io.ktor.client.call.call
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.Config.okHttpClient
import org.athenian.Config.requestCount
import org.athenian.Config.threadCount
import java.util.concurrent.Executors
import kotlin.time.measureTime

fun main() {
  Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()
    .use { dispatcher ->
      val client = HttpClient()
      val dur1 = measureTime {
        runBlocking {
          (1..requestCount)
            .map { id ->
              launch(dispatcher) {
                log("Launching suspending ktor request $id")
                val dur2 = measureTime { client.call("http://localhost:8080/delayed") }
                log("Suspending request $id time: $dur2")
              }
            }
            .joinAll()
        }
      }
      println("Total time: $dur1 Pool size: ${okHttpClient.connectionPool().connectionCount()}")
    }
}