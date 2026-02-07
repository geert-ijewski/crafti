
class LoxFunction(val declaration: Stmt.Function) : LoxCallable {

    override fun call(interpreter: Interpreter, arguments: List<Any?>) : Any? {
        val enviroment = Enviroment(interpreter.globals)
        for(i in 0..arguments.size-1) {
            enviroment.define(declaration.params[i].lexeme, arguments[i])
        }
        try {
            interpreter.execute(declaration.body, enviroment)
        } catch (returnValue: Return) {
            return returnValue.value 
        }
        return null
    }

    override fun arity(): Int { return declaration.params.size }

}