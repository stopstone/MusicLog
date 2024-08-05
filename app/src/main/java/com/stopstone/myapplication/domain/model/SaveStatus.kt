package com.stopstone.myapplication.domain.model


sealed class SaveStatus {
    object Success : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}