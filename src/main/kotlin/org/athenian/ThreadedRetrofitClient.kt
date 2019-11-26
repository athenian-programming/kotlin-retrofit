package org.athenian

import org.athenian.Config.okHttpClient
import org.athenian.Config.requestCount
import kotlin.concurrent.thread
import kotlin.time.measureTimedValue

fun main() {
  val (_, dur) =
    measureTimedValue {
      val service = Config.retrofit.create(DelayedService::class.java)
      (1..requestCount)
        .map { id ->
          thread {
            log("Launching request $id with thread")
            service.withBlock().execute().body()
          }
        }
        .forEach { t -> t.join() }
    }
  println("Total time: $dur Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")
}