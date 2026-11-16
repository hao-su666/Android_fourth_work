package cn.edu.sicnu.cs.sjh.fourth_work

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * 第一个 Fragment：个人信息。
 *
 * 个人信息以 Serializable 对象的形式写入 player.dat，
 * 退出程序后重新打开会自动恢复；同时读取两个翻牌游戏的历史最高分。
 */
class PersonInfoFragment : Fragment(R.layout.fragment_person_info) {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var rgGender: RadioGroup
    private lateinit var cbRead: CheckBox
    private lateinit var cbTravel: CheckBox
    private lateinit var cbGame: CheckBox
    private lateinit var cbShop: CheckBox
    private lateinit var tvGameRecords: TextView

    private var player = Player()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.et_name)
        etPhone = view.findViewById(R.id.et_phone)
        rgGender = view.findViewById(R.id.rg_gender)
        cbRead = view.findViewById(R.id.cb_read)
        cbTravel = view.findViewById(R.id.cb_travel)
        cbGame = view.findViewById(R.id.cb_game)
        cbShop = view.findViewById(R.id.cb_shop)
        tvGameRecords = view.findViewById(R.id.tv_game_records)

        val btnSave: Button = view.findViewById(R.id.btn_save)
        val btnCancel: Button = view.findViewById(R.id.btn_cancel)

        loadPlayer()
        refreshGameRecords()

        btnSave.setOnClickListener {
            val ok = collectAndSave()
            val message = if (ok) R.string.save_success else R.string.save_failed
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // 取消：丢弃未保存的修改，重新从文件读取
        btnCancel.setOnClickListener {
            loadPlayer()
            Toast.makeText(requireContext(), R.string.cancel_restored, Toast.LENGTH_SHORT).show()
        }
    }

    /** 从序列化文件恢复个人信息。 */
    private fun loadPlayer() {
        player = Storage.load<Player>(requireContext(), SaveFiles.PLAYER) ?: Player()

        etName.setText(player.name)
        etPhone.setText(player.phone)

        rgGender.clearCheck()
        when (player.gender) {
            getString(R.string.male) -> rgGender.check(R.id.rb_male)
            getString(R.string.female) -> rgGender.check(R.id.rb_female)
        }
        cbRead.isChecked = player.hobby.contains(getString(R.string.hobby_read))
        cbTravel.isChecked = player.hobby.contains(getString(R.string.hobby_travel))
        cbGame.isChecked = player.hobby.contains(getString(R.string.hobby_game))
        cbShop.isChecked = player.hobby.contains(getString(R.string.hobby_shop))
    }

    /** 把界面上的内容收集成 Player 对象并序列化保存。 */
    private fun collectAndSave(): Boolean {
        player.name = etName.text.toString().trim()
        player.phone = etPhone.text.toString().trim()

        player.gender = when (rgGender.checkedRadioButtonId) {
            R.id.rb_male -> getString(R.string.male)
            R.id.rb_female -> getString(R.string.female)
            else -> ""
        }

        val hobbies = mutableListOf<String>()
        if (cbRead.isChecked) hobbies.add(getString(R.string.hobby_read))
        if (cbTravel.isChecked) hobbies.add(getString(R.string.hobby_travel))
        if (cbGame.isChecked) hobbies.add(getString(R.string.hobby_game))
        if (cbShop.isChecked) hobbies.add(getString(R.string.hobby_shop))
        player.hobby = hobbies.joinToString(" ")

        return Storage.save(requireContext(), SaveFiles.PLAYER, player)
    }

    /** 读取两个翻牌游戏的存档，显示各自的历史最高分。 */
    private fun refreshGameRecords() {
        if (view == null) return
        val twoCard = Storage.load<GameSave>(requireContext(), SaveFiles.TWO_CARD_GAME)
        val threeCard = Storage.load<GameSave>(requireContext(), SaveFiles.THREE_CARD_GAME)
        tvGameRecords.text = getString(
            R.string.game_records_format,
            twoCard?.history?.bestScore ?: 0,
            threeCard?.history?.bestScore ?: 0
        )
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        // 从游戏页切换回个人信息页时刷新最高分
        if (!hidden) refreshGameRecords()
    }

    override fun onPause() {
        super.onPause()
        // 离开页面时自动保存，保证未按“保存”也能恢复
        if (this::etName.isInitialized) {
            collectAndSave()
        }
    }
}
