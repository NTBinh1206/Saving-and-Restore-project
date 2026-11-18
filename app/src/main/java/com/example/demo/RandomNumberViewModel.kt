package com.example.demo

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

// E: tạo một file DataStore tên "state_lab" trong bộ nhớ máy (khai báo ở ngoài class).
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "state_lab")

class RandomNumberViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "StateLab"
        private const val KEY_SAVED_NUMBER = "saved_number"
        private val KEY_PERSISTED_NUMBER = intPreferencesKey("persisted_number")
    }

    init {
        // Log để chứng minh: xoay màn hình KHÔNG tạo lại ViewModel,
        // còn khi process bị kill thì ViewModel được tạo mới.
        Log.d(TAG, "ViewModel CREATED")
    }

    // C: lưu trong SavedStateHandle -> sống qua xoay màn hình VÀ process death.
    val savedNumber: StateFlow<Int> =
        savedStateHandle.getStateFlow(KEY_SAVED_NUMBER, 0)

    fun generateSavedNumber() {
        savedStateHandle[KEY_SAVED_NUMBER] = Random.nextInt(1, 1000)
    }

    // D: chỉ nằm trong bộ nhớ của ViewModel -> sống qua xoay màn hình,
    // nhưng MẤT khi process bị kill.
    private val _plainNumber = MutableStateFlow(0)
    val plainNumber: StateFlow<Int> = _plainNumber.asStateFlow()

    fun generatePlainNumber() {
        _plainNumber.value = Random.nextInt(1, 1000)
    }

    // E: lưu vào DataStore (file trong bộ nhớ máy) -> còn cả khi tắt app rồi mở lại.
    private val dataStore = application.dataStore

    val persistedNumber: StateFlow<Int> = dataStore.data
        .map { preferences -> preferences[KEY_PERSISTED_NUMBER] ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun generatePersistedNumber() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[KEY_PERSISTED_NUMBER] = Random.nextInt(1, 1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel CLEARED")
    }
}