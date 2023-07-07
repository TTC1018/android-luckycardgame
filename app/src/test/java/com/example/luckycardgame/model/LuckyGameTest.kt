package com.example.luckycardgame.model

import com.example.luckycardgame.data.model.AnimalType
import com.example.luckycardgame.data.model.Card
import com.example.luckycardgame.data.model.LuckyGame
import com.example.luckycardgame.data.model.MAX_CARD_COUNT
import com.example.luckycardgame.data.model.MAX_USER
import com.example.luckycardgame.data.model.MIN_USER
import com.example.luckycardgame.data.repo.CardRepository
import com.example.luckycardgame.data.repo.CardRepositoryImpl
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Assert.*
import java.util.EnumMap

class LuckyGameTest {

    private lateinit var luckyGame: LuckyGame

    companion object {
        private lateinit var cardRepository: CardRepository
        private lateinit var animalTypes: Array<AnimalType>

        @JvmStatic
        @BeforeClass
        fun init() {
            cardRepository = CardRepositoryImpl()
            animalTypes = AnimalType.values()
        }
    }

    // 테스트 함수 내에서 따로 regame을 하지 않으면 항상 최대 유저수로 진행
    @Before
    fun initLuckyGame() {
        luckyGame = LuckyGame(cardRepository.getCards())
        luckyGame.reGame(MAX_USER) // 기본적으로 User 5명이라 가정하고 시작
    }

    // 테스트 케이스 컨벤션
    // 메소드명_기존데이터상태_호출뒤예상결과

    // 나눠준 카드와 남는 카드를 합쳤을 때 타입별로 12장씩 존재하는지 테스트 (3명일 때는 11장씩)
    @Test
    fun reGame_HandOutShuffledCards_EachTypeHasTwelveCards() {
        // 최소 인원 수부터 최대 인원 수까지 확인
        for (userCount in MIN_USER..MAX_USER) {
            // 각 타입이 가진 카드 개수 -> 전체 카드 개수 / 타입 개수
            var actualCountPerAnimal = MAX_CARD_COUNT.div(animalTypes.size)
            if (userCount == MIN_USER) // 3명은 번호12를 버리므로 총 33장
                actualCountPerAnimal -= 1

            luckyGame.reGame(userCount)
            val activeUsersCards = luckyGame.getUsers().sliceArray(0 until userCount).map { it.cards }
            val leftCards = luckyGame.getLeftCards()
            val cardCounterByAnimal = EnumMap(animalTypes.associateWith { 0 }).apply {
                // 유저가 가진 카드 개수 타입 별로 세기
                for (cards in activeUsersCards) {
                    val countUserCardsByType = cards.groupBy { it.type }.mapValues { it.value.size }
                    for ((type, count) in countUserCardsByType) {
                        put(type, getOrDefault(type, 0) + count)
                    }
                }

                // 남은 카드도 세기
                val countLeftCardsByType = leftCards.groupBy { it.type }.mapValues { it.value.size }
                for ((type, count) in countLeftCardsByType) {
                    put(type, getOrDefault(type, 0) + count)
                }
            }

            if (cardCounterByAnimal.values.any { count -> count != actualCountPerAnimal })
                assert(false)
        }

        assert(true)
    }

    // 참여자가 3명인 경우에 12번 카드가 존재하지 않는지 테스트
    @Test
    fun reGame_ThreeUsersPlay_NoTwelfthCard(){
        luckyGame.reGame(MIN_USER)
        val activeUsersCards = luckyGame.getUsers().sliceArray(0 until MIN_USER).map { it.cards }

        // 3번째 유저까지 카드 목록 조회
        // 12번 카드가 존재하면 안 됨
        for (cards in activeUsersCards) {
            if (cards.any { it.num == 12 })
                assert(false)
        }

        assert(true)
    }

    // 이전에 나눠준 카드들과 다른 카드 목록을 갖는 유저가 한명이라도 있는지(섞였는지) 확인
    @Test
    fun reGame_HandOutShuffledCards_AtLeastOneUserHasDifferentCards() {
        val users = luckyGame.getUsers()
        val prevCards = users.map { it.cards.toList() } // 지금 각 유저가 갖고 있는 카드

        luckyGame.reGame(MAX_USER) // 카드 다시 나눠주기
        val nextCards = users.map { it.cards } // 게임 재시작 후 유저가 갖고 있는 카드

        val result = prevCards.zip(nextCards).any { (prev, next) -> prev != next }
        assertTrue(result)
    }

    // 나눠주고 남은 카드 수가 올바른지 테스트
    @Test
    fun reGame_HandOutShuffledCards_LeftCardsMustBeSameWithNumberOfMap() {
        for (userCount in MIN_USER..MAX_USER) {
            luckyGame.reGame(userCount)
            val registeredCount = LuckyGame.leftCountMap[userCount] ?: throw AssertionError("남은 카드 수에 등록되지 않은 유저 수: $userCount")
            val handOutCountPerUser = LuckyGame.cardCountMap[userCount] ?: throw AssertionError("유저 별 카드 수에 등록되지 않은 유저 수: $userCount")
            var actualCount = (MAX_CARD_COUNT - (userCount * handOutCountPerUser)) // 전체 카드 개수 - (유저 수 * 유저별 카드 개수)
            if (userCount == 3)
                actualCount -= userCount

            val leftCardCount = luckyGame.getLeftCards().size
            // Map에 기록된 남은 카드 장수와 실제로 남는 카드 장수 모두 비교
            if (leftCardCount != registeredCount || leftCardCount != actualCount)
                assert(false)
        }

        assert(true)
    }

    // 각 사용자가 동일한 카드 수를 갖는지 테스트
    @Test
    fun reGame_HandOutShuffledCards_AllUsersHaveRightNumberOfCards() {
        for (userCount in MIN_USER..MAX_USER) {
            luckyGame.reGame(userCount)
            val activeUserCardCounts = luckyGame.getUsers().sliceArray(0 until userCount).map { it.cards.size }
            val exactCount = LuckyGame.cardCountMap[userCount] ?: AssertionError("등록되지 않은 유저 수: $userCount")

            if (activeUserCardCounts.any { it != exactCount })
                assert(false)
        }

        assert(true)
    }

    // 유저 별 카드를 번호 기준으로 정렬 후에 비내림차순인지 테스트
    @Test
    fun sortUserCardsByNum_CardsInRandomOrder_NumsInAscOrder() {
        luckyGame.sortUserCardsByNum()

        val usersCards = luckyGame.getUsers().map { it.cards }
        for (cards in usersCards) {
            // 단 하나라도 다음 카드보다 번호가 크면 false
            if (cards.zipWithNext().any { (prev, next) -> prev.num > next.num })
                assert(false)
        }

        assert(true)
    }

    // 남은 카드를 번호 기준으로 정렬 후에 비내림차순인지 테스트
    @Test
    fun sortLeftCardsByNum_CardsInRandomOrder_NumsInAscOrder() {
        luckyGame.sortLeftCardsByNum()

        // 모든 카드가 다음 카드보다 번호가 작으면 true
        val result = luckyGame.getLeftCards()
            .zipWithNext()
            .all { (prev, next) -> prev.num <= next.num }

        assert(result)
    }

    // 같은 번호 카드 세장을 가진 유저가 있는지 테스트
    @Test
    fun findTripleCardsUsers_FirstUserHasTripleCards_FindAtLeastOneUser() {
        val users = luckyGame.getUsers()
        val userOne = users.first()
        val cardCount = LuckyGame.cardCountMap[MAX_USER] ?: throw AssertionError("카드 장 수가 설정되어 있지 않은 인원 수: $MAX_USER")

        // 첫번째 유저가 번호1 3개, 번호2 3개 갖도록 조작
        userOne.cards = buildList {
            repeat(cardCount / 2) { add(Card(num = 1)) }
            repeat(cardCount / 2) { add(Card(num = 2)) }
        }

        // 찾기 (유저ID를 리스트에 담아서 리턴)
        val found = luckyGame.findTripleCardsUsers()

        // 첫번째 유저가 조건에 해당하므로, 최소 한명 이상 존재해야 됨 (결과 리스트가 비어있으면 안 됨)
        assertTrue(found.isNotEmpty())
    }

    // 두 유저가 최소/최대 번호가 같은 카드를 가질 때, 어떤 남은 카드와 같은지를 테스트
    @Test
    fun compareTwoUsersCardWithLeftCard_TwoUsersHaveNumberOfLeftCardsAbsolutely_MatchWithLeftCardAtLeastOnce() {
        val leftCards = luckyGame.getLeftCards()
        val users = luckyGame.getUsers()

        // 두 유저가 남은 카드와 똑같은 카드를 갖도록 조작
        val userOne = users[0]
        val userTwo = users[1]
        userOne.cards = leftCards.toList()
        userTwo.cards = leftCards.toList()

        // 두 유저가 남은 카드와 똑같은 카드를 갖고 있는 시점에서
        // 모든 남은 카드와 비교해봤을 때 적어도 두번(최대, 최소)은 같아야 함
        val sameCount = leftCards.count { leftCard -> luckyGame.compareTwoUsersCardWithLeftCard(0, 1, leftCard) }
        assert(sameCount >= 2)
    }
}

