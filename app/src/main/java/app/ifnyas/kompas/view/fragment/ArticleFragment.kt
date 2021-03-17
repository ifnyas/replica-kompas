package app.ifnyas.kompas.view.fragment

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import app.ifnyas.kompas.App.Companion.fu
import app.ifnyas.kompas.R
import app.ifnyas.kompas.databinding.FragmentArticleBinding
import app.ifnyas.kompas.viewmodel.ArticleViewModel
import app.ifnyas.kompas.viewmodel.MainViewModel
import coil.load
import coil.transform.RoundedCornersTransformation
import kotlinx.coroutines.launch
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val avm: MainViewModel by activityViewModels()
    private val vm: ArticleViewModel by viewModels()
    private val bind: FragmentArticleBinding by viewBinding()
    private val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        initBtn()
        initData()
    }

    private fun initBtn() {
        bind.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        bind.btnShare.setOnClickListener {
            fu.share(vm.getArticle())
        }
        bind.btnWeb.setOnClickListener {
            fu.web(vm.getArticle().link)
        }
    }

    private fun initData() {
        lifecycleScope.launch {
            // start progress
            setProgressView(true)

            // on progress
            vm.decodeArticle(args.item)
            kotlin.runCatching { vm.getBody(vm.getArticle().guid) }
                    .onSuccess { initView() }
                    .onFailure { fu.exHandler(it) }

            // end progress
            setProgressView(false)
        }
    }

    private fun initView() {
        bind.apply {
            // text
            textTitle.text = vm.getArticle().title
            textDate.text = fu.formatArticleSub(vm.getArticle())
            textBody.setHtml(vm.getArticle().body, HtmlHttpImageGetter(textBody))

            // image
            imgArticle.load(vm.getArticle().image) {
                crossfade(true)
                transformations(RoundedCornersTransformation(16f))
            }
        }
    }

    private fun setProgressView(isLoading: Boolean) {
        fu.beginTransition(bind.root)
        bind.root.isRefreshing = isLoading
        bind.layParent.visibility = if (isLoading) INVISIBLE else VISIBLE
    }
}