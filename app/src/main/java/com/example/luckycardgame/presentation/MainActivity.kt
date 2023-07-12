package com.example.luckycardgame.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import com.example.luckycardgame.R
import com.example.luckycardgame.databinding.ActivityMainBinding
import com.example.luckycardgame.presentation.result.GameResultActivity
import com.example.luckycardgame.util.extractNumWithOutLetter
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private val startResult = registerForGameResult()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        addToggleChangedListener()
        observeGameEnd()
    }

    private fun registerForGameResult() = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val resetFlag = it.data?.extras?.getBoolean(KEY_RESET)
        val userCount = it.data?.extras?.getInt(KEY_USERCOUNT)
        if (resetFlag == true && userCount != null) {
            viewModel.reset(userCount)
        }
    }

    private fun observeGameEnd() {
        viewModel.endFlag.observe(this) { flag ->
            when (flag) {
                true -> {
                    val userCount = viewModel.userCount.value
                    if (userCount != null) {
                        val msg = if (viewModel.winners.value.isNullOrEmpty().not()) {
                            startResult.launch(Intent(this, GameResultActivity::class.java))
                            "승자: ${viewModel.winners.value?.map { 'A' + it }?.joinToString(" ")}"
                        }
                        else {
                            viewModel.reset(userCount)
                            getString(R.string.msg_game_end)
                        }
                        Snackbar.make(binding.layoutMain, msg, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
                false -> {}
            }
        }
    }

    private fun addToggleChangedListener() {
        binding.tgButtons.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val toggleBtn = group.findViewById<MaterialButton>(checkedId)
            val userCount = toggleBtn.extractNumWithOutLetter()
            when (isChecked) {
                true -> {
                    toggleBtn.icon = AppCompatResources.getDrawable(this, R.drawable.ic_check)
                    viewModel.reset(userCount)
                }
                false -> toggleBtn.icon = null
            }
        }
    }

    companion object {
        const val KEY_RESET = "resetGame"
        const val KEY_USERCOUNT = "userCount"
    }
}