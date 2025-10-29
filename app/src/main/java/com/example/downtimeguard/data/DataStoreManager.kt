package com.example.downtimeguard.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.downtimeguard.data.model.BlockRule
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "app_prefs")
private val RULE_ENABLED = booleanPreferencesKey("rule_enabled")
private val RULE_PACKAGES = stringSetPreferencesKey("rule_packages")
private val UNLOCK_UNTIL = longPreferencesKey("unlock_until")
private val UNLOCK_DURATION_MIN = intPreferencesKey("unlock_duration_min")
private val EMERG_USED = intPreferencesKey("emerg_used")          // how many used this month
private val EMERG_MONTH = stringPreferencesKey("emerg_month")

class DataStoreManager(private val context: Context) {
    val ruleFlow = context.dataStore.data.map { prefs ->
        BlockRule(
            enabled = prefs[RULE_ENABLED] ?: false,
            packages = prefs[RULE_PACKAGES] ?: emptySet(),
            unlockUntil = prefs[UNLOCK_UNTIL],
            unlockDurationMin = prefs[UNLOCK_DURATION_MIN] ?: 15,
            emergencyUsed = prefs[EMERG_USED] ?: 0,
            emergencyMonth = prefs[EMERG_MONTH]
        )
    }

    private val SELECTED_APPS = stringSetPreferencesKey("selected_apps")
    val selectedAppsFlow = context.dataStore.data.map { prefs ->
        prefs[SELECTED_APPS] ?: emptySet()
    }

    suspend fun toggleAppSelection(packageName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[SELECTED_APPS] ?: emptySet()
            prefs[SELECTED_APPS] = if (current.contains(packageName))
                current - packageName
            else
                current + packageName
        }
    }

    suspend fun setAppSelection(packageName: String, selected: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[SELECTED_APPS] ?: emptySet()
            prefs[SELECTED_APPS] = if (selected)
                current + packageName
            else
                current - packageName
        }
    }


    suspend fun saveRule(rule: BlockRule) {
        context.dataStore.edit { p ->
            p[RULE_ENABLED] = rule.enabled
            p[RULE_PACKAGES] = rule.packages
            p[UNLOCK_DURATION_MIN] = rule.unlockDurationMin
//            p[EMERG_USED] = rule.emergencyUsed
//            p[EMERG_MONTH]= rule.emergencyMonth as String


            // handle nullable Long properly:
            if (rule.unlockUntil == null) {
                p.remove(UNLOCK_UNTIL)
            } else {
                p[UNLOCK_UNTIL] = rule.unlockUntil
            }

            // unlockUntil is Long?
            if (rule.unlockUntil == null) p.remove(UNLOCK_UNTIL)
            else p[UNLOCK_UNTIL] = rule.unlockUntil

            // emergencyMonth is String?
            if (rule.emergencyMonth == null) p.remove(EMERG_MONTH)
            else p[EMERG_MONTH] = rule.emergencyMonth

            // emergencyUsed is Int (non-null), safe to set directly
            p[EMERG_USED] = rule.emergencyUsed

        }
    }

    suspend fun setTemporaryUnlock(minutes: Int) {
        val until = System.currentTimeMillis() + minutes * 60_000L
        context.dataStore.edit { it[UNLOCK_UNTIL] = until }
    }
}