import TokenType.*
import Expr

class Interpreter : Expr.Visitor<Any?> {
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
}
