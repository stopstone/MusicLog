package com.stopstone.musicplaylist.ui.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.musicplaylist.domain.usecase.setting.DeleteAllTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val deleteAllTracksUseCase: DeleteAllTracksUseCase
) : ViewModel() {
    private val _clearDataResult = MutableSharedFlow<Boolean>()
    val clearDataResult = _clearDataResult.asSharedFlow()

    fun clearData() = viewModelScope.launch {
        runCatching {
            deleteAllTracksUseCase()
        }.onSuccess {
            _clearDataResult.emit(true)
            Log.d("SettingViewModel", "Data cleared successfully")
        }.onFailure { e ->
            _clearDataResult.emit(false)
            Log.e("SettingViewModel", "Error clearing data", e)
        }
    }
}