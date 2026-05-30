package com.wasted.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("wasted_prefs")

class WastedPrefs(private val context: Context) {
    companion object {
        private val KEY_TRACKED   = stringSetPreferencesKey("tracked_packages")
        private val KEY_NAMES     = stringPreferencesKey("display_names_json")
        private val KEY_ONBOARDED = booleanPreferencesKey("onboarded")
    }

    val trackedPackages: Flow<Set<String>> = context.dataStore.data
        .map { it[KEY_TRACKED] ?: emptySet() }

    val displayNames: Flow<Map<String, String>> = context.dataStore.data
        .map { pref ->
            pref[KEY_NAMES]?.let { Json.decodeFromString<Map<String, String>>(it) } ?: emptyMap()
        }

    val isOnboarded: Flow<Boolean> = context.dataStore.data
        .map { it[KEY_ONBOARDED] ?: false }

    suspend fun setTrackedPackages(packages: Set<String>, names: Map<String, String>) {
        context.dataStore.edit {
            it[KEY_TRACKED] = packages
            it[KEY_NAMES] = Json.encodeToString(names)
        }
    }

    suspend fun setOnboarded() {
        context.dataStore.edit { it[KEY_ONBOARDED] = true }
    }
}
