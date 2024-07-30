package com.stopstone.myapplication.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.stopstone.myapplication.R
import com.stopstone.myapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationHome.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearDataStore()
    }

    private fun clearDataStore() = runBlocking {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}