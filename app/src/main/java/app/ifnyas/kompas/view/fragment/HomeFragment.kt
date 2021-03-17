package app.ifnyas.kompas.view.fragment

import android.os.Bundle
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import app.ifnyas.kompas.App.Companion.fu
import app.ifnyas.kompas.R
import app.ifnyas.kompas.databinding.DialogMoreBinding
import app.ifnyas.kompas.databinding.FragmentHomeBinding
import app.ifnyas.kompas.model.Article
import app.ifnyas.kompas.model.ArticleViewHolder
import app.ifnyas.kompas.model.HeadlineViewHolder
import app.ifnyas.kompas.viewmodel.HomeViewModel
import app.ifnyas.kompas.viewmodel.MainViewModel
import coil.load
import coil.transform.RoundedCornersTransformation
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG: String by lazy { javaClass.simpleName }
    private val avm: MainViewModel by activityViewModels()
    private val vm: HomeViewModel by viewModels()
    private val bind: FragmentHomeBinding by viewBinding()

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)
        initFun()
    }

    private fun initFun() {
        initBtn()
        initData()
    }

    private fun initBtn() {
        bind.btnProfile.setOnClickListener { createProfileDialog() }
        bind.btnMore.setOnClickListener { createMoreDialog() }
    }

    private fun initData() {
        if (!vm.isLoggedIn()) createProfileDialog() else {

            lifecycleScope.launch {
                // start progress
                setProgressView(true)

                // on progress
                kotlin.runCatching { if (!vm.hasArticles()) vm.getProfile() }
                        .onSuccess { initView() }
                        .onFailure { fu.exHandler(it) }

                // end progress
                setProgressView(false)
            }
        }
    }

    private fun initView() {
        initArticles()
        initSwipeRefresh()
        initProfileView()
        initHeadlines()
        initMore()
        initScrollPosition()
    }

    private fun initScrollPosition() {
        bind.layScroll.apply {
            scrollBy(0, vm.scrollPosition.value)
            setOnScrollChangeListener { _, _, scrollY, _, _ ->
                val lastChild = getChildAt(childCount - 1)
                val end = lastChild.bottom - (height + scrollY)
                if (end == 0) {
                    vm.loadMoreArticleList()
                    initMore()
                }
            }
        }
    }

    private fun initMore() {
        bind.pbArticles.visibility = if (vm.noMore()) GONE else VISIBLE
        bind.textStaticEnd.visibility = if (vm.noMore()) VISIBLE else GONE
    }

    private fun initSwipeRefresh() {
        bind.root.setOnRefreshListener { initData() }
    }

    private fun initProfileView() {
        bind.textNameProfile.text = vm.nameProfile.value
        bind.textChannelProfile.text = vm.channelProfile.value
        bind.textArticlesProfile.text = "${vm.articlesProfile.value}"
        bind.textSinceProfile.text = vm.sinceProfile.value
    }

    private fun initHeadlines() {
        // get list
        val source = dataSourceTypedOf(vm.getHeadlines())

        // set empty view
        bind.textStaticTitleArticles.visibility = if (source.isEmpty()) GONE else VISIBLE
        bind.textStaticDescArticles.visibility = if (source.isEmpty()) GONE else VISIBLE

        // setup rv
        if (source.isNotEmpty()) bind.rvHeadlines.setup {
            withDataSource(source)
            withItem<Article, HeadlineViewHolder>(R.layout.item_article_headlines) {
                // bind
                onBind(::HeadlineViewHolder) { _, item ->
                    title.text = item.title
                    date.text = fu.formatDateArticle(item.date)
                    image.load(item.image) {
                        crossfade(true)
                        transformations(RoundedCornersTransformation(16f))
                    }
                }

                // click
                onClick { navToArticleFragment(item) }
                onLongClick { fu.share(item) }
            }
        }
    }

    private fun initArticles() {
        // get list
        val source = vm.articleList

        // setup rv
        if (source.isNotEmpty()) bind.rvArticles.setup {
            withDataSource(source)
            withItem<Article, ArticleViewHolder>(R.layout.item_article_list) {
                // bind
                onBind(::ArticleViewHolder) { _, item ->
                    title.text = item.title
                    date.text = fu.formatArticleSub(item)
                    image.load(item.image) {
                        crossfade(true)
                        transformations(RoundedCornersTransformation(16f))
                    }
                }

                // click
                onClick { navToArticleFragment(item) }
                onLongClick { fu.share(item) }
            }
        }
    }

    private fun setSearchList(rv: RecyclerView, page: Int, limit: Int) {
        // get list
        val source = dataSourceTypedOf(vm.getSearch(page, limit))

        // setup rv
        if (source.isNotEmpty()) rv.setup {
            withDataSource(source)
            withItem<Article, ArticleViewHolder>(R.layout.item_article_list) {
                // bind
                onBind(::ArticleViewHolder) { _, item ->
                    title.text = item.title
                    date.text = fu.formatArticleSub(item)
                    image.load(item.image) {
                        crossfade(true)
                        transformations(RoundedCornersTransformation(16f))
                    }
                }

                // click
                onClick { navToArticleFragment(item) }
                onLongClick { fu.share(item) }
            }
        }
    }

    private fun createProfileDialog() {
        MaterialDialog(requireContext()).show {
            lifecycleOwner(viewLifecycleOwner)
            cornerRadius(8f)
            title(text = "Profil Penulis")
            message(text = "Tulis nama penulis yang Anda cari. " +
                    "Hasil akan lebih baik jika nama sesuai dengan Indeks Profil Kompas."
            )
            input(hint = "Tulis nama di sini...", prefill = vm.nameProfile.value) { _, input ->
                vm.setNameProfile("$input"); initData()
            }
            getInputField().apply {
                gravity = CENTER
                post { selectAll() }
                setBackgroundColor(resources.getColor(
                        android.R.color.transparent, null
                ))
            }
            negativeButton(text = "Kembali")
            positiveButton(text = "Simpan")
        }
    }

    private fun createMoreDialog() { //TODO strange behavior
        MaterialDialog(requireContext(), BottomSheet()).show {
            // init val
            val binding = DialogMoreBinding.inflate(
                    LayoutInflater.from(requireContext())
            )

            // init fun
            fun setSearch(isReset: Boolean, input: String) {
                val size = if (isReset) vm.articleBank.size else vm.articleSearch.size
                if (isReset) vm.initSearch() else vm.setSearch(input)
                setSearchList(binding.rvSearch, 1, 10)
                binding.laySearch.helperText = "Artikel ditemukan: $size"
            }

            // set dialog
            lifecycleOwner(viewLifecycleOwner)
            customView(view = binding.root)
            cancelable(false)

            // set view
            binding.apply {
                lifecycleScope.launch {
                    root.isEnabled = false
                    setSearch(true, "")
                    btnBack.setOnClickListener { dismiss() }
                    editSearch.apply {
                        setOnEditorActionListener { v, actionId, _ ->
                            if (actionId != EditorInfo.IME_ACTION_DONE) false
                            else {
                                v.clearFocus()
                                fu.clearKeyboard(v)
                                true
                            }
                        }

                        doAfterTextChanged {
                            when {
                                "$it".length > 2 -> {
                                    setSearch(false, "$it")
                                }
                                "$it".length == 0 -> {
                                    vm.initSearch()
                                    setSearchList(rvSearch, 1, 10)
                                    laySearch.helperText = "Artikel ditemukan: ${vm.articleBank.size}"
                                }
                            }
                        }
                    }

                    delay(100)
                    setPeekHeight(R.layout.dialog_more)
                }
            }
        }
    }

    private fun navToArticleFragment(item: Article) {
        val jsonItem = Json.encodeToString(item)
        val dir = HomeFragmentDirections.actionHomeFragmentToArticleFragment(jsonItem)
        findNavController().navigate(dir)
    }

    private fun setProgressView(isLoading: Boolean) {
        fu.beginTransition(bind.root)
        bind.root.isRefreshing = isLoading
        bind.layParent.visibility = if (isLoading) INVISIBLE else VISIBLE
    }

    override fun onPause() {
        super.onPause()
        vm.scrollPosition.value = bind.layScroll.scrollY
    }
}