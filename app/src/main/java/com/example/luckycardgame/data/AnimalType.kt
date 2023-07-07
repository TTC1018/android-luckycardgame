package com.example.luckycardgame.data

// String이나 Int 등으로 Dog, Cat, Cow를 구분하는 것보다 enum이 정확하다고 생각합니다.
// 유니코드를 내부 속성으로 가져 활용이 원활해지도록 구현
enum class AnimalType(val emoji: String) {
    DOG("\uD83D\uDC36"), CAT("\uD83D\uDC31"), COW("\uD83D\uDC2E")
}

// 미사용이지만 sealed로도 구현해보기
sealed class Animal(val emoji: String) {
    object Dog: Animal("\uD83D\uDC36")
    object Cat: Animal("\uD83D\uDC31")
    object Cow: Animal("\uD83D\uDC2E")
}