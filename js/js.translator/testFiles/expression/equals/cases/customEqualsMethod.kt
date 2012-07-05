package foo

class Foo(val name: String) {
    public fun equals(that: Any?): Boolean {
        if (that !is Foo) {
            return false
        }
        return this.name == that.name
    }
}

fun box() : Boolean {
    val a = Foo("abc")
    val b = Foo("abc")
    val c = Foo("def")

    if (a != b) return false
    if (a == c) return false

    return true
}