package cn.edu.sicnu.cs.sjh.fourth_work

/**
 * 第二个 Fragment：两张牌的翻牌游戏。
 * 两张牌面值相同即可消除。
 */
class TwoCardGameFragment : CardGameFragment() {

    override val cardsPerMatch: Int = 2

    override val saveFileName: String = SaveFiles.TWO_CARD_GAME
}
