package xyz.malefic.types

/**
 * Annotation to indicate that a function should have overloads generated
 * for its parameters of type [Union]. This is processed at the source level
 * and is intended to simplify function calls by allowing direct use of
 * parameter types without manually wrapping them in [Union] instances.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class UnionOverload
