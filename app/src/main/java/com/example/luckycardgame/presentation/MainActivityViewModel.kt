package com.example.luckycardgame.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.luckycardgame.data.model.Card
import com.example.luckycardgame.data.model.CardCheckable
import com.example.luckycardgame.data.model.LuckyGame
import com.example.luckycardgame.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private val TAG = MainActivityViewModel::class.java.simpleName

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val luckyGame: LuckyGame
): ViewModel(), OnFlipCardListener, CardCheckable {

    private val _userCount = MutableLiveData<Int>()
    val userCount: LiveData<Int> get() = _userCount

    private val _users: MutableLiveData<List<User>> = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _leftCards = MutableLiveData<List<Card>>()
    val leftCards: LiveData<List<Card>> get() = _leftCards

    private val _endFlag = MutableLiveData<Boolean>(luckyGame.getEndFlag())
    val endFlag: LiveData<Boolean> get() = _endFlag

    private val _winners = MutableLiveData<Set<Int>>(luckyGame.getWinners())
    val winners: LiveData<Set<Int>> get() = _winners

    fun reset(userCount: Int) {
        _endFlag.value = false
        _userCount.value = userCount
        luckyGame.resetGame(userCount)

        _users.value = luckyGame.users
        _leftCards.value = luckyGame.getLeftCards()
    }

    override fun checkPicked(userId: Int, cardIdx: Int): List<Int> = luckyGame.checkPicked(userId, cardIdx)

    override fun onFlipCard(userId: Int, cardPos: Int) {
        luckyGame.onFlipCard(userId, cardPos)
        if (luckyGame.getEndFlag()) {
            _winners.value = luckyGame.getWinners()
            _endFlag.value = luckyGame.getEndFlag()
        } else {
            _winners.value = emptySet()
        }
    }
}