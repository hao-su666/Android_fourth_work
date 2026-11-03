package cn.edu.sicnu.cs.sjh.fourth_work

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

class CardGameActivity : AppCompatActivity() {
    private lateinit var tvGameScore: TextView
    private lateinit var gvCard: GridView
    private lateinit var model: CardGameModel
    private lateinit var adapter: CardAdapter
    private lateinit var player: Player

    // 不匹配时停留多久再盖回去
    private var hideCardsRunnable: Runnable? = null

    companion object {
        private const val CARD_TOTAL = 16
        private const val MISMATCH_DELAY = 800L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)
        tvGameScore = findViewById(R.id.tv_game_score)
        gvCard = findViewById(R.id.gv_card)
        val btnReset: Button = findViewById(R.id.btn_reset)
        val btnExit: Button = findViewById(R.id.btn_exit)

        player = intent.getSerializableExtra("player") as Player
        model = CardGameModel(CARD_TOTAL)

        adapter = CardAdapter(this, model) { pos ->
            val (opened, matched, first) = model.clickCard(pos)
            // 翻开第二张且不匹配：停留一会儿再把两张牌盖回去并解锁
            if (opened && !matched && first != -1) {
                hideCardsRunnable?.let { gvCard.removeCallbacks(it) }
                val hide = Runnable {
                    model.closeTwoCard(first, pos)
                    adapter.notifyDataSetChanged()
                }
                hideCardsRunnable = hide
                gvCard.postDelayed(hide, MISMATCH_DELAY)
            }
            tvGameScore.text = "${getString(R.string.score)}:${model.score}"
            adapter.notifyDataSetChanged()
        }
        gvCard.adapter = adapter

        btnReset.setOnClickListener {
            hideCardsRunnable?.let { gvCard.removeCallbacks(it) }
            model.resetGame()
            tvGameScore.text = "${getString(R.string.score)}:${model.score}"
            adapter.notifyDataSetChanged()
        }

        btnExit.setOnClickListener {
            saveScoreAndExit()
        }

        // 按系统返回键也要保存最高分
        onBackPressedDispatcher.addCallback(this) {
            saveScoreAndExit()
        }
    }

    private fun saveScoreAndExit() {
        hideCardsRunnable?.let { gvCard.removeCallbacks(it) }
        if (model.score > player.maxScore) {
            player.maxScore = model.score
        }
        val intent = Intent()
        intent.putExtra("player", player)
        setResult(RESULT_OK, intent)
        finish()
    }
}
