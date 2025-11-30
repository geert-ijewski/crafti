import TokenType.*
import Expr

class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Any?> {
    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return expr.expression.accept(this)
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = expr.right.accept(this)
        return when (expr.operator.type) {
            TokenType.MINUS -> -(right as Double)
            TokenType.BANG -> !(right as Boolean)
            else -> throw UnsupportedOperationException("Unsupported unary operator: ${expr.operator.type}")
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = expr.left.accept(this) as Double
        val right = expr.right.accept(this) as Double
        return when (expr.operator.type) {
            TokenType.PLUS -> left + right
            TokenType.MINUS -> left - right
            TokenType.STAR -> left * right
            TokenType.SLASH -> left / right
            TokenType.GREATER -> left > right
            TokenType.LESSER -> left < right
            TokenType.EQUAL_EQUAL -> left == right
            TokenType.BANG_EQUAL -> left != right
            else -> throw UnsupportedOperationException("Unsupported binary operator: ${expr.operator.type}")
        }
    }
 
	override fun visitExpressionStmt(stmt : Stmt.Expression ) : Any? {
        evaluate(stmt.expression)
        return null
	}

	override fun visitPrintStmt(stmt : Stmt.Print )  : Any?{
        val value = evaluate(stmt.expression)
        println(stringify(value))
        return null
	}


    fun interpret(statements : List<Stmt>) {
        try {
            for(stmt in statements) {
                execute(stmt)
            }
        } catch(e : RuntimeError) {
            println(e.message)
        }
    }

    fun execute(stmt : Stmt) {
        stmt.accept(this);
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"
        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }
        return obj.toString()
    }

    class RuntimeError(message: String) : RuntimeException(message)
}
