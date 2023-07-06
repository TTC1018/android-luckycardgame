package com.example.luckycardgame.util

import android.widget.TextView

private val numberRegex = Regex("[^0-9]")

fun TextView.extractNumWithOutLetter():Int = text.replace(numberRegex, "").toInt()