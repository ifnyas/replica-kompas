package app.ifnyas.kompas.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.transition.TransitionManager
import app.ifnyas.kompas.App.Companion.cxt
import app.ifnyas.kompas.model.Article
import java.text.SimpleDateFormat
import java.util.*

class FunUtils {
    fun exHandler(e: Throwable) {
        e.printStackTrace()
        AlertDialog.Builder(cxt)
                .setTitle("Exception")
                .setMessage(e.message)
                .show()
    }

    fun clearKeyboard(view: View) {
        val imm = cxt.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun beginTransition(view: ViewGroup) {
        TransitionManager.beginDelayedTransition(view)
    }

    fun formatDateProfile(date: String): String {
        val iPattern = "yyyy-MM-dd HH:mm:ss"
        val oPattern = "MMM yyyy"
        val input = SimpleDateFormat(iPattern, Locale.ROOT).parse(date) ?: Date()
        return SimpleDateFormat(oPattern, Locale.ROOT).format(input)
    }

    fun formatDateArticle(date: String): String {
        val iPattern = "yyyy-MM-dd HH:mm:ss"
        val oPattern = "d MMM yyyy"
        val input = SimpleDateFormat(iPattern, Locale.ROOT).parse(date) ?: Date()
        return SimpleDateFormat(oPattern, Locale.ROOT).format(input)
    }

    fun formatArticleSub(item: Article): String {
        return "${item.channel}   ○   ${formatDateArticle(item.date)}"
    }

    fun capitalizeName(name: String): String {
        return name.split(" ").joinToString(" ") {
            it.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
        }
    }

    fun share(item: Article) {
        // text
        val text = "*${item.title}*\nKlik untuk baca: ${item.link}"

        // intent
        val intent = Intent(Intent.ACTION_SEND).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        // start
        startActivity(cxt, Intent.createChooser(intent, "Bagikan kabar via: "), null)
    }

    fun web(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
        startActivity(cxt, intent, null)
    }
}