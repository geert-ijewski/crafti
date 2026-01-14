import TokenType.*
import Expr

class Interpreter(val printFunction: (String) -> Unit) : Expr.Visitor<Any?>, Stmt.Visitor<Any?> {
    var enviroment = Enviroment()
 
    fun interpret(statements : List<Stmt?>) {
        try {
            for(stmt in statements) {
                if(stmt == null) continue

                execute(stmt)
            }
        } catch(e : RuntimeException) {
            println(e.message)
        }
    }

    override fun visitVarStmt(stmt : Stmt.Var) : Any? {
        val value = if(stmt.initializer != null) {
            evaluate(stmt.initializer)
        } else {
            null
        }

        enviroment.define(stmt.name.lexeme, value)
        return null
    }

 
	override fun visitVariableExpr(expr : Expr.Variable) : Any? {
        return enviroment.get(expr.name.lexeme)
    }

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
        printFunction(stringify(value))
        return null
	}

    override fun visitIfStmt(stmt: Stmt.If): Any? {
        val condition = evaluate(stmt.condition) as Boolean
        if (condition) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }
        return null
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

    override fun visitAssignExpr(expr : Expr.Assign) : Any? {
        val value = evaluate(expr.value)
        enviroment.assign(expr.name.lexeme, value)
        return value
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Any? {
        val previousEnv = enviroment
        val blockEnv = Enviroment(previousEnv)
        try {
            for (statement in stmt.statements) {
                execute(statement)
            }
        } finally {
            enviroment = previousEnv
        }
        return null
    }

    override fun visitWhileStmt(expr: Stmt.While): Any? {
        while (evaluate(expr.condition) as Boolean) {
            execute(expr.body)
        }
        return null
    }
}
