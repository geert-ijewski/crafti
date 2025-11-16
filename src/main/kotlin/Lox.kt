import java.io.File
import Scanner

class Lox() {
	   fun interpret(script: String): Any? {
			   val scanner = Scanner(script)
			   val tokens = scanner.scanTokens()

			   val parser = Parser(tokens)
			   val expr = parser.expression()
			   val interpreter = Interpreter()
			   val result = expr.accept(interpreter)
			   println(result)
			   return result
	   }

	fun error(token: Token, message: String) {
		if(token.type == TokenType.EOF) {
			println(token.line.toString() + " at end" + message)
		} else {
			println(token.line.toString() + " at '" + token.lexeme + "'" + message)
		}
	}
}
