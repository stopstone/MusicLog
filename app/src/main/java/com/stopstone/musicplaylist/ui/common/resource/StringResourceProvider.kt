package com.stopstone.musicplaylist.ui.common.resource

import androidx.annotation.StringRes

/**
 * 문자열 리소스를 안전하게 제공하기 위한 인터페이스.
 */
interface StringResourceProvider {
    fun getString(@StringRes resId: Int): String
}

