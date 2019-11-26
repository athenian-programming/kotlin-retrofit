package org.athenian

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.athenian.Config.okHttpClient
import org.athenian.Config.requestCount
import org.athenian.Config.threadCount
import java.util.concurrent.Executors
import kotlin.time.measureTimedValue


fun main() {
  Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()
    .use { dispatcher ->
      val service = Config.retrofit.create(DelayedService::class.java)
      val (_, dur) =
        measureTimedValue {
          runBlocking {
            (1..requestCount)
              .map { id ->
                launch(dispatcher) {
                  log("Launching blocking request $id")
                  service.withBlock().execute().body()
                }
              }
              .joinAll()
          }
        }
      println("Total time: $dur Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")
    }
}