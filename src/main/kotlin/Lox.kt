import java.io.File
import Scanner

class Lox() {
	  fun interpret(script: String): Any? {
		  val scanner = Scanner(script)
		  val tokens = scanner.scanTokens()

		  val parser = Parser(tokens)
		  val statements = parser.parse()
		  val interpreter = Interpreter { s: String -> println(s) }
		  return interpreter.interpret(statements)
	  }

	fun error(token: Token, message: String) {
		if(token.type == TokenType.EOF) {
			println(token.line.toString() + " at end" + message)
		} else {
			println(token.line.toString() + " at '" + token.lexeme + "'" + message)
		}
	}
}
