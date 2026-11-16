package cn.edu.sicnu.cs.sjh.fourth_work

import java.io.Serializable

/**
 * 翻牌游戏的历史信息。
 *
 * - [bestScore] 历史最高分
 * - [gamesWon] 通关次数
 * - [recentScores] 最近若干次通关的得分
 */
class GameHistory : Serializable {
    var bestScore: Int = 0
    var gamesWon: Int = 0
    val recentScores: ArrayList<Int> = ArrayList()

    /** 记录一次通关，只保留最近 [MAX_RECENT] 次成绩。 */
    fun recordWin(score: Int) {
        bestScore = maxOf(bestScore, score)
        gamesWon += 1
        recentScores.add(score)
        while (recentScores.size > MAX_RECENT) {
            recentScores.removeAt(0)
        }
    }

    companion object {
        private const val MAX_RECENT = 5
        private const val serialVersionUID = 1L
    }
}

/**
 * 一次游戏的完整存档：当前牌局 + 历史统计。
 * 退出程序后重新打开，可以恢复牌局并查看历史成绩。
 */
class GameSave : Serializable {
    var model: CardMatchingGame? = null
    var history: GameHistory = GameHistory()

    companion object {
        private const val serialVersionUID = 1L
    }
}
