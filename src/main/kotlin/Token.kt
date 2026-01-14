enum class TokenType {
	LEFT_PAREN, RIGHT_PAREN,
	LEFT_BRACE, RIGHT_BRACE,
	COMMA, DOT,
	MINUS, PLUS, STAR, SLASH,
	SEMICOLON,

	BANG, BANG_EQUAL, EQUAL,
	EQUAL_EQUAL, LESS, LESS_EQUAL,

	GREATER, LESSER,

	IDENTIFIER, STRING, NUMBER,
	FALSE, TRUE, NIL,

	AND, OR, FOR, IF, ELSE, CLASS,
	WHILE, FUNCTION, VAR, PRINT, RETURN,

	EOF
}

data class Token(val type: TokenType, val lexeme: String, val literal: Any?, val line: Int)
