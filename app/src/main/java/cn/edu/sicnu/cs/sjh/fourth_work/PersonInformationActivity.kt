package cn.edu.sicnu.cs.sjh.fourth_work

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class PersonInformationActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var cbRead: CheckBox
    private lateinit var cbTravel: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShop: CheckBox
    private lateinit var player: Player

    companion object {
        private const val HOBBY_READ = "阅读"
        private const val HOBBY_TRAVEL = "旅游"
        private const val HOBBY_GAME = "游戏"
        private const val HOBBY_SHOP = "购物"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_information)

        etName = findViewById(R.id.et_name)
        etPhone = findViewById(R.id.et_phone)
        rgGender = findViewById(R.id.rg_gender)
        cbRead = findViewById(R.id.cb_read)
        cbTravel = findViewById(R.id.cb_travel)
        cbGame = findViewById(R.id.cb_game)
        cbShop = findViewById(R.id.cb_shop)
        val btnSave: Button = findViewById(R.id.btn_save)
        val btnCancel: Button = findViewById(R.id.btn_cancel)

        player = intent.getSerializableExtra("player") as Player

        etName.setText(player.name)
        etPhone.setText(player.phone)

        // 回填性别和爱好，避免再次编辑时把已有信息清空
        when (player.gender) {
            getString(R.string.male) -> rgGender.check(R.id.rb_male)
            getString(R.string.female) -> rgGender.check(R.id.rb_female)
        }
        cbRead.isChecked = player.hobby.contains(HOBBY_READ)
        cbTravel.isChecked = player.hobby.contains(HOBBY_TRAVEL)
        cbGame.isChecked = player.hobby.contains(HOBBY_GAME)
        cbShop.isChecked = player.hobby.contains(HOBBY_SHOP)

        btnSave.setOnClickListener {
            player.name = etName.text.toString()
            player.phone = etPhone.text.toString()

            player.gender = when(rgGender.checkedRadioButtonId){
                R.id.rb_male -> getString(R.string.male)
                R.id.rb_female -> getString(R.string.female)
                else -> ""
            }

            val hobbies = mutableListOf<String>()
            if (cbRead.isChecked) hobbies.add(HOBBY_READ)
            if (cbTravel.isChecked) hobbies.add(HOBBY_TRAVEL)
            if (cbGame.isChecked) hobbies.add(HOBBY_GAME)
            if (cbShop.isChecked) hobbies.add(HOBBY_SHOP)
            player.hobby = hobbies.joinToString(" ")

            val intent = Intent()
            intent.putExtra("player", player)
            setResult(RESULT_OK, intent)
            finish()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }
}
