package com.stopstone.musicplaylist.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseHttpClient