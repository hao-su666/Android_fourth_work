package cn.edu.sicnu.cs.sjh.fourth_work

import org.junit.Assert.assertEquals
import org.junit.Test

class CardGameModelTest {
    @Test
    fun testResetGame(){
        val model = CardGameModel(8)
        model.clickCard(0)
        model.resetGame()
        assertEquals(0,model.score)
    }
}
