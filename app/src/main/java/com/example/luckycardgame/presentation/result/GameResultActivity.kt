package com.example.luckycardgame.presentation.result


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.luckycardgame.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GameResultActivity : ComponentActivity() {

    private val viewModel by viewModels<GameResultViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameResultScreen(viewModel = viewModel)
        }

        collectUiState()
        blockOnBackPressed()
    }

    private fun blockOnBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
        }
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        ResultUiState.Loading -> {}
                        ResultUiState.Complete -> {
                            setResult(
                                RESULT_OK,
                                Intent(
                                    this@GameResultActivity,
                                    MainActivity::class.java
                                ).apply {
                                    putExtra(MainActivity.KEY_USERCOUNT, viewModel.userCount.value)
                                    putExtra(MainActivity.KEY_RESET, true)
                                }
                            )
                            finish()
                        }
                    }
                }
            }
        }
    }
}