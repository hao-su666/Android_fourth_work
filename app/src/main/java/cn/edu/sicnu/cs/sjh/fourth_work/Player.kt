package cn.edu.sicnu.cs.sjh.fourth_work

import java.io.Serializable

// 实现Serializable，Intent页面间传递对象
data class Player(
    var name: String = "",
    var gender: String = "",
    var phone: String = "",
    var hobby: String = "",
    var maxScore: Int = 0
) : Serializable