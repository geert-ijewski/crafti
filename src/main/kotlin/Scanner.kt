import TokenType.*
import Token
import kotlin.collections.List
import kotlin.collections.ArrayList

class Scanner(
	val source: String
) {
	var tokens: MutableList<Token> = mutableListOf()
	var start: Int = 0
	var current: Int = 0
	var line: Int = 1

	fun scanTokens(): List<Token> {
		while (!isAtEnd()) {
			// skip whitespace and comments
			skipWhitespaceAndComments()
			if (isAtEnd()) break
			start = current
			scanToken()
		}
		tokens.add(Token(EOF, "", null, line))
		return tokens
	}

	private fun skipWhitespaceAndComments() {
		while (!isAtEnd()) {
			val c = peek()
			when {
				c.isWhitespace() -> advance()
				c == '/' && peekNext() == '/' -> {
					// skip comment
					advance() // skip first '/'
					advance() // skip second '/'
					while (peek() != '\n' && !isAtEnd()) advance()
				}
				else -> return
			}
		}
	}

	fun isAtEnd(): Boolean {
		return current >= source.length;
	}

	fun scanToken() {
		println("scanToken() " + current.toString() + '"' + peek() + '"')
		var c: Char = advance()

		// ...existing code...

		// todo reserved words ans identifers P53

		when(c) {
		'{' -> tokens.add(createToken(LEFT_BRACE));
		'}' -> tokens.add(createToken(RIGHT_BRACE));
		'(' -> tokens.add(createToken(LEFT_PAREN));
		')' -> tokens.add(createToken(RIGHT_PAREN));
		',' -> tokens.add(createToken(COMMA));
		'.' -> tokens.add(createToken(DOT));
		'+' -> tokens.add(createToken(PLUS));
		'-' -> tokens.add(createToken(MINUS));
		'=' -> if(match('=')) createToken(EQUAL_EQUAL) else tokens.add(createToken(EQUAL));
		in '0'..'9' -> tokens.add(createNumber());
		'/' -> {
			if(match('/')) {
				while(peek() != '\n' && !isAtEnd()) {
					advance();
				}
				// After skipping comment, scan next token on the same line
				if (!isAtEnd()) scanToken()
			} else {
				tokens.add(createToken(SLASH));
			}
		}
		else -> throw Error("unknown token: '" + c + '\'')
		}
	}

	fun createToken(type: TokenType): Token {
		return Token(type, source.substring(start, current), null, line)
	}

	// todo string P51

	fun createNumber(): Token {
		while(peek().isDigit()) {
			advance()
		}

		if(peek() == '.' && peekNext().isDigit()) {
			// consume .
			advance()
			while(peek().isDigit()) {
				advance()
			}
		}

		return Token(NUMBER, "", source.substring(start, current).toDouble(), line)
	}

	fun advance(): Char {
		val ret = source[current];
		current += 1;
		return ret
	}

	fun match(expected: Char): Boolean {
		if(isAtEnd()) return false;
		if(source[current] != expected) return false;

		advance();
		return true
	}

	fun error(line: Int, message: String) {
		println("[L" + line + "] Err: " + message)
	}

	fun peek(): Char {
		if(isAtEnd()) return Char.MIN_VALUE;
		return source[current]
	}

	fun peekNext(): Char {
		if(current + 1 >= source.length) return Char.MIN_VALUE;
		return source[current + 1]
	}
}
