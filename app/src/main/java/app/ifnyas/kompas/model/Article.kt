package app.ifnyas.kompas.model

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import app.ifnyas.kompas.R
import com.afollestad.recyclical.ViewHolder
import kotlinx.serialization.Serializable

@Serializable
data class Article(
        var guid: String = "",
        var title: String = "",
        var desc: String = "",
        var channel: String = "",
        var date: String = "",
        var image: String = "",
        var link: String = "",
        var body: String = ""
)

class HeadlineViewHolder(itemView: View) : ViewHolder(itemView) {
    val image: AppCompatImageView = itemView.findViewById(R.id.img_thumb_headline)
    val title: AppCompatTextView = itemView.findViewById(R.id.text_title_headline)
    val date: AppCompatTextView = itemView.findViewById(R.id.text_date_headline)
}

class ArticleViewHolder(itemView: View) : ViewHolder(itemView) {
    val image: AppCompatImageView = itemView.findViewById(R.id.img_thumb_article)
    val title: AppCompatTextView = itemView.findViewById(R.id.text_title_article)
    val date: AppCompatTextView = itemView.findViewById(R.id.text_date_article)
}