package com.example.luckycardgame.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import com.example.luckycardgame.R
import com.example.luckycardgame.databinding.ActivityMainBinding
import com.example.luckycardgame.util.extractNumWithOutLetter
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        addToggleChangedListener()
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
}