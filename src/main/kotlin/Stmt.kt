import kotlin.collections.List

abstract class Stmt {
	interface Visitor<out R> {
		fun visitExpressionStmt(stmt : Expression) : R;
		fun visitPrintStmt(stmt : Print) : R;
}


	// generic function for double dispatch
	abstract fun <R> accept(visitor: Visitor<R>): R

	class Expression(
		val expression : Expr,
	) : Stmt(){

		override fun <R> accept(visitor: Visitor<R>): R {
			return visitor.visitExpressionStmt(this);
}
		}

	class Print(
		val expression : Expr,
	) : Stmt(){

		override fun <R> accept(visitor: Visitor<R>): R {
			return visitor.visitPrintStmt(this);
}
		}

}
