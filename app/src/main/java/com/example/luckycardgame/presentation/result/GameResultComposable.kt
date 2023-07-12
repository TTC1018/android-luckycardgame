package com.example.luckycardgame.presentation.result

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.luckycardgame.R
import com.example.luckycardgame.data.model.AnimalType


@Composable
fun GameResultScreen(
    modifier: Modifier = Modifier,
    viewModel: GameResultViewModel
) {
    val userCount by viewModel.userCount.collectAsStateWithLifecycle()
    val matchedCardsOfUsers by viewModel.matchedCards.collectAsStateWithLifecycle()
    val winners by viewModel.winners.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { GameResultTopText() },
        bottomBar = {
            GameResultBottomArea(
                winners = winners,
                backToMain = viewModel::backToMain
            )
        }
    ) {
        GameResultMainArea(
            modifier = Modifier.padding(it),
            userCount = userCount,
            matchedCardsOfUsers = matchedCardsOfUsers,
            winners = winners
        )
    }
}

@Composable
fun GameResultMainArea(
    modifier: Modifier = Modifier,
    userCount: Int,
    matchedCardsOfUsers: Map<Int, Set<Int>>,
    winners: Set<Int>
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Column(modifier = modifier) {
        repeat(userCount) { userId ->
            PlayerCardRow(
                modifier = Modifier.padding(12.dp),
                text = "${'A' + userId}",
                isWinner = userId in winners,
                screenWidth = screenWidth,
                rowHeight = (screenHeight.toDouble() * 0.125).toInt(),
                cardNums = matchedCardsOfUsers[userId] ?: emptySet()
            )
        }
    }
}

@Composable
fun PlayerCardRow(
    modifier: Modifier = Modifier,
    text: String,
    isWinner: Boolean,
    screenWidth: Int,
    rowHeight: Int,
    cardNums: Set<Int>
) {
    val animalTypes = remember { AnimalType.values() }
    val rowWidth = (screenWidth.toDouble() * 0.8)

    Row(
        modifier = modifier
            .background(
                color = if (isWinner) colorResource(id = R.color.red_light)
                else colorResource(id = R.color.gray_light),
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .height(rowHeight.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterVertically),
            text = text,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.gray_blur),
            fontSize = 30.sp
        )

        Spacer(
            modifier = Modifier
                .width(8.dp)
        )

        val cardCount = cardNums.size * animalTypes.size
        val cardWidth = (rowWidth * 0.8 / cardCount).toInt()
        cardNums.forEach { num ->
            animalTypes.forEach { animal ->
                PlayerCard(
                    modifier = Modifier
                        .padding(4.dp),
                    cardNum = num,
                    animalType = animal,
                    width = cardWidth,
                    height = rowHeight
                )
            }
        }
    }
}

@Composable
fun PlayerCard(
    modifier: Modifier = Modifier,
    cardNum: Int,
    animalType: AnimalType,
    width: Int,
    height: Int
) {
    Box(
        modifier = modifier
            .width(width.dp)
            .height(height.dp)
            .border(
                border = BorderStroke(1.dp, colorResource(id = R.color.black)),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = colorResource(id = R.color.white),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            text = "$cardNum",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(modifier = Modifier.align(Alignment.Center), text = animalType.emoji)
        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            text = "$cardNum",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GameResultTopText(
    modifier: Modifier = Modifier
) {
    Text(
        text = "게임결과",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}

@Composable
fun GameResultBottomArea(
    modifier: Modifier = Modifier,
    winners: Set<Int>,
    backToMain: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "이번 게임은 ${winners.map { 'A' + it }.joinToString(", ")}가 승리했습니다.",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentPadding = PaddingValues(8.dp),
            shape = RectangleShape,
            onClick = backToMain
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically),
                text = "재시작",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun PreviewGameResultBottomArea() {
    GameResultBottomArea(
        winners = setOf(0, 3, 4),
        backToMain = {}
    )
}

@Preview
@Composable
fun PreviewMainArea() {
    GameResultMainArea(
        userCount = 3,
        matchedCardsOfUsers = mapOf(0 to setOf(1), 1 to setOf(2), 2 to setOf(3)),
        winners = setOf(0, 1)
    )
}

@Preview
@Composable
fun PreviewPlayerCardRow() {
    PlayerCardRow(
        text = "A",
        isWinner = false,
        screenWidth = 400,
        rowHeight = 100,
        cardNums = setOf(1)
    )
}

@Preview
@Composable
fun PreviewAnimalCard() {
    PlayerCard(
        cardNum = 1,
        animalType = AnimalType.DOG,
        width = 100,
        height = 150
    )
}