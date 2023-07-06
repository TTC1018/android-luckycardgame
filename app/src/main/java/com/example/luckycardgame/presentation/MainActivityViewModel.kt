package com.example.luckycardgame.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.luckycardgame.data.model.Card
import com.example.luckycardgame.data.model.LuckyGame
import com.example.luckycardgame.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val luckyGame: LuckyGame
): ViewModel() {
    private val TAG = this.javaClass.simpleName

    private val _userCount = MutableLiveData<Int>()
    val userCount: LiveData<Int> get() = _userCount

    private val _users = MutableLiveData<Array<User>>()
    val users: LiveData<Array<User>> get() = _users

    private val _leftCards = MutableLiveData<List<Card>>()
    val leftCards: LiveData<List<Card>> get() = _leftCards

    fun reset(userCount: Int) {
        _userCount.value = userCount
        luckyGame.reGame(userCount)

        _users.value = luckyGame.getUsers()
        _leftCards.value = luckyGame.getLeftCards()
    }
}