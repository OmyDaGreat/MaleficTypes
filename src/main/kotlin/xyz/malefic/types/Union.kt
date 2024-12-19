package xyz.malefic.types

import kotlin.reflect.KClass

/**
 * Represents a union of two types, A and B, where only one value can be held at a time.
 *
 * @param A The first type parameter.
 * @param B The second type parameter.
 * @property first The optional value of type A.
 * @property second The optional value of type B.
 *
 * @constructor Ensures that only one of the two values is non-null.
 */
class Union<A, B>(
    private val first: A? = null,
    private val second: B? = null,
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
     * Retrieves the value of type A from the union.
     *
     * @return The value of type A if present.
     * @throws IllegalStateException if no value of type A is present.
     */
    fun getFirst(): A = first ?: throw IllegalStateException("No value of type A present.")

    /**
     * Retrieves the value of type B from the union.
     *
     * @return The value of type B if present.
     * @throws IllegalStateException if no value of type B is present.
     */
    fun getSecond(): B = second ?: throw IllegalStateException("No value of type B present.")

    /**
     * Determines if the union holds a value of the specified type.
     *
     * @param type The KClass representing the type to check against.
     * @return true if the union contains a value of the specified type, false otherwise.
     */
    fun isType(type: KClass<*>): Boolean =
        when (type) {
            first?.javaClass?.kotlin -> isFirst()
            second?.javaClass?.kotlin -> isSecond()
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

        return when {
            isFirst() && other.isFirst() -> first == other.first
            isSecond() && other.isSecond() -> second == other.second
            else -> false
        }
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
            isFirst() -> "Union(first=${first?.javaClass?.simpleName}: $first)"
            isSecond() -> "Union(second=${second?.javaClass?.simpleName}: $second)"
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
                is A -> Union(first = value)
                is B -> Union(second = value)
                else -> throw IllegalArgumentException("Value must be of type A or B.")
            }
    }
}
