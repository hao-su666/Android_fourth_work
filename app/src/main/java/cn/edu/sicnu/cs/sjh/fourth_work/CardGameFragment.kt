package cn.edu.sicnu.cs.sjh.fourth_work

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 翻牌游戏 Fragment 基类。
 *
 * 两张牌和三张牌玩法只在 [cardsPerMatch] 与存档文件上不同，
 * 界面、点击逻辑和序列化存储都复用这里的实现。
 */
abstract class CardGameFragment : Fragment(R.layout.fragment_card_game) {

    /** 一次匹配需要的牌数：两张牌玩法为 2，三张牌玩法为 3。 */
    protected abstract val cardsPerMatch: Int

    /** 该游戏使用的存档文件名。 */
    protected abstract val saveFileName: String

    private lateinit var tvGameScore: TextView
    private lateinit var tvGameHistory: TextView
    private lateinit var rvCards: RecyclerView
    private lateinit var model: CardMatchingGame
    private lateinit var adapter: CardAdapter

    private var history = GameHistory()
    private var hideCardsRunnable: Runnable? = null
    private var viewsReady = false

    private val isLandscape: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private val cardCount: Int
        get() = if (isLandscape) LANDSCAPE_CARD_COUNT else PORTRAIT_CARD_COUNT

    private val spanCount: Int
        get() = if (isLandscape) LANDSCAPE_COLUMNS else PORTRAIT_COLUMNS

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvGameScore = view.findViewById(R.id.tv_game_score)
        tvGameHistory = view.findViewById(R.id.tv_game_history)
        rvCards = view.findViewById(R.id.rv_cards)
        val btnReset: Button = view.findViewById(R.id.btn_reset)

        // 从序列化文件中恢复历史信息和上次的牌局
        val save = Storage.load<GameSave>(requireContext(), saveFileName)
        history = save?.history ?: GameHistory()
        val restored = save?.model
        model = if (restored != null &&
            restored.cardCount == cardCount &&
            restored.cardsPerMatch == cardsPerMatch
        ) {
            // 上次退出时可能正处于“不匹配等待翻回”的状态，恢复时先解锁
            if (restored.isLocked) restored.revealChosenCards()
            restored
        } else {
            // 首次运行，或旋转屏幕后牌数变化，重新开局
            CardMatchingGame(cardCount, cardsPerMatch)
        }

        rvCards.layoutManager = GridLayoutManager(requireContext(), spanCount)
        adapter = CardAdapter(model) { position -> onCardClick(position) }
        rvCards.adapter = adapter

        btnReset.setOnClickListener { resetGame() }
        viewsReady = true
        updateUi()
    }

    private fun onCardClick(position: Int) {
        val evaluated = model.chooseCardAtIndex(position)
        adapter.notifyDataSetChanged()
        updateUi()

        if (!evaluated) {
            // 只是翻开/取消一张牌，也立即存档
            saveGame()
            return
        }

        when {
            // 凑满的一组牌不匹配：停留一会儿再把它们盖回去
            model.isLocked -> scheduleHideCards()
            model.isGameOver() -> onGameWon()
            else -> saveGame()
        }
    }

    private fun scheduleHideCards() {
        hideCardsRunnable?.let { rvCards.removeCallbacks(it) }
        val hide = Runnable {
            hideCardsRunnable = null
            if (!isAdded || view == null) return@Runnable
            model.revealChosenCards()
            adapter.notifyDataSetChanged()
            saveGame()
        }
        hideCardsRunnable = hide
        rvCards.postDelayed(hide, MISMATCH_DELAY)
    }

    private fun onGameWon() {
        history.recordWin(model.score)
        saveGame()
        updateUi()

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.game_win_title)
            .setMessage(getString(R.string.game_win_msg, model.score))
            .setPositiveButton(R.string.play_again) { _, _ -> resetGame() }
            .setNegativeButton(R.string.ok, null)
            .setCancelable(false)
            .show()
    }

    private fun resetGame() {
        hideCardsRunnable?.let { rvCards.removeCallbacks(it) }
        hideCardsRunnable = null
        model.reset()
        adapter.notifyDataSetChanged()
        updateUi()
        saveGame()
    }

    private fun updateUi() {
        history.bestScore = maxOf(history.bestScore, model.score)
        tvGameScore.text = getString(R.string.score_format, model.score)

        val recent = if (history.recentScores.isEmpty()) {
            getString(R.string.no_history)
        } else {
            history.recentScores.joinToString("、")
        }
        tvGameHistory.text = getString(
            R.string.game_history_format,
            history.bestScore,
            history.gamesWon,
            recent
        )
    }

    /** 使用 ObjectOutputStream 把牌局和历史成绩写入私有文件。 */
    private fun saveGame() {
        if (!viewsReady || !isAdded) return
        val save = GameSave()
        save.model = model
        save.history = history
        Storage.save(requireContext(), saveFileName, save)
    }

    override fun onPause() {
        super.onPause()
        if (!viewsReady) return

        // 页面离开时收起未完成的匹配，避免下次打开棋盘一直处于锁定状态
        hideCardsRunnable?.let { rvCards.removeCallbacks(it) }
        hideCardsRunnable = null
        if (model.isLocked) {
            model.revealChosenCards()
            adapter.notifyDataSetChanged()
        }
        updateUi()
        saveGame()
    }

    override fun onDestroyView() {
        hideCardsRunnable?.let { rvCards.removeCallbacks(it) }
        hideCardsRunnable = null
        viewsReady = false
        super.onDestroyView()
    }

    companion object {
        private const val PORTRAIT_CARD_COUNT = 12
        private const val LANDSCAPE_CARD_COUNT = 24
        private const val PORTRAIT_COLUMNS = 4
        private const val LANDSCAPE_COLUMNS = 8
        private const val MISMATCH_DELAY = 800L
    }
}
