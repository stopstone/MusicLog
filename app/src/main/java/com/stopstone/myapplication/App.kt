package com.stopstone.myapplication

import android.app.Application
import com.stopstone.myapplication.data.local.TokenManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    // 테스트를 위해 앱을 실행할때 DataStore클리어
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate() {
        super.onCreate()
        clearAllData()
    }

    private fun clearAllData() = runBlocking {
        tokenManager.clearAll()
    }
}