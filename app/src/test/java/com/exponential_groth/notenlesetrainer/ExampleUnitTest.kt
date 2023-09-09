package com.exponential_groth.notenlesetrainer

import com.exponential_groth.notenlesetrainer.util.KeyColor
import com.exponential_groth.notenlesetrainer.util.toKeyColor
import com.exponential_groth.notenlesetrainer.util.toKeyNum
import com.exponential_groth.notenlesetrainer.util.toWhiteKeyNum
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testToKeyNum() {
        for (i in 1..25) {
            assertEquals(i - if (i.toKeyColor() == KeyColor.BLACK) 1 else 0, i.toWhiteKeyNum().toKeyNum())
        }
    }
}