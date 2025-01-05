package xyz.malefic.types

/**
 * Converts the current object to a [Union] type containing either type [A] or [B].
 * Utilizes reified type parameters to determine the type at runtime.
 *
 * @param A The first type parameter for the [Union].
 * @param B The second type parameter for the [Union].
 * @return A [Union] instance containing the current object as either type [A] or [B].
 * @throws IllegalArgumentException if the object is not of type [A] or [B].
 */
inline fun <reified A, reified B> Any.toUnion(): Union<A, B> = Union.of(this)
