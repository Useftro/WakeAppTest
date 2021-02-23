package com.uniolco.wakeapptest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {

    private lateinit var dataStore: DataStore<Preferences>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        dataStore = createDataStore(name = "settings")
        update()
    }

    private fun goToMainMenu() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun waitAtSplashScreen(millisecs: Long) {
        Handler().postDelayed(
            {
                goToMainMenu()
                finish()
            },
            millisecs
        )
    }

    private suspend fun save(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    private suspend fun read(key: String): Boolean? {
        val dataStoreKey = booleanPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    fun update() {
        lifecycleScope.launch {
            val firstTimeEnteringApp = read("firstTimeEnteringApp")
            if(firstTimeEnteringApp != false){
                save("firstTimeEnteringApp", false)
                waitAtSplashScreen(2500)
            }
            else{
                waitAtSplashScreen(1000)
            }
        }
    }
}