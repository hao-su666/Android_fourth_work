package cn.edu.sicnu.cs.sjh.fourth_work

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardMatchingGameTest {

    @Test
    fun resetClearsScoreAndChosenState() {
        val model = CardMatchingGame(3)
        model.chooseCardAtIndex(0)
        assertTrue(model.isCardChosen(0))

        model.reset()

        assertEquals(0, model.score)
        assertFalse(model.isCardChosen(0))
        assertFalse(model.isGameOver())
    }

    @Test
    fun matchingThreeSameValueCardsScores() {
        val model = CardMatchingGame(3)

        assertFalse(model.chooseCardAtIndex(0))
        assertFalse(model.chooseCardAtIndex(1))
        assertTrue(model.chooseCardAtIndex(2))

        assertEquals(CardMatchingGame.MATCH_BONUS, model.score)
        assertTrue(model.isGameOver())
        assertTrue(model.isCardMatched(0))
        assertTrue(model.isCardMatched(1))
        assertTrue(model.isCardMatched(2))
    }

    @Test
    fun mismatchedThreeCardsLockThenReveal() {
        val model = CardMatchingGame(6)
        val groups = (0 until 6).groupBy { model.getCardValue(it) }
        val firstGroup = groups.values.first()
        val secondGroup = groups.values.first { it.first() != firstGroup.first() }
        val picks = listOf(firstGroup[0], firstGroup[1], secondGroup[0])

        picks.dropLast(1).forEach { assertFalse(model.chooseCardAtIndex(it)) }
        assertTrue(model.chooseCardAtIndex(picks.last()))

        assertTrue(model.isLocked)
        assertTrue(model.isCardChosen(picks[0]))

        model.revealChosenCards()

        assertFalse(model.isLocked)
        picks.forEach { assertFalse(model.isCardChosen(it)) }
        assertEquals(0, model.score)
    }
}
