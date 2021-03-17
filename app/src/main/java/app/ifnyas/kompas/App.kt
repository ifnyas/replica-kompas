package app.ifnyas.kompas

import android.app.Application
import android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import app.ifnyas.kompas.api.ApiRequest
import app.ifnyas.kompas.util.FunUtils
import app.ifnyas.kompas.util.SessionUtils
import app.ifnyas.kompas.view.MainActivity

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initFun()
    }

    private fun initFun() {
        // init analytics
        //FirebaseAnalytics.getInstance(this)

        // init day/night mode
        setDefaultNightMode(MODE_NIGHT_YES)

        // init debug
        if (applicationInfo.flags and FLAG_DEBUGGABLE != 0) initDebug()
    }

    private fun initDebug() {
        //
    }

    companion object {
        lateinit var cxt: MainActivity
        val ar by lazy { ApiRequest() }
        val fu by lazy { FunUtils() }
        val su by lazy { SessionUtils() }
        //val db: Database by lazy { Database(sqlDriver) }
    }
}