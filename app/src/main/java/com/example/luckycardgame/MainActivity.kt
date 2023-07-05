package com.example.luckycardgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import com.example.luckycardgame.databinding.ActivityMainBinding
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
            val userNum = toggleBtn.text.first().digitToInt()
            when (isChecked) {
                true -> {
                    toggleBtn.icon = AppCompatResources.getDrawable(this, R.drawable.ic_check)
                    viewModel.setUserNum(userNum)
                }
                false -> toggleBtn.icon = null
            }
        }
    }
}