package xyz.malefic.types

import kotlin.reflect.KClass

/**
 * Represents a union type that can hold a value of either type A or type B, but not both.
 * Provides utility functions to check the type of the held value, retrieve it, and create
 * instances of the union. Ensures that only one of the types is non-null at any time.
 *
 * @param A The first type parameter.
 * @param B The second type parameter.
 */
class Union<A, B> internal constructor(
    val first: A? = null,
    val second: B? = null,
) {
    init {
        require((first == null) xor (second == null)) { "Either must hold exactly one value at a time." }
    }

    /**
     * Checks if the union holds a value of type A.
     *
     * @return true if the union contains a value of type A, false otherwise.
     */
    fun isFirst(): Boolean = first != null

    /**
     * Checks if the union holds a value of type B.
     *
     * @return true if the union contains a value of type B, false otherwise.
     */
    fun isSecond(): Boolean = second != null

    /**
     * Retrieves the non-null value held by the union.
     *
     * @return The value of either type A or type B.
     */
    fun get() = (first ?: second)!!

    /**
     * Determines if the union holds a value of the specified type.
     *
     * @param type The KClass representing the type to check against.
     * @return true if the union contains a value of the specified type, false otherwise.
     */
    fun isType(type: KClass<*>): Boolean =
        when {
            isFirst() && type == first!!::class -> isFirst()
            isSecond() && type == second!!::class -> isSecond()
            else -> false
        }

    /**
     * Compares this Union instance with another object for equality.
     *
     * @param other The object to compare with this instance.
     * @return true if the other object is a Union of the same type and holds an equal value,
     *         false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Union<*, *>) return false

        return get() == other.get()
    }

    /**
     * Returns the hash code of the union, based on the non-null value it holds.
     * If the union holds a value of type A, its hash code is returned.
     * If the union holds a value of type B, its hash code is returned.
     * If neither value is present, returns 0.
     *
     * @return The hash code of the non-null value or 0 if both are null.
     */
    override fun hashCode(): Int = first?.hashCode() ?: second?.hashCode() ?: 0

    /**
     * Returns a string representation of the Union instance.
     * Indicates which type the Union holds and its value.
     *
     * @return A string representation of the Union.
     */
    override fun toString(): String =
        when {
            isFirst() -> "Union(${first!!::class.simpleName}: $first)"
            isSecond() -> "Union(${second!!::class.simpleName}: $second)"
            else -> "Union(empty)"
        }

    /**
     * Companion object for providing static members and utility functions.
     */
    companion object {
        /**
         * Creates a Union instance holding a value of type A.
         *
         * @param value The value of type A to be held by the Union.
         * @return A Union instance containing the specified value of type A.
         */
        fun <A, B> ofFirst(value: A): Union<A, B> = Union(first = value)

        /**
         * Creates a Union instance holding a value of type B.
         *
         * @param value The value of type B to be held by the Union.
         * @return A Union instance containing the specified value of type B.
         */
        fun <A, B> ofSecond(value: B): Union<A, B> = Union(second = value)

        /**
         * Creates a Union instance from a value of either type A or B.
         *
         * @param value The value to be held by the Union.
         * @return A Union instance containing the specified value.
         * @throws IllegalArgumentException if the value is not of type A or B.
         */
        inline fun <reified A, reified B> of(value: Any): Union<A, B> =
            when (value) {
                is A -> ofFirst(value)
                is B -> ofSecond(value)
                else -> throw IllegalArgumentException("Value must be of type A or B.")
            }
    }
}
