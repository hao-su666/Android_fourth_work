package cn.edu.sicnu.cs.sjh.fourth_work

/**
 * 单张牌。value 相同的牌为一组，实验五采用三张牌匹配玩法。
 */
class Card(val value: Int) {

    /** 是否已被玩家选中（翻开状态）。 */
    var isChosen = false

    /** 是否已经匹配成功（消除状态）。 */
    var isMatched = false

    /**
     * 判断本牌与另外两张牌是否匹配。
     * 三张牌牌面值相同即视为匹配。
     */
    fun match(otherCards: List<Card>): Boolean =
        otherCards.size == 2 && otherCards.all { it.value == value }
}
