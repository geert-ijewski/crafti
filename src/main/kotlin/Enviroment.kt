class Enviroment(val enclosing: Enviroment? = null) {
	private val values: MutableMap<String, Any?> = mutableMapOf()

	/**
	 * Setter: store a value for [name].
	 */
	fun define(name: String, value: Any?) {
		values[name] = value
	}

	fun assign(name: String, value: Any?) {
		if(values.containsKey(name)) {
			values[name] = value
			return
		}
		if(this.enclosing != null) {
			this.enclosing.assign(name, value)
			return
		}

		throw RuntimeException("Undefined variable: '$name'")
	}

	/**
	 * Getter: return the stored value for [name].
	 * Throws RuntimeException if the key does not exist.
	 */
	fun get(name: String): Any? {
		if (values.containsKey(name)) {
			return values[name]
		}
		if(this.enclosing != null) {
			return this.enclosing.get(name)
		}
		throw RuntimeException("Undefined variable: '$name'")
	}

}