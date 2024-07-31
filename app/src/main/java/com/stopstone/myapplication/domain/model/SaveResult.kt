package com.stopstone.myapplication.domain.model

sealed class SaveResult {
    data object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
}