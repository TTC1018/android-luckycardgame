package com.example.luckycardgame.presentation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.luckycardgame.data.model.Card
import com.example.luckycardgame.data.model.CardCheckable
import com.example.luckycardgame.data.model.LuckyGame
import com.example.luckycardgame.data.model.User
import kotlin.math.ceil

// 유저 카드 간격 설정용
@BindingAdapter(value = ["showCardsOfUser", "onFlipCard", "checkPicked"])
fun RecyclerView.showCardsAndSetFlipFunction(user: User?, onFlipCardListener: OnFlipCardListener, cardCheckable: CardCheckable) {
    user?.let { u ->
        if (adapter == null) {
            this.adapter = MyCardAdapter(u.userId, onFlipCardListener, cardCheckable)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    if (parent.getChildAdapterPosition(view) != 0) {
                        val cardCount = (adapter as MyCardAdapter).itemCount
                        if (cardCount != LuckyGame.cardCountMap[5]) {
                            val wholeWidth = (parent.parent as ConstraintLayout).width.toDouble()
                            val width = ceil((wholeWidth * (0.88 / cardCount) / 4)).toInt()
                            outRect.left = -width
                        }
                    }
                }
            })
        }
        (this.adapter as MyCardAdapter).submitList(u.cards)
    }
}

// 최하단 남은 카드 간격 설정용
@BindingAdapter(value = ["showLeftCards", "spanCount"], requireAll = false)
fun RecyclerView.showLeftCards(leftCards: List<Card>?, userCount: Int?) {
    leftCards?.let {
        if (adapter == null) {
            this.adapter = MyCardAdapter(-1, object : OnFlipCardListener {
                override fun onFlipCard(userId: Int, cardPos: Int) {}
            }, object : CardCheckable {
                override fun checkPicked(userId: Int, cardIdx: Int): List<Int> = emptyList()
            })
            (this.layoutManager as GridLayoutManager).spanCount = 2

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val cl = (parent.parent as ConstraintLayout)
                    val spanCount = (parent.layoutManager as GridLayoutManager).spanCount
                    val cardCount = parent.adapter?.itemCount
                    val row = cardCount?.toDouble()?.div(2)
                    if (spanCount == 1 || row == null) return

                    val wholeWidth = cl.width.toDouble()
                    val wholeHeight = cl.height.toDouble()
                    val startEndSpacing = when (cardCount) {
                        LuckyGame.leftCountMap[3] -> ceil(wholeWidth * 0.05 / ceil(row)).toInt()
                        LuckyGame.leftCountMap[4] -> ceil(wholeWidth * 0.15 / ceil(row)).toInt()
                        else -> throw Exception("옳지 않은 남은 카드 수: $cardCount")
                    }
                    val topBotSpacing = ceil(wholeHeight * 0.25 * 0.1 / spanCount).toInt()

                    with(outRect) {
                        top = topBotSpacing
                        bottom = topBotSpacing
                        left = startEndSpacing
                        right = startEndSpacing
                    }
                }
            })
        }
        (this.adapter as MyCardAdapter).submitList(leftCards)
        (this.layoutManager as GridLayoutManager).spanCount = when (userCount) {
            5 -> 1
            else -> 2
        }
    }
}

// 3장 매칭된 카드 사라지는 효과
@BindingAdapter("fadeOutIfPicked")
fun View.fadeOutIfPicked(picked: Boolean) {
    if (picked) {
        ObjectAnimator.ofFloat(this, "alpha", 0.0f)
            .apply {
                duration = 1000
                interpolator = AccelerateInterpolator()
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        this@fadeOutIfPicked.visibility = View.GONE
                    }
                })
            }.start()
    } else {
        this.visibility = View.VISIBLE
    }
}