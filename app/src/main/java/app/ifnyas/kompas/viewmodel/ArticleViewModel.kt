package app.ifnyas.kompas.viewmodel

import androidx.lifecycle.ViewModel
import app.ifnyas.kompas.App.Companion.ar
import app.ifnyas.kompas.model.Article
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

class ArticleViewModel : ViewModel() {

    private val TAG by lazy { javaClass.simpleName }
    private val article by lazy { MutableStateFlow(Article()) }

    fun getArticle(): Article {
        return article.value
    }

    fun decodeArticle(json: String) {
        article.value = Json.decodeFromString(json)
    }

    suspend fun getBody(guid: String) {
        // send request
        val req = ar.getArticle(guid)
        if (req.status.value == 200) {
            // parse json
            val res = Json.parseToJsonElement(req.readText()).jsonObject
            val result = res["result"]?.jsonObject
            val contents = result?.get("content")?.jsonArray
            var body = ""
            contents?.forEach {
                val content = it.jsonPrimitive.contentOrNull
                body += "$content"
            }
            article.value.body = body
        }
    }
}