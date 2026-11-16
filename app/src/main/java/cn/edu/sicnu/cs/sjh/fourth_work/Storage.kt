package cn.edu.sicnu.cs.sjh.fourth_work

import android.content.Context
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * 序列化存储工具。
 *
 * 按实验六的要求使用 openFileOutput / openFileInput，
 * 配合 ObjectOutputStream / ObjectInputStream 读写 Serializable 对象。
 */
object Storage {

    /** 把可序列化对象写入应用私有文件，失败时返回 false。 */
    fun <T : Serializable> save(context: Context, fileName: String, value: T): Boolean {
        return try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
                ObjectOutputStream(fos).use { it.writeObject(value) }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /** 从应用私有文件读取可序列化对象，不存在或读取失败时返回 null。 */
    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable> load(context: Context, fileName: String): T? {
        return try {
            context.openFileInput(fileName).use { fis ->
                ObjectInputStream(fis).use { it.readObject() as? T }
            }
        } catch (e: Exception) {
            // 文件不存在（首次运行）或版本不兼容时都当作没有历史数据
            null
        }
    }
}

/** 各个模块使用的存档文件名。 */
object SaveFiles {
    const val PLAYER = "player.dat"
    const val TWO_CARD_GAME = "two_card_game.dat"
    const val THREE_CARD_GAME = "three_card_game.dat"
}
