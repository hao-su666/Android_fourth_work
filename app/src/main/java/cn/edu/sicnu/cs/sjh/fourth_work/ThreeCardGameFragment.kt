package cn.edu.sicnu.cs.sjh.fourth_work

/**
 * 第三个 Fragment：三张牌的翻牌游戏。
 * 三张牌面值相同才能消除。
 */
class ThreeCardGameFragment : CardGameFragment() {

    override val cardsPerMatch: Int = 3

    override val saveFileName: String = SaveFiles.THREE_CARD_GAME
}
