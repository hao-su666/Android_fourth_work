package cn.edu.sicnu.cs.sjh.fourth_work

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

/**
 * 实验六：使用 Fragment 重构 APP。
 *
 * 三个 Fragment 通过 TabLayout 切换：
 * 1. [PersonInfoFragment]  个人信息
 * 2. [TwoCardGameFragment] 两张牌翻牌游戏
 * 3. [ThreeCardGameFragment] 三张牌翻牌游戏
 *
 * 使用 add + show/hide 而不是 replace，切换标签时保留各 Fragment 的界面状态。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_person))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_two_card))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_three_card))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) = Unit

            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })

        // 首次启动时显示第一个标签对应的 Fragment
        showFragment(tabLayout.selectedTabPosition.coerceAtLeast(0))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // 旋转屏幕等配置变更后，TabLayout 会在状态恢复阶段重新选中标签，
        // 这里再同步一次 Fragment 的显示/隐藏状态
        if (savedInstanceState != null) {
            showFragment(tabLayout.selectedTabPosition.coerceAtLeast(0))
        }
    }

    private fun showFragment(position: Int) {
        val tag = TAGS[position]
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()

        for (t in TAGS) {
            val fragment = fm.findFragmentByTag(t) ?: continue
            if (t == tag) transaction.show(fragment) else transaction.hide(fragment)
        }

        // 该标签对应的 Fragment 还没创建时才 add；已存在则由上面的 show 恢复
        if (fm.findFragmentByTag(tag) == null) {
            transaction.add(R.id.fragment_container, createFragment(position), tag)
        }
        transaction.commit()
    }

    private fun createFragment(position: Int): Fragment = when (position) {
        0 -> PersonInfoFragment()
        1 -> TwoCardGameFragment()
        else -> ThreeCardGameFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about) {
            showAboutDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about_title)
            .setMessage(R.string.about_msg)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    companion object {
        private val TAGS = arrayOf("person_info", "two_card_game", "three_card_game")
    }
}
