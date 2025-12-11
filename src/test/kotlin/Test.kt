import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import Scanner
import Parser
import TokenType.*

class SampleTest {

    @Test
    fun testSumTokens() {
        val scanner = Scanner("40+2")
        val tokens = scanner.scanTokens()
        val types = tokens.map { it.type }
        assertEquals(listOf(NUMBER, PLUS, NUMBER, EOF), types)
        val numbers = tokens.filter { it.type == NUMBER }.map { it.literal as Double }
        assertEquals(listOf(40.0, 2.0), numbers)
    }

    @Test
    fun testWhitespaceSumTokens() {
        val scanner = Scanner("  40\t+\n 2\t")
        val tokens = scanner.scanTokens()
        val types = tokens.map { it.type }
        assertEquals(listOf(NUMBER, PLUS, NUMBER, EOF), types)
        val numbers = tokens.filter { it.type == NUMBER }.map { it.literal as Double }
        assertEquals(listOf(40.0, 2.0), numbers)
    }

    @Test
    fun testCommentTokens() {
        val scanner = Scanner("// abc\n1+19")
        val tokens = scanner.scanTokens()
        val types = tokens.map { it.type }
        assertEquals(listOf(NUMBER, PLUS, NUMBER, EOF), types)
        val numbers = tokens.filter { it.type == NUMBER }.map { it.literal as Double }
        assertEquals(listOf(1.0, 19.0), numbers)
    }

    @Test
    fun testPrintStatementParsed() {
        val scanner = Scanner("print 2+2;")
        val tokens = scanner.scanTokens()
        val parser = Parser(tokens)
        val stmts = parser.parse()
        assertEquals(1, stmts.size)
        assertTrue(stmts[0] is Stmt.Print)
    }

    @Test
    fun testVariableAssignment() {
        val scanner = Scanner("var a = 123;print a;")
        val tokens = scanner.scanTokens()
        assertEquals(9, tokens.size)
        val parser = Parser(tokens)
        val stmts = parser.parse()
        assertEquals(2, stmts.size)

        val interpreter = Interpreter()
        interpreter.interpret(stmts)
    }

}
