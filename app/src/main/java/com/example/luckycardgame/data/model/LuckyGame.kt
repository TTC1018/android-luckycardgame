package com.example.luckycardgame.data.model

const val MIN_USER = 3
const val MAX_USER = 5
const val MAX_NUMBER = 12
const val MIN_NUMBER = 1
const val MAX_CARD_COUNT = 36

private val TAG = LuckyGame::class.java.simpleName

class LuckyGame(
    private val cards: Array<Array<Card>>,
    private var userCount: Int = 3,
): OnFlipCardListener {

    val users: List<User> = List(MAX_USER) { User(it, emptyList()) }
    private var shuffledCards: List<Card> = emptyList()
    private var leftCards: List<Card> = emptyList()
    private val cardIdxes = Array(MAX_USER) { intArrayOf(0, cardCountMap[MAX_USER] ?: throw Exception("등록되지 않은 유저 수: $MAX_USER")).also { it[1]-- } }

    fun resetGame(userCount: Int) {
        clearAllHands(userCount)
        setUserCount(userCount)
        shuffleAllCards(userCount)
        makeUserWithCards()
        sortUserCardsByNum()
    }

    private fun shuffleAllCards(userCount: Int) {
        val duplicatedCards = Array(3) { i -> cards[i].copyOf() }
        if (userCount == 3) {
            for (i in 0 until userCount) {
                duplicatedCards[i] = duplicatedCards[i].sliceArray(0 until MAX_NUMBER - 1)
            }
        }

        shuffledCards = duplicatedCards.flatten().shuffled()
    }

    private fun clearAllHands(userCount: Int) {
        // 뒤집은 상태 초기화
        cards.forEach { card -> card.forEach { it.flipped = false } }
        
        // 양쪽 참조 인덱스 초기화
        for (i in cardIdxes.indices) {
            cardIdxes[i] = intArrayOf(0, cardCountMap[userCount] ?: throw Exception("등록되지 않은 유저 수: $userCount")).apply { this[1]-- }
        }

        // 유저 카드 목록 초기화
        users.forEach { user -> user.cards = emptyList() }
    }

    private fun makeUserWithCards() {
        val cardCount = cardCountMap[userCount] ?: throw Exception("올바르지 않은 유저 수: $userCount")
        for (i in 0 until userCount) {
            users[i].cards = shuffledCards.subList(i * cardCount, (i + 1) * cardCount)
        }

        var left = MAX_CARD_COUNT - cardCount * userCount
        if (userCount == MIN_USER)
            left -= MIN_USER

        leftCards = shuffledCards.takeLast(left)
    }

    private fun setUserCount(userCount: Int) {
        this.userCount = userCount
    }

    fun sortUserCardsByNum() {
        for (i in 0 until userCount) {
            users[i].cards = users[i].cards.sortedBy { it.num }
        }
    }

    fun sortLeftCardsByNum() {
        leftCards = leftCards.sortedBy { it.num }
    }

    private fun checkUserHasTripleCards(userId: Int): Boolean {
        val cardsOfUserId = users[userId].cards
        return cardsOfUserId.groupBy { it.num }.mapValues { it.value.size }.containsValue(3)
    }

    fun findTripleCardsUsers(): List<Int> {
        return users
            .filter { checkUserHasTripleCards(it.userId) }
            .map { it.userId }
    }

    override fun onFlipCard(userId: Int, cardPos: Int) {
        val (leftIdx, rightIdx) = cardIdxes[userId]
        if (leftIdx > rightIdx) {
            return
        }

        val isLeft = leftIdx == cardPos
        val isRight = rightIdx == cardPos
        when {
            isLeft -> {
                users[userId].cards[leftIdx].flipped = true
                cardIdxes[userId][0]++

            }
            isRight -> {
                users[userId].cards[rightIdx].flipped = true
                cardIdxes[userId][1]--
            }
        }
    }

    fun compareTwoUsersCardWithLeftCard(userOneId: Int, userTwoId: Int, leftCard: Card = leftCards.random()): Boolean {
        val minNumOfUserOne = users[userOneId].cards.minOf { it.num }
        val maxNumOfUserOne = users[userOneId].cards.maxOf { it.num }
        val minNumOfUserTwo = users[userTwoId].cards.minOf { it.num }
        val maxNumOfUserTwo = users[userTwoId].cards.maxOf { it.num }
        val leftCardNumber = leftCard.num

        val minNumCount = setOf(minNumOfUserOne, minNumOfUserTwo, leftCardNumber).size
        val maxNumCount = setOf(maxNumOfUserOne, maxNumOfUserTwo, leftCardNumber).size
        return minNumCount == 1 || maxNumCount == 1
    }
    
    fun getLeftCards() = this.leftCards

    companion object {
        val cardCountMap = buildMap { put(3, 8); put(4, 7); put(5, 6) }

        val leftCountMap = buildMap {
            putAll(cardCountMap.mapValues { (userCount, cardCount) ->
                var leftCards = MAX_CARD_COUNT - userCount * cardCount
                if (userCount == MIN_USER)
                    leftCards -= MIN_USER

                leftCards
            })
        }
    }

}

interface OnFlipCardListener {
    fun onFlipCard(userId: Int, cardPos: Int)
}