package org.athenian

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.header
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import kotlin.time.seconds

fun main() {
  val httpServer =
    embeddedServer(CIO, port = 8080) {
      routing {
        get("/") {
          call.respondText("index.html requested", ContentType.Text.Plain)
        }
        get("/delayed") {
          call.apply {
            response.header("cache-control", "must-revalidate,no-cache,no-store")
            response.status(HttpStatusCode.OK)
            log("Waiting 2 seconds")
            delay(2.seconds.toLongMilliseconds())
            val map = mapOf("Field1" to "val1", "Field2" to "val2")
            val json = Json.stringify(map)
            respondText(json, ContentType.Application.Json)
          }
        }
      }
    }

  httpServer.start(true)
}

fun log(msg: String = "") = println("[${Thread.currentThread().name}] $msg")