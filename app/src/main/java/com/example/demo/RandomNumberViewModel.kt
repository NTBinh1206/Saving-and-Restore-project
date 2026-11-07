package com.example.demo

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class RandomNumberViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "StateLab"
        private const val KEY_SAVED_NUMBER = "saved_number"
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

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel CLEARED")
    }
}