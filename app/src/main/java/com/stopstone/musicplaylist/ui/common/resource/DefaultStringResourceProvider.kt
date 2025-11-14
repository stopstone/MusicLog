package com.stopstone.musicplaylist.ui.common.resource

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Application Context를 활용해 문자열 리소스를 반환하는 기본 구현체.
 */
@Singleton
class DefaultStringResourceProvider
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : StringResourceProvider {
        override fun getString(@StringRes resId: Int): String = context.getString(resId)
    }

