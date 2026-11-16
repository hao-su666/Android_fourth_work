package cn.edu.sicnu.cs.sjh.fourth_work

import java.io.Serializable

/**
 * 翻牌游戏模型（Model）。
 *
 * 通过 [cardsPerMatch] 同时支持实验六要求的两种玩法：
 * - 两张牌玩法：两张牌面值相同即可消除；
 * - 三张牌玩法：三张牌面值相同才能消除。
 *
 * 模型实现 Serializable，配合 [GameSave] 把整个牌局写入文件，
 * 退出程序后再打开可以恢复上次的牌面与分数。
 */
class CardMatchingGame(
    val cardCount: Int,
    val cardsPerMatch: Int
) : Serializable {

    private val cards = mutableListOf<Card>()
    private val chosenIndices = mutableListOf<Int>()

    var score = 0
        private set

    /** 凑满一组牌但不匹配时锁定棋盘，等待控制器延时把牌盖回去。 */
    var isLocked = false
        private set

    init {
        require(cardCount > 0 && cardsPerMatch >= 2 && cardCount % cardsPerMatch == 0) {
            "cardCount must be a positive multiple of cardsPerMatch"
        }
        reset()
    }

    /** 重新洗牌并重置分数、选中状态。 */
    fun reset() {
        cards.clear()
        val groupCount = cardCount / cardsPerMatch
        for (value in 1..groupCount) {
            repeat(cardsPerMatch) { cards.add(Card(value)) }
        }
        cards.shuffle()

        chosenIndices.clear()
        score = 0
        isLocked = false
    }

    /**
     * 选中指定位置的牌。
     *
     * @return true 表示本次选择凑满了一组并完成了一次匹配判定；
     *         false 表示只是翻开/取消了一张牌。
     */
    fun chooseCardAtIndex(index: Int): Boolean {
        if (isLocked || index !in cards.indices) return false

        val card = cards[index]
        if (card.isMatched) return false

        // 再次点击已选中的牌则取消选择
        if (card.isChosen) {
            card.isChosen = false
            chosenIndices.remove(index)
            return false
        }

        card.isChosen = true
        chosenIndices.add(index)

        if (chosenIndices.size < cardsPerMatch) return false

        val firstCard = cards[chosenIndices.first()]
        val otherCards = chosenIndices.drop(1).map { cards[it] }

        return if (firstCard.match(otherCards)) {
            chosenIndices.forEach {
                cards[it].isChosen = false
                cards[it].isMatched = true
            }
            chosenIndices.clear()
            score += MATCH_BONUS
            true
        } else {
            score = (score - MISMATCH_PENALTY).coerceAtLeast(0)
            isLocked = true
            true
        }
    }

    /**
     * 一组牌不匹配后，由控制器延时调用，把选中的牌重新盖回去。
     */
    fun revealChosenCards() {
        chosenIndices.forEach { cards[it].isChosen = false }
        chosenIndices.clear()
        isLocked = false
    }

    fun isGameOver(): Boolean = cards.all { it.isMatched }

    fun getCard(index: Int): Card = cards[index]

    fun getCardValue(index: Int): Int = cards[index].value

    fun isCardChosen(index: Int): Boolean = cards[index].isChosen

    fun isCardMatched(index: Int): Boolean = cards[index].isMatched

    companion object {
        const val MATCH_BONUS = 20
        const val MISMATCH_PENALTY = 10
        private const val serialVersionUID = 1L
    }
}
