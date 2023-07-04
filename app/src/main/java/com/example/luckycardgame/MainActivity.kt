package com.example.luckycardgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.luckycardgame.data.Card

class MainActivity : AppCompatActivity() {

    private val TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, Array(10) { Card() }.joinToString(", "))
    }
}