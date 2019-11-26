package org.athenian

import org.athenian.Config.okHttpClient
import org.athenian.Config.requestCount
import org.athenian.Config.threadCount
import java.util.concurrent.Executors
import kotlin.time.measureTimedValue

fun main() {
  val service = Config.retrofit.create(DelayedService::class.java)
  val executor = Executors.newFixedThreadPool(threadCount)
  val (_, dur) =
    measureTimedValue {
      (1..requestCount)
        .map { id ->
          executor.submit {
            log("Launching blocking request $id with executor")
            service.withBlock().execute().body()
          }
        }
        .onEach { future -> future.get() }
    }

  println("Total time: $dur Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")

  executor.shutdown()
}