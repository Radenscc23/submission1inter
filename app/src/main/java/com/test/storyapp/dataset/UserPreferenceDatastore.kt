package com.test.storyapp.dataset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("User")

class UserPreferenceDatastore private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken(): Flow<String> = dataStore.data.map { it[key1] ?: "Tidak diatur" }
    companion object {
        @Volatile
        private var userPreferenceDatastore: UserPreferenceDatastore? = null
        private val stringKey = stringPreferencesKey("name")
        private val key = stringPreferencesKey("userId")
        private val key1 = stringPreferencesKey("token")

        fun instance(dataStore: DataStore<Preferences>): UserPreferenceDatastore {
            return userPreferenceDatastore ?: synchronized(this) {
                val instance = UserPreferenceDatastore(dataStore)
                userPreferenceDatastore = instance
                instance
            }
        }
    }

    suspend fun saveInfoUser(name: String, userId: String, token: String) {
        dataStore.edit { preferences ->
            preferences[stringKey] = name
            preferences[key] = userId
            preferences[key1] = token
        }
    }

    fun getUserInfo(): Flow<LoginResult> {
        return dataStore.data.map { preferences ->
            LoginResult(
                preferences[stringKey] ?:"",
                preferences[key] ?:"",
                preferences[key1] ?:"",
            )
        }
    }

    suspend fun userSignOut() {
        dataStore.edit { preferences ->
            preferences[stringKey] = ""
            preferences[key] = ""
            preferences[key1] = ""
        }
    }


}