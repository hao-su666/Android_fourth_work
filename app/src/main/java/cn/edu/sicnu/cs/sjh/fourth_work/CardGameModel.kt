package cn.edu.sicnu.cs.sjh.fourth_work

import kotlin.random.Random

class CardGameModel(private val cardCount: Int) {
    private var cardValues = IntArray(cardCount)
    private var isOpened = BooleanArray(cardCount)
    private var firstClickIndex = -1
    var isLocked = false
    var score = 0
        private set

    init {
        initCards()
    }

    private fun initCards() {
        for (i in 0 until cardCount / 2) {
            cardValues[i] = i
            cardValues[i + cardCount / 2] = i
        }
        shuffleCard()
    }

    fun shuffleCard() {
        for (i in cardValues.indices) {
            val swapPos = Random.nextInt(cardValues.size)
            val temp = cardValues[i]
            cardValues[i] = cardValues[swapPos]
            cardValues[swapPos] = temp
        }
        isOpened.fill(false)
        firstClickIndex = -1
        score = 0
        isLocked = false
    }

    fun clickCard(pos: Int): Triple<Boolean, Boolean, Int> {
        if (isOpened[pos] || isLocked) return Triple(false, false, -1)
        isOpened[pos] = true

        if (firstClickIndex == -1) {
            firstClickIndex = pos
            return Triple(true, false, -1)
        } else {
            isLocked = true
            val match = cardValues[firstClickIndex] == cardValues[pos]
            val first = firstClickIndex
            if (match) {
                score += 10
                firstClickIndex = -1
                isLocked = false
            } else {
                firstClickIndex = -1
            }
            return Triple(true, match, first)
        }
    }

    fun closeTwoCard(i1: Int, i2: Int) {
        isOpened[i1] = false
        isOpened[i2] = false
        isLocked = false
    }

    fun resetGame() {
        shuffleCard()
    }

    fun getCardValue(pos: Int): Int = cardValues[pos]
    fun isCardOpen(pos: Int): Boolean = isOpened[pos]
    // 对外获取卡牌总数量
    fun getCardCount(): Int = cardCount
}
