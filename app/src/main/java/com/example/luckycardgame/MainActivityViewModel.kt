package com.example.luckycardgame

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(): ViewModel() {
    private val TAG = this.javaClass.simpleName

    private val _userNum = MutableLiveData<Int>()
    val userNum: LiveData<Int> get() = _userNum

    fun setUserNum(num: Int) {
        _userNum.value = num
    }
}