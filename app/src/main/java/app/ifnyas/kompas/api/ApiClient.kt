package app.ifnyas.kompas.api

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

object ApiClient {
    val httpClient: HttpClient by lazy {
        HttpClient(Android) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                )

                engine {
                    connectTimeout = 60_000
                    socketTimeout = 60_000
                }
            }

            install(Logging) {
//                logger = object : Logger {
//                    override fun log(message: String) {
//                        Log.v("Logger Ktor =>", message)
//                    }
//                }
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }

            defaultRequest {
                host = "api.kompas.com" // dev
                url { protocol = URLProtocol.HTTPS }
                headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
                headers.append(HttpHeaders.Authorization, "")
            }
        }
    }
}