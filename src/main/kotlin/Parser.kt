import TokenType.*
import Token

class Parser(val tokens : List<Token>) {
	class ParseError() : RuntimeException() {}
	var current : Int = 0;

	fun parse(): List<Stmt?> {
		val stmts: MutableList<Stmt?> = mutableListOf()
		while(!isAtEnd()) {
			stmts.add(declaration())
		}

		return stmts
	}

	fun assignment() : Expr {
		val expr = equality()

		if(match(EQUAL)) {
			val equals = previous()
			val value = assignment()

			if(expr is Expr.Variable) {
				val name = expr.name
				return Expr.Assign(name, value)
			}

			error(equals, "invalid assignment target")
		}

		return expr
	}

	fun declaration() : Stmt? {
		try {
			return if(match(VAR)) {
				varDeclaration();
			} else {
				statment();
			}
		} catch(e : RuntimeException) {
			synchronize();
			return null;
		}
	}

	fun varDeclaration() : Stmt {
		val name = consume(IDENTIFIER, "expected variable name")

		var initalizer: Expr? = if(match(EQUAL)) {
			expression();
		} else {
			null
		}

		consume(SEMICOLON, "expect ';' after variable declaration");
		return Stmt.Var(name, initalizer)
	}

	fun statment() : Stmt {
		if(match(IF)) { return ifStatment(); }
		if(match(PRINT)) return printStatment();
		if(match(WHILE)) {
			consume(LEFT_PAREN, "expect '(' after 'while'")
			val condition = expression()
			consume(RIGHT_PAREN, "expect ')' after condition")
			val body = statment()
			return Stmt.While(condition, body)
		}
		if(match(LEFT_BRACE)) {
			val statements: MutableList<Stmt> = mutableListOf()
			while(!check(RIGHT_BRACE) && !isAtEnd()) {
				statements.add(declaration()!!)
			}
			consume(RIGHT_BRACE, "expect '}' after block")
			return Stmt.Block(statements)
		}

		return expressionStatment();
	}

	fun ifStatment() : Stmt {
		consume(LEFT_PAREN, "expect '(' after 'if'")
		val condition = expression()
		consume(RIGHT_PAREN, "expect ')' after if condition")

		val thenBranch = statment()
		var elseBranch: Stmt? = null
		if(match(ELSE)) {
			elseBranch = statment()
		}

		return Stmt.If(condition, thenBranch, elseBranch)
	}

	fun printStatment() : Stmt {
		val value = expression();
		consume(SEMICOLON, "expected ; after value");
		return Stmt.Print(value);
	}

	fun expressionStatment() : Stmt {
		val expr = expression();
		consume(SEMICOLON, "expected ; after value");
		return Stmt.Expression(expr);
	}

	fun expression() : Expr {
		return assignment()
	}

	fun equality() : Expr {
		var expr: Expr = comparison()

		while(match(BANG_EQUAL, EQUAL_EQUAL)) {
			val operator = previous()
			val right = comparison()

			expr = Expr.Binary(expr, operator, right)
		}

		return expr
	}

	fun comparison() : Expr {
		var expr: Expr = term()

		while(match(TokenType.GREATER, TokenType.LESSER)) {
			val operator = previous()
			val right = term()

			expr = Expr.Binary(expr, operator, right)
		}

		return expr
	}

	fun term() : Expr {
		var expr: Expr = factor()

		while(match(TokenType.MINUS, TokenType.PLUS)) {
			val operator = previous()
			val right = factor()

			expr = Expr.Binary(expr, operator, right)
		}

		return expr
	}

	fun factor() : Expr {
		var expr: Expr = unary()

		while(match(TokenType.SLASH, TokenType.STAR)) {
			val operator = previous()
			val right = unary()

			expr = Expr.Binary(expr, operator, right)
		}

		return expr
	}

	fun unary() : Expr {
		if(match(TokenType.BANG, TokenType.MINUS)) {
			val operator = previous()
			val right = unary()

			 return Expr.Unary(operator, right)
		}

		return primary()
	}

	fun primary(): Expr {
		if(match(TokenType.FALSE)) return Expr.Literal(false)
		if(match(TokenType.TRUE)) return Expr.Literal(true)
		if(match(TokenType.NIL)) return Expr.Literal(null)

		if(match(TokenType.NUMBER, TokenType.STRING)) {
			return Expr.Literal(previous().literal)
		}

		if(match(TokenType.LEFT_PAREN)) {
			val expr = expression()
			consume(TokenType.RIGHT_PAREN, "expect ) after exprrssion");
			return Expr.Grouping(expr)
		}

		if(match(TokenType.IDENTIFIER)) {
			return Expr.Variable(previous())
		}

		throw error(peek(), "primary didn't match current token")
	}

	fun consume(type: TokenType, message : String) : Token {
		if(check(type)) return advance()

		throw error(peek(), message)
	}


	fun match(vararg types: TokenType) : Boolean {
		for(type in types) {
			if(check(type)) {
				advance()
				return true
			}
		}

		return false
	}

	fun check(token: TokenType) : Boolean {
		if(isAtEnd()) return false
		return peek().type == token
	}

	fun advance() : Token {
		if(!isAtEnd()) current++
		return previous()
	}

	fun isAtEnd(): Boolean {
		return peek().type == EOF
	}

	fun peek() : Token { return this.tokens.get(current) }

	fun previous() : Token {return this.tokens[current - 1] }

	fun error(token: Token, message: String) : ParseError {
		println(token.toString() + message)
		return ParseError()
	}

	fun synchronize() {
		advance();
		while(!isAtEnd()) {
			if(previous().type == SEMICOLON) return

			if(match(CLASS, FOR, FUNCTION, IF, PRINT, RETURN, VAR, WHILE)) return

			advance()
		}
	}
}
