class AstPrinter : Expr.Visitor<String> {
	fun print(expr: Expr) : String {
		return expr.accept(this)
	}

	override fun visitAssignExpr(expr : Expr.Assign) : String {
		return "assign " + expr.name + " " + expr.value;
	}

	override fun visitVariableExpr(expr : Expr.Variable) : String {
		return "var " + expr.name;
	}

	override fun visitBinaryExpr(expr : Expr.Binary) : String {
		return parenthesize(expr.operator.lexeme, expr.left, expr.right)
	}

	override fun visitGroupingExpr(expr : Expr.Grouping) : String {
		return parenthesize("group", expr.expression)
	}

	override fun visitLiteralExpr(expr : Expr.Literal) : String {
		if(expr.value == null)return "nil"

		return expr.value.toString()
	}

	override fun visitUnaryExpr(expr: Expr.Unary) : String {
		return parenthesize(expr.operator.lexeme, expr.right)
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
