package com.example.luckycardgame.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.luckycardgame.data.model.Card
import com.example.luckycardgame.data.model.CardCheckable
import com.example.luckycardgame.data.model.LuckyGame
import com.example.luckycardgame.data.model.MAX_CARD_COUNT
import com.example.luckycardgame.data.model.MIN_USER
import com.example.luckycardgame.databinding.ItemCardBinding
import kotlin.math.ceil

class MyCardAdapter(
    private val userId: Int,
    private val onFlipCardListener: OnFlipCardListener,
    private val cardCheckable: CardCheckable
) : ListAdapter<Card, MyCardViewHolder>(diffUtil), OnCardClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCardViewHolder =
        MyCardViewHolder(
            ItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), this
        )

    override fun onBindViewHolder(holder: MyCardViewHolder, position: Int) {
        holder.bind(getItem(position), userId)
    }

    override fun onCardClick(userId: Int, pos: Int) {
        onFlipCardListener.onFlipCard(userId, pos)

        val shouldBeHidden = cardCheckable.checkPicked(userId, pos)
        if (shouldBeHidden.isNotEmpty()) {
            shouldBeHidden.forEach { notifyItemChanged(it) }
        }
        else {
            notifyItemChanged(pos)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Card>() {
            override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean =
                false
            // 카드 개수에 맞게 가로 길이를 설정했는데,
            // 뷰를 재활용하는 상황이 생기면 이전 카드 가로길이가 그대로 보존되는 경우가 생겨
            // 일단은 강제로 false 리턴 시킴..

            override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean =
                false
        }
    }
}

class MyCardViewHolder(
    val binding: ItemCardBinding,
    val onCardClick: OnCardClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

    private var lifecycleOwner: LifecycleOwner? = null

    init {
        itemView.doOnAttach {
            lifecycleOwner = it.findViewTreeLifecycleOwner()
        }
        itemView.doOnDetach {
            lifecycleOwner = null
        }
    }

    fun bind(card: Card, userId: Int) {
        binding.card = card
        binding.userId = userId
        binding.position = adapterPosition
        binding.onCardClickListener = onCardClick
        binding.lifecycleOwner = lifecycleOwner

        // 화면 가로 길이의 특정 비율로 가로 길이 재조정
        with(binding.root) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val cl = parent.parent as ConstraintLayout
                    val cardCount = (parent as RecyclerView).adapter?.itemCount
                    if (cl.width == 0 || cardCount == null) return

                    val width = cl.width.toDouble()
                    // 최하단 남은 카드는 길이 배치 다르게 설정하기
                    layoutParams.width =
                        if (userId == -1 && cardCount != LuckyGame.cardCountMap[5]) {
                            val cardNumWhenMaxUser = LuckyGame.cardCountMap[MIN_USER]
                                ?: throw Exception("잘못된 카드 장수: $cardCount")
                            val leftCards = MAX_CARD_COUNT - cardNumWhenMaxUser * MIN_USER
                            ceil(width * (0.85 / ceil(leftCards.toDouble() / 2))).toInt()
                        } else
                            ceil(width * (0.88 / cardCount)).toInt()

                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }
}

interface OnCardClickListener {
    fun onCardClick(userId: Int, pos: Int)
}

interface OnFlipCardListener {
    fun onFlipCard(userId: Int, cardPos: Int)
}