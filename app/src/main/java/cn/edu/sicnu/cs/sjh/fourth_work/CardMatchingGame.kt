package cn.edu.sicnu.cs.sjh.fourth_work

/**
 * 翻牌游戏模型（Model）。
 *
 * 实验五要求实现三张牌玩法：牌面值相同的三张牌可以被消除。
 * 修改游戏规则时，只需要调整 [chooseCardAtIndex] 与 [Card.match]。
 */
class CardMatchingGame(val cardCount: Int) {

    private val cards = mutableListOf<Card>()
    private val chosenIndices = mutableListOf<Int>()

    var score = 0
        private set

    /** 三张牌不匹配时锁定棋盘，等待控制器延时把牌盖回去。 */
    var isLocked = false
        private set

    init {
        require(cardCount > 0 && cardCount % CARDS_PER_MATCH == 0) {
            "cardCount must be a positive multiple of $CARDS_PER_MATCH"
        }
        reset()
    }

    /**
     * 重新洗牌并重置分数、选中状态。实验五要求新增的 reset 方法。
     */
    fun reset() {
        cards.clear()
        val groupCount = cardCount / CARDS_PER_MATCH
        for (value in 1..groupCount) {
            repeat(CARDS_PER_MATCH) { cards.add(Card(value)) }
        }
        cards.shuffle()

        chosenIndices.clear()
        score = 0
        isLocked = false
    }

    /**
     * 选中指定位置的牌。
     *
     * @return true 表示本次选择凑满了三张并完成了一次匹配判定；
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

        if (chosenIndices.size < CARDS_PER_MATCH) return false

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
     * 三张牌不匹配后，由控制器延时调用，把选中的牌重新盖回去。
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
        /** 一次匹配需要的牌数。 */
        const val CARDS_PER_MATCH = 3
        const val MATCH_BONUS = 30
        const val MISMATCH_PENALTY = 10
    }
}
