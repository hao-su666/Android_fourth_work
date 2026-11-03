package cn.edu.sicnu.cs.sjh.fourth_work

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var tvName: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvMaxScore: TextView
    private var player: Player = Player()

    companion object {
        const val REQ_EDIT = 1001
        const val REQ_GAME = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvName = findViewById(R.id.tv_name)
        tvGender = findViewById(R.id.tv_gender)
        tvPhone = findViewById(R.id.tv_phone)
        tvMaxScore = findViewById(R.id.tv_max_score)
        val btnEdit: Button = findViewById(R.id.btn_edit)
        val btnGame: Button = findViewById(R.id.btn_game)

        refreshUI()

        btnEdit.setOnClickListener {
            val intent = Intent(this, PersonInformationActivity::class.java)
            intent.putExtra("player", player)
            startActivityForResult(intent, REQ_EDIT)
        }

        btnGame.setOnClickListener {
            val intent = Intent(this, CardGameActivity::class.java)
            intent.putExtra("player", player)
            startActivityForResult(intent, REQ_GAME)
        }
    }

    private fun refreshUI() {
        tvName.text = "${getString(R.string.name)}:${player.name}"
        tvGender.text = "${getString(R.string.gender)}:${player.gender}"
        tvPhone.text = "${getString(R.string.phone)}:${player.phone}"
        tvMaxScore.text = "${getString(R.string.max_score)}:${player.maxScore}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val retPlayer = data.getSerializableExtra("player") as Player?
            retPlayer?.let {
                player = it
                refreshUI()
            }
        }
    }
}
