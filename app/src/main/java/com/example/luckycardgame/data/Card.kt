package com.example.luckycardgame.data

import kotlin.random.Random

private val animals = AnimalType.values()

data class Card(
    val type: AnimalType = animals[Random.nextInt(animals.size)],
    val num: Int = Random.nextInt(1, 12 + 1)
) {
    // 콘솔 출력 편하게 toString 오버라이드
    override fun toString() = "${type.emoji}${num.toString().padStart(2, '0')}"
}