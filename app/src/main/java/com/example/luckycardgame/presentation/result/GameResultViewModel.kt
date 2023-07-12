package com.example.luckycardgame.presentation.result


import androidx.lifecycle.ViewModel
import com.example.luckycardgame.data.model.LuckyGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class GameResultViewModel @Inject constructor(
    private val luckyGame: LuckyGame
): ViewModel() {

    private val _userCount = MutableStateFlow(luckyGame.getUserCount())
    val userCount: StateFlow<Int> get() = _userCount

    private val _matchedCards = MutableStateFlow<Map<Int, Set<Int>>>(luckyGame.getCardsOfWinners())
    val matchedCards: StateFlow<Map<Int, Set<Int>>> get() = _matchedCards

    private val _winners = MutableStateFlow(luckyGame.getWinners())
    val winners: StateFlow<Set<Int>> get() = _winners

    private val _uiState = MutableStateFlow<ResultUiState>(ResultUiState.Loading)
    val uiState: StateFlow<ResultUiState> get() = _uiState

    fun backToMain() {
        _uiState.value = ResultUiState.Complete
    }
}

sealed interface ResultUiState {
    object Loading: ResultUiState
    object Complete: ResultUiState
}