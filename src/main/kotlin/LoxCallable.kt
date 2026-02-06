
interface LoxCallable {
    fun call(Interpreter: Interpreter, arguments: List<Any?>) : Any?
    fun arity() : Int
}