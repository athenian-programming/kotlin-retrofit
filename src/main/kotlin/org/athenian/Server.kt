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
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.delay
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
@KtorExperimentalAPI
@ImplicitReflectionSerializer
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
                        log("Waiting 1 second")
                        delay(1.seconds.toLongMilliseconds())
                        respondText(
                            Json.stringify(
                                mapOf(
                                    "Field1" to "val1",
                                    "Field2" to "val2"
                                )
                            ), ContentType.Application.Json
                        )
                    }
                }
            }
        }


    httpServer.start(true)
}

fun log(msg: String = "") = println("[${Thread.currentThread().name}] $msg")

