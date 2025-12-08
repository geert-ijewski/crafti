class AstPrinter : Expr.Visitor<String> {
	fun print(expr: Expr) : String {
		return expr.accept(this)
	}

	override fun visitBinaryExpr(expr : Expr.Binary) : String {
		return parenthesize(expr.operator.lexeme, expr.left, expr.right)
	}

	override fun visitGroupingExpr(expr : Expr.Grouping) : String {
		return "" // todo parenthesize("group", expr.expression)
	}

	override fun visitLiteralExpr(expr : Expr.Literal) : String {
		if(expr.value == null)return "nil"

		return expr.value.toString()
	}

	override fun visitUnaryExpr(expr: Expr.Unary) : String {
		return "" // todo parenthesize(expr.operator.lexeme, expr.right)
	}

	fun parenthesize(name : String, vararg exprs : Expr) : String {
		var ret: String = "(" + name

		for(expr in exprs) {
			ret += " " + expr.accept(this)
		}

		ret += ")"
		return ret
	}
}
