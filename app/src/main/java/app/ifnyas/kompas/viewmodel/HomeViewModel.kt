package app.ifnyas.kompas.viewmodel

import androidx.lifecycle.ViewModel
import app.ifnyas.kompas.App.Companion.ar
import app.ifnyas.kompas.App.Companion.fu
import app.ifnyas.kompas.App.Companion.su
import app.ifnyas.kompas.model.Article
import app.ifnyas.kompas.util.SessionUtils.Companion.fullName_STR
import com.afollestad.recyclical.datasource.emptyDataSourceTyped
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.*

class HomeViewModel : ViewModel() {

    private val TAG by lazy { javaClass.simpleName }
    private val limit by lazy { 100 }

    val nameProfile by lazy { MutableStateFlow("") }
    val channelProfile by lazy { MutableStateFlow("") }
    val sinceProfile by lazy { MutableStateFlow("") }
    val articlesProfile by lazy { MutableStateFlow(0) }
    val scrollPosition by lazy { MutableStateFlow(0) }
    val articleBank by lazy { mutableListOf<Article>() }
    val articleList = emptyDataSourceTyped<Article>()

    val articleSearch by lazy { mutableListOf<Article>() }
    val searchText by lazy { MutableStateFlow("") }
    val searchStartDate by lazy { MutableStateFlow("") }
    val searchEndDate by lazy { MutableStateFlow("") }

    suspend fun getProfile() {
        // init val
        var page = 0
        nameProfile.value = su.get(fullName_STR).toString()

        // empty list
        articleBank.clear()

        // send request
        while (isFindNext(page)) {
            page++
            getProfileRequest(page)
        }

        // set data
        articleBank.take(10).drop(5).forEach { articleList.add(it) }
        channelProfile.value = articleBank[0].channel
        sinceProfile.value = fu.formatDateProfile(
                articleBank[articleBank.lastIndex].date
        )
    }

    private suspend fun getProfileRequest(page: Int) {
        // send request
        val req = ar.getProfile(nameProfile.value, page, limit)
        if (req.status.value == 200) {
            // parse json
            val res = Json.parseToJsonElement(req.readText()).jsonObject
            val xml = res["xml"]?.jsonObject
            val pencarian = xml?.get("pencarian")?.jsonObject
            val items = pencarian?.get("item")?.jsonArray

            // iterate items
            items?.forEach {
                articleBank.add(
                        Article(
                                getElement(it, "guid"),
                                getElement(it, "title"),
                                getElement(it, "description"),
                                getElement(it, "kanal"),
                                getElement(it, "pubDate"),
                                getElement(it, "photo"),
                                getElement(it, "link")
                        )
                )
            }

            // update size
            articlesProfile.value = articleBank.size
        }
    }

    fun getHeadlines(): List<Article> {
        return articleBank.toList().take(5)
    }

    private fun getElement(it: JsonElement, value: String): String {
        return if (value != "photo") {
            it.jsonObject[value]?.jsonPrimitive?.contentOrNull ?: ""
        } else {
            val element = it.jsonObject[value]?.jsonPrimitive?.contentOrNull ?: ""
            try {
                val delCrops = element.split("/data")
                "https://asset.kompas.com/data${delCrops[delCrops.lastIndex]}"
            } catch (e: Exception) {
                element
            }
        }
    }

    private fun isFindNext(page: Int): Boolean {
        return if (page == 0) true else articlesProfile.value % (page * limit) == 0
    }

    fun setNameProfile(name: String) {
        nameProfile.value = fu.capitalizeName(name)
        su.set(fullName_STR, nameProfile.value)
    }

    fun isLoggedIn(): Boolean {
        return su.get(fullName_STR).toString().isNotBlank()
    }

    fun hasArticles(): Boolean {
        return articleBank.isNotEmpty()
    }

    fun logout() {
        su.set(fullName_STR, "")
        articleBank.clear()
    }

    fun setSearch(search: String) {
        articleSearch.clear()
        articleBank.forEach {
            val isContains = it.title.contains(search, true)
            if (isContains) articleSearch.add(it)
        }
    }

    fun getSearch(page: Int, limit: Int): List<Article> {
        return if (articleSearch.isEmpty()) emptyList() else {
            val fromIndex = (page * limit) - limit
            val toIndex =
                    if ((limit - 1) < articleSearch.lastIndex) limit - 1
                    else articleSearch.lastIndex
            articleSearch.subList(fromIndex, toIndex)
        }
    }

    fun loadMoreArticleList() {
        val page = (articleList.size() / 10) + 1
        val limit = 10
        val fromIndex = (page * limit) - limit
        val toIndex =
                if ((limit - 1) < articleBank.lastIndex) (page * limit) - 1
                else articleBank.lastIndex

        if (fromIndex < articleBank.lastIndex) {
            articleBank.subList(fromIndex, toIndex).forEach { articleList.add(it) }
        }
    }

    fun initSearch() {
        articleSearch.clear()
        articleBank.take(10).forEach(articleSearch::add)
    }

    fun noMore(): Boolean {
        return articleList.size() == articleBank.size
    }
}