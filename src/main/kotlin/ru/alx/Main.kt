package ru.alx

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.system.exitProcess

val port = System.getenv("PORT")?.toInt() ?: 8080
val TOKEN: String? = System.getenv("TOKEN")
val TELEGRAM_URL = System.getenv("TELEGRAM_URL") ?: "https://api.telegram.org/bot$TOKEN"
val BASE_URL = System.getenv("APP_URL") ?: "https://d298-178-66-158-184.eu.ngrok.io"
var isRunning = true
//val JDBC = System.getenv("JDBC_DATABASE_URL")
//JDBC_DATABASE_USERNAME

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        jackson {
            configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}

suspend fun main() {
//    get("deleteWebhook")

//    val response = client.get("$TELEGRAM_URL/getUpdates")
//    println(response.body() as String)

    if (TOKEN == null) {
        System.err.println("TOKEN is empty!")
//        exitProcess(-1)
    }

    setWebhook()

    get("getWebhookInfo")

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
        configureRouting()
    }.start(wait = true)
}

suspend fun get(method:String) {
    val response = client.get("$TELEGRAM_URL/$method")
    println("get $method: ${response.status} ${response.bodyAsText()}")
}

suspend fun setWebhook() {
    val response = client.post("$TELEGRAM_URL/setWebhook") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("url" to BASE_URL))
    }

    println("Set hook: ${response.status} ${response.bodyAsText()}")
}

val users = mutableMapOf<String, User>()

var defaultMsgIdx = 0
val defaultMessages = listOf(
    "Что что?", "Я не понимаю", "А?", "Хм, попробуй еще раз",
)

suspend fun sendDefaultMessage(chatId: String): String {
    sendMessage(OutTextMessage(
        chat_id = chatId,
        text = defaultMessages[defaultMsgIdx]
    ))

    defaultMsgIdx += 1
    if (defaultMsgIdx >= defaultMessages.size) {
        defaultMsgIdx = 0
    }

    return ""
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText { "Hello world" }
        }
        get("/stop") {
            isRunning = false
        }
        get("/start") {
            isRunning = true
        }
        static("assets") {
            staticBasePackage = "assets"
            resources(".")
        }
        post("/") {
            try {
                if (!isRunning) return@post
//                val text = call.receiveText()
//                println(text)
//                val mapper = ObjectMapper().registerModule(KotlinModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                val action: Action = mapper.readValue(text, Action::class.java)

                val action: Action = call.receive()
                val chatId = action.message?.chat?.id!!
                val user = users.computeIfAbsent(action.message.from?.username!!) { username ->
                    User(
                        chatId = chatId,
                        username = username,
                        firstName = action.message.from.first_name
                    )
                }
                val botctx = Context(chatId, user)

                println(action)

                var nextStateName: String? = user.currentState.nextState(action, botctx)

                while (nextStateName?.isNotEmpty() == true) {
                    val nextState = states[nextStateName]!!
                    nextStateName = nextState.action(botctx)
                    user.currentState = nextState
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

val photos = mutableMapOf<String, String>()
fun getPhoto(name: String): String {
    val url = "$BASE_URL/assets/$name"
    return photos.getOrDefault(url, url)
}

suspend fun sendPhoto(message: OutPhotoMessage) {
    val response = client.post("$TELEGRAM_URL/sendPhoto") {
        contentType(ContentType.Application.Json)
        setBody(message)
    }

    response.body<Message>().photo?.last()?.let {
        photos.put(message.photo, it.file_id)
    }
}

suspend fun sendDice(chatId: String) {
    client.post("$TELEGRAM_URL/sendDice") {
        contentType(ContentType.Application.Json)
        setBody(OutTextMessage(
            chat_id = chatId,
            emoji =  "\uD83C\uDFB2",
            reply_markup = RemoveKeyboard()
        ))
    }
}

suspend fun sendMessage(message: OutTextMessage) {
    client.post("$TELEGRAM_URL/sendMessage") {
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}