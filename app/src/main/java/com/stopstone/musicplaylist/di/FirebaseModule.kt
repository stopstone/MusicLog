package com.stopstone.musicplaylist.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Firebase 관련 의존성 주입 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * FirebaseFirestore 인스턴스 제공
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        val firestore = FirebaseFirestore.getInstance()
        
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        
        firestore.firestoreSettings = settings
        
        return firestore
    }
}

