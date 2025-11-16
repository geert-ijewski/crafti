import Lox
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class SampleTest {

    private val testLox: Lox = Lox()

    @Test
    fun testSum() {
        assertEquals(42.0, testLox.interpret("40+2"))
    }

    @Test
    fun testWhitespaceSum() {
        assertEquals(42.0, testLox.interpret("  40\t+\n 2\t"))
    }

    @Test
    fun testComment() {
        assertEquals(20.0, testLox.interpret("// abc\n1+19"))
    }
}
