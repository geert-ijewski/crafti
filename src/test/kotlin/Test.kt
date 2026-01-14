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

        val interpreter = Interpreter { str: String -> assertEquals("123", str) }
        interpreter.interpret(stmts)
    }

    @Test
    fun testMultiVariableAndReassignment() {
        val scanner = Scanner("var a = 99;var b = 0;b=1;var c = a+b;print c;")
        val tokens = scanner.scanTokens()
        assertEquals(25, tokens.size)
        val parser = Parser(tokens)
        val stmts = parser.parse()
        assertEquals(5, stmts.size)

        val interpreter = Interpreter { str: String -> assertEquals("100", str) }
        interpreter.interpret(stmts)
    }

    @Test
    fun testBlockScope() {
        val scanner = Scanner("var a = 10; { var a = 20; print a; } print a;")
        val tokens = scanner.scanTokens()
        val parser = Parser(tokens)
        val stmts = parser.parse()
        val interpreter = Interpreter { str: String ->
            when (str) {
                "20" -> {} 
                "10" -> {} 
                else -> fail("Unexpected print output: $str")
            }
        }
        interpreter.interpret(stmts)
    }

    @Test
    fun testIfElseStatement() {
        val scanner = Scanner("var a = 10; if (a < 5) { print 1; } else { print 2; }")
        val parser = Parser(scanner.scanTokens())
        val outputs = mutableListOf<String>()
        val interpreter = Interpreter { str: String ->
            outputs.add(str)
        }
        interpreter.interpret(parser.parse())
        assertEquals(listOf("2"), outputs)
    }

    @Test
    fun testIfStatement() {
        val scanner = Scanner("var a = 10; if (a > 5) { print 1; }")
        val parser = Parser(scanner.scanTokens())
        val outputs = mutableListOf<String>()
        val interpreter = Interpreter { str: String ->
            outputs.add(str)
        }
        interpreter.interpret(parser.parse())
        assertEquals(listOf("1"), outputs)
    }

    @Test
    fun testIfDoesntAlwaysEvaluateStatement() {
        val scanner = Scanner("var a = 0; if (a > 5) { print 1; }")
        val parser = Parser(scanner.scanTokens())
        val outputs = mutableListOf<String>()
        val interpreter = Interpreter { str: String ->
            outputs.add(str)
        }
        interpreter.interpret(parser.parse())
        assertEquals(0, outputs.size)
    }
}