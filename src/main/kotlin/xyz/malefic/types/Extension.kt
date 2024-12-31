package xyz.malefic.types

inline fun <reified A, reified B> Any.toUnion(): Union<A, B> = Union.of(this)
