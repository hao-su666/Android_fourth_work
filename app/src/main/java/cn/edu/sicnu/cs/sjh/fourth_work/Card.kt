package cn.edu.sicnu.cs.sjh.fourth_work

import java.io.Serializable

/**
 * 单张牌。value 相同的牌为一组，两张牌或三张牌玩法由 [CardMatchingGame] 决定。
 *
 * 实现 Serializable，使整个牌局可以随游戏一起写入文件。
 */
class Card(val value: Int) : Serializable {

    /** 是否已被玩家选中（翻开状态）。 */
    var isChosen = false

    /** 是否已经匹配成功（消除状态）。 */
    var isMatched = false

    /**
     * 判断本牌与另外几张牌是否匹配。
     * 传入的牌与本牌牌面值全部相同即视为匹配（两牌玩法传 1 张，三牌玩法传 2 张）。
     */
    fun match(otherCards: List<Card>): Boolean =
        otherCards.isNotEmpty() && otherCards.all { it.value == value }

    companion object {
        private const val serialVersionUID = 1L
    }
}
