package scaffold.core

import org.junit.Assert
import org.junit.Test

class FlagUnitTest {
    companion object{
        private val flagCreator = FlagCreator()
        private val FlagEmpty = flagCreator.createNext()
        private val Flag1 = flagCreator.createNext()
        private val Flag2 = flagCreator.createNext()
        private val Flag3 = flagCreator.createNext()
    }
    @Test
    fun plus() {
        var flag = FlagEmpty
        flag += Flag1
        Assert.assertTrue(flag contains Flag1)
        Assert.assertFalse(flag contains Flag2)
    }

    @Test
    fun minus() {
        var flag = FlagEmpty
        flag += Flag1
        flag -= Flag1
        Assert.assertFalse(flag contains Flag1)
        Assert.assertFalse(flag contains Flag2)
    }

    @Test
    fun process() {
        var flag = FlagEmpty
        flag += Flag1
        flag += Flag3
        flag -= Flag1
        flag.process {
            Assert.assertFalse(Flag1.has)
            Assert.assertFalse(Flag2.has)
            Assert.assertTrue(Flag3.has)
        }
    }

}