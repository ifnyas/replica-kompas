package app.ifnyas.kompas.view

import android.os.Bundle
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.ifnyas.kompas.App.Companion.cxt
import app.ifnyas.kompas.databinding.ActivityMainBinding
import app.ifnyas.kompas.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private val bind: ActivityMainBinding by viewBinding()
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        initFun()
    }

    private fun initFun() {
        cxt = this
    }
}