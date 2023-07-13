package com.example.luckycardgame.di

import com.example.luckycardgame.data.model.LuckyGame
import com.example.luckycardgame.data.repo.CardRepository
import com.example.luckycardgame.data.repo.CardRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LuckyGameModule {

    @Singleton
    @Provides
    fun provideCardRepository(): CardRepository = CardRepositoryImpl()

    @Singleton
    @Provides
    fun provideLuckyGame(cardRepository: CardRepository) = LuckyGame(cardRepository.getCards())

}