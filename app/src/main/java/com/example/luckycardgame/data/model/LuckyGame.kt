package com.example.luckycardgame.data.model

import kotlin.math.absoluteValue
import com.example.luckycardgame.presentation.OnFlipCardListener

const val MIN_USER = 3
const val MAX_USER = 5
const val MAX_NUMBER = 12
const val MIN_NUMBER = 1
const val MAX_CARD_COUNT = 36

private val TAG = LuckyGame::class.java.simpleName

class LuckyGame(
    private val cards: Array<Array<Card>>,
    private var userCount: Int = 3,
) : OnFlipCardListener, CardCheckable {

    val users: List<User> = List(MAX_USER) { User(it, emptyList()) }
    private var shuffledCards: List<Card> = emptyList()
    private var leftCards: List<Card> = emptyList()
    private val cardIdxes: List<IntArray> = List(MAX_USER) { intArrayOf(0, cardCountMap[MAX_USER] ?: throw Exception("등록되지 않은 유저 수: $MAX_USER")).also { it[1]-- } }
    private var endFlag = false
    private val chanceCounter = IntArray(MAX_USER) { 3 }
    private val flippedCounter = Array(MAX_USER) { mutableMapOf<Int, Int>() }
    private var winners = emptySet<Int>()

    fun resetGame(userCount: Int) {
        setUserCount(userCount)
        resetRound()
        clearTurn()
        clearAllHands()
        shuffleAllCards()
        makeUserWithCards()
        sortUserCardsByNum()
    }

    private fun resetRound() {
        endFlag = false
        winners = emptySet()
        for (i in 0 until userCount) {
            flippedCounter[i].clear()
        }
    }

    private fun clearTurn() {
        for (i in 0 until userCount) {
            chanceCounter[i] = 3
        }
    }

    private fun checkTurnContinue(): Set<Int> {
        val winners = findWinner()
        if (winners.isNotEmpty() || cardIdxes.slice(0 until userCount).all { it[0] > it[1] })
            endFlag = true

        this.winners = winners.toSortedSet()
        return winners
    }

    private fun findWinner(): Set<Int> {
        return buildSet {
            // 7 카드 보유자 찾기
            addAll(findSevenOwner())

            // 서로 짝을 맞췄을 때 합 또는 차가 7이 되는 경우 찾기
            addAll(findSevenSumOwners())
        }
    }

    private fun findSevenSumOwners(): Collection<Int> {
        return buildList {
            val numOwner = mutableMapOf<Int, Int>()
            val cands = flippedCounter.withIndex()
                .filter { (i, _) -> checkUserHasTripleCards(i) }
                .map { (userId, map) ->
                    val numOfThreeCards = map.filterValues { v -> v == 3 }
                    numOfThreeCards.keys.forEach { numOwner[it] = userId }
                    numOfThreeCards.keys
                }.flatten()

            for (i in cands.indices) {
                for (j in i + 1 until cands.size) {
                    if (cands[i] + cands[j] == 7 || (cands[i] - cands[j]).absoluteValue == 7)
                        addAll(
                            listOf(
                                numOwner[cands[i]]
                                    ?: throw Exception("등록되지 않은 숫자 소유자: ${cands[i]}"),
                                numOwner[cands[j]] ?: throw Exception("등록되지 않은 숫자 소유자: ${cands[j]}")
                            )
                        )
                }
            }
        }
    }

    private fun findSevenOwner(): Collection<Int> {
        return buildList {
            findTripleCardsUsers().forEach { userId ->
                if (flippedCounter[userId].any { (num, cnt) -> num == 7 && cnt == 3 }) {
                    add(userId)
                }
            }
        }
    }

    private fun shuffleAllCards() {
        val duplicatedCards = Array(3) { i -> cards[i].copyOf() }
        if (userCount == 3) {
            for (i in 0 until userCount) {
                duplicatedCards[i] = duplicatedCards[i].sliceArray(0 until MAX_NUMBER - 1)
            }
        }

        shuffledCards = duplicatedCards.flatten().shuffled()
    }

    private fun clearAllHands() {
        // 뒤집은 상태 초기화
        cards.forEach { card -> card.forEach { it.flipped = false } }
        // 같은 3장 매치된 상태 초기화
        cards.forEach { card -> card.forEach { it.matched = false } }

        // 양쪽 참조 인덱스 초기화
        for (i in cardIdxes.indices) {
            cardIdxes[i][0] = 0
            cardIdxes[i][1] = cardCountMap[userCount] ?: throw Exception("등록되지 않은 유저 수: $userCount")
            cardIdxes[i][1]--
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

    fun sortUserCardsByNum() {
        for (i in 0 until userCount) {
            users[i].cards = users[i].cards.sortedBy { it.num }
        }
    }

    fun sortLeftCardsByNum() {
        leftCards = leftCards.sortedBy { it.num }
    }

    override fun checkPicked(userId: Int, cardIdx: Int): List<Int> {
        val cardsOfUser = users[userId].cards
        val cardNum = cardsOfUser[cardIdx].num
        return buildList {
            if (flippedCounter[userId][cardNum] == 3) {
                cardsOfUser
                    .withIndex()
                    .filter { (_, v) -> v.num == cardNum }
                    .forEach { (i, v) -> v.matched = true; add(i) }
            }
        }
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
        if (leftIdx > rightIdx || chanceCounter[userId] == 0) {
            return
        }

        val isLeft = leftIdx == cardPos
        val isRight = rightIdx == cardPos
        val flipCounterOfUser = flippedCounter[userId]
        when {
            isLeft -> {
                val card = users[userId].cards[leftIdx]
                card.flipped = true
                flipCounterOfUser[card.num] = flipCounterOfUser.getOrDefault(card.num, 0) + 1
                cardIdxes[userId][0]++
                chanceCounter[userId]--
            }

            isRight -> {
                val card = users[userId].cards[rightIdx]
                card.flipped = true
                flipCounterOfUser[card.num] = flipCounterOfUser.getOrDefault(card.num, 0) + 1
                cardIdxes[userId][1]--
                chanceCounter[userId]--
            }
        }

        if (isEndOfTurn()) {
            clearTurn()
            checkTurnContinue()
        }
    }

    private fun isEndOfTurn(): Boolean = chanceCounter.sliceArray(0 until userCount).all { it == 0 }
            || cardIdxes.slice(0 until userCount).all { it[0] > it[1] }


    fun compareTwoUsersCardWithLeftCard(
        userOneId: Int,
        userTwoId: Int,
        leftCard: Card = leftCards.random()
    ): Boolean {
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

    fun getEndFlag() = this.endFlag

    fun getWinners() = this.winners

    private fun setUserCount(userCount: Int) {
        this.userCount = userCount
    }

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

interface CardCheckable {
    fun checkPicked(userId: Int, cardIdx: Int): List<Int>
}