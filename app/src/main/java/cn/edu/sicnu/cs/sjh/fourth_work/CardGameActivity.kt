package cn.edu.sicnu.cs.sjh.fourth_work

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 翻牌游戏控制器（Controller）。
 *
 * 竖屏使用 12 张牌（4 列），横屏使用 24 张牌（6 列），
 * 两种牌数都能被 3 整除，适配实验五的三张牌玩法。
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var tvGameScore: TextView
    private lateinit var rvCards: RecyclerView
    private lateinit var model: CardMatchingGame
    private lateinit var adapter: CardAdapter
    private lateinit var player: Player

    // 不匹配时停留多久再把三张牌盖回去
    private var hideCardsRunnable: Runnable? = null

    private val isLandscape: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    private val cardCount: Int
        get() = if (isLandscape) LANDSCAPE_CARD_COUNT else PORTRAIT_CARD_COUNT

    private val spanCount: Int
        get() = if (isLandscape) LANDSCAPE_COLUMNS else PORTRAIT_COLUMNS

    companion object {
        private const val PORTRAIT_CARD_COUNT = 12
        private const val LANDSCAPE_CARD_COUNT = 24
        private const val PORTRAIT_COLUMNS = 4
        private const val LANDSCAPE_COLUMNS = 8
        private const val MISMATCH_DELAY = 800L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)

        tvGameScore = findViewById(R.id.tv_game_score)
        rvCards = findViewById(R.id.rv_cards)
        val btnReset: Button = findViewById(R.id.btn_reset)
        val btnExit: Button = findViewById(R.id.btn_exit)

        player = intent.getSerializableExtra("player") as? Player ?: Player()
        model = CardMatchingGame(cardCount)

        rvCards.layoutManager = GridLayoutManager(this, spanCount)
        adapter = CardAdapter(model) { position -> onCardClick(position) }
        rvCards.adapter = adapter

        updateScore()

        btnReset.setOnClickListener { resetGame() }
        btnExit.setOnClickListener { saveScoreAndExit() }

        // 按系统返回键也要保存最高分
        onBackPressedDispatcher.addCallback(this) { saveScoreAndExit() }
    }

    private fun onCardClick(position: Int) {
        val evaluated = model.chooseCardAtIndex(position)
        adapter.notifyDataSetChanged()
        updateScore()

        if (!evaluated) return

        if (model.isLocked) {
            // 三张牌不匹配：停留一会儿再把它们盖回去
            hideCardsRunnable?.let { rvCards.removeCallbacks(it) }
            val hide = Runnable {
                model.revealChosenCards()
                adapter.notifyDataSetChanged()
                hideCardsRunnable = null
            }
            hideCardsRunnable = hide
            rvCards.postDelayed(hide, MISMATCH_DELAY)
        } else if (model.isGameOver()) {
            showWinDialog()
        }
    }

    private fun resetGame() {
        hideCardsRunnable?.let { rvCards.removeCallbacks(it) }
        hideCardsRunnable = null
        model.reset()
        adapter.notifyDataSetChanged()
        updateScore()
    }

    private fun updateScore() {
        tvGameScore.text = getString(R.string.score_format, model.score)
    }

    private fun showWinDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.game_win_title)
            .setMessage(getString(R.string.game_win_msg, model.score))
            .setPositiveButton(R.string.play_again) { _, _ -> resetGame() }
            .setNegativeButton(R.string.exit) { _, _ -> saveScoreAndExit() }
            .setCancelable(false)
            .show()
    }

    private fun saveScoreAndExit() {
        hideCardsRunnable?.let { rvCards.removeCallbacks(it) }
        hideCardsRunnable = null
        if (model.score > player.maxScore) {
            player.maxScore = model.score
        }
        val intent = Intent()
        intent.putExtra("player", player)
        setResult(RESULT_OK, intent)
        finish()
    }
}
