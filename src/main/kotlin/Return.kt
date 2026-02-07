
class Return(val value: Any?) : RuntimeException(value.toString()) {
}