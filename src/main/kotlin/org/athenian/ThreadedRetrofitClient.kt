package org.athenian

import org.athenian.Config.okHttpClient
import org.athenian.Config.requestCount
import kotlin.concurrent.thread
import kotlin.time.measureTime

fun main() {
  val dur1 = measureTime {
    val service = Config.retrofit.create(DelayedService::class.java)
    (1..requestCount)
      .map { id ->
        thread {
          log("Launching request $id with thread")
          val dur2 = measureTime { service.withBlock().execute().body() }
          log("Blocking request $id time: $dur2")
        }
      }
      .forEach { t -> t.join() }
  }
  println("Total time: $dur1 Pool size: ${okHttpClient.connectionPool().connectionCount()}\n")
}