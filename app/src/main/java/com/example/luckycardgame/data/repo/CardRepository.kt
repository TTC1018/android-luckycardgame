package com.example.luckycardgame.data.repo

import com.example.luckycardgame.data.model.AnimalType
import com.example.luckycardgame.data.model.Card
import com.example.luckycardgame.data.model.MAX_NUMBER

interface CardRepository {
    fun getCards(): Array<Array<Card>>
}

class CardRepositoryImpl : CardRepository {

    private val animals = AnimalType.values()

    override fun getCards(): Array<Array<Card>> =
        Array(animals.size) { i -> Array(MAX_NUMBER) { num -> Card(animals[i], num + 1) } }
}