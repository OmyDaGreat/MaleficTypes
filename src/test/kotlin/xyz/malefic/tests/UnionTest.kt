package xyz.malefic.tests

import xyz.malefic.types.Union
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class UnionTest {
    // Creating Union instance with first value using ofFirst() factory method
    @Test
    fun test_create_union_with_first_value() {
        val union = Union.ofFirst<String, Int>("test")

        assertTrue(union.isFirst())
        assertFalse(union.isSecond())
        assertEquals("test", union.getFirst())
    }

    // Attempting to create Union with both values null
    @Test
    fun test_create_union_with_both_values_null() {
        assertFailsWith(IllegalArgumentException::class) {
            Union<String, Int>(first = null, second = null)
        }
    }

    // Retrieving first value when present using getFirst()
    @Test
    fun test_get_first_value_when_present() {
        val union = Union.ofFirst<String, Int>("example")

        assertTrue(union.isFirst())
        assertFalse(union.isSecond())
        assertEquals("example", union.getFirst())
    }

    // Creating Union instance using of() factory method with value of type A
    @Test
    fun test_create_union_with_of_method_for_type_a() {
        val union = Union.of<String, Int>("test")

        assertTrue(union.isFirst())
        assertFalse(union.isSecond())
        assertEquals("test", union.getFirst())
    }

    // Retrieving second value when present using getSecond()
    @Test
    fun test_create_union_with_second_value() {
        val union = Union.ofSecond<String, Int>(42)

        assertFalse(union.isFirst())
        assertTrue(union.isSecond())
        assertEquals(42, union.getSecond())
    }

    // Checking if Union contains first value using isFirst()
    @Test
    fun test_union_is_first_when_initialized_with_first_value() {
        val union = Union.ofFirst<String, Int>("example")

        assertTrue(union.isFirst())
        assertFalse(union.isSecond())
        assertEquals("example", union.getFirst())
    }

    // Calling getFirst() when first value is null
    @Test
    fun test_get_first_when_first_is_null() {
        val union = Union.ofSecond<String, Int>(42)

        assertFalse(union.isFirst())
        assertTrue(union.isSecond())
        assertFailsWith(IllegalStateException::class) {
            union.getFirst()
        }
    }

    // Attempting to create Union with both values non-null
    @Test
    fun test_create_union_with_both_values_non_null() {
        val exception =
            assertFailsWith(IllegalArgumentException::class) {
                Union(first = "test", second = 123)
            }
        assertEquals("Either must hold exactly one value at a time.", exception.message)
    }

    // Calling getSecond() when second value is null
    @Test
    fun test_get_second_when_second_is_null() {
        val union = Union.ofFirst<String, Int>("test")

        assertFalse(union.isSecond())

        val exception =
            assertFailsWith(IllegalStateException::class) {
                union.getSecond()
            }

        assertEquals("No value of type B present.", exception.message)
    }

    // Checking if Union contains specific type using isType()
    @Test
    fun check_union_contains_specific_type() {
        val unionA = Union.ofFirst<String, Int>("test")
        val unionB = Union.ofSecond<String, Int>(42)

        assertTrue(unionA.isType(String::class))
        assertFalse(unionA.isType(Int::class))

        assertTrue(unionB.isType(Int::class))
        assertFalse(unionB.isType(String::class))
    }

    // Checking isType() with unrelated type class
    @Test
    fun test_is_type_with_unrelated_class() {
        val union = Union.ofFirst<String, Int>("test")

        assertFalse(union.isType(Double::class))
    }

    // Using of() factory method with value of invalid type
    @Test
    fun test_of_method_with_invalid_type() {
        try {
            Union.of<String, Int>(3.14)
            fail("Expected IllegalArgumentException to be thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Value must be of type A or B.", e.message)
        }
    }

    // Using Union with nullable type parameters
    @Test
    fun test_create_union_with_nullable_first_value() {
        assertFailsWith(IllegalArgumentException::class) {
            Union.ofFirst<String?, Int>(null)
        }
    }

    // Verifying type erasure behavior with generic type parameters
    @Test
    fun verify_type_erasure_with_generic_parameters() {
        val union: Union<String, Int> = Union.of("test")

        assertTrue(union.isFirst())
        assertFalse(union.isSecond())
        assertEquals("test", union.getFirst())

        val unionInt = Union.of<String, Int>(42)

        assertFalse(unionInt.isFirst())
        assertTrue(unionInt.isSecond())
        assertEquals(42, unionInt.getSecond())

        assertFailsWith(IllegalArgumentException::class) {
            Union.of<String, Int>(3.14)
        }
    }

    // Using Union with primitive types as type parameters
    @Test
    fun test_create_union_with_primitive_types() {
        val unionInt = Union.ofFirst<Int, String>(42)
        assertTrue(unionInt.isFirst())
        assertFalse(unionInt.isSecond())
        assertEquals(42, unionInt.getFirst())

        val unionString = Union.ofSecond<Int, String>("hello")
        assertFalse(unionString.isFirst())
        assertTrue(unionString.isSecond())
        assertEquals("hello", unionString.getSecond())
    }

    // Using Union with custom class types as type parameters
    @Test
    fun test_create_union_with_custom_class_types() {
        class CustomTypeA(
            val data: String,
        )

        class CustomTypeB(
            val number: Int,
        )

        val unionA = Union.ofFirst<CustomTypeA, CustomTypeB>(CustomTypeA("data"))

        assertTrue(unionA.isFirst())
        assertFalse(unionA.isSecond())
        assertEquals("data", unionA.getFirst().data)

        val unionB = Union.ofSecond<CustomTypeA, CustomTypeB>(CustomTypeB(42))

        assertFalse(unionB.isFirst())
        assertTrue(unionB.isSecond())
        assertEquals(42, unionB.getSecond().number)
    }

    // Testing equality and hash code implementations
    @Test
    fun test_union_equality_and_hash_code() {
        val union1 = Union.ofFirst<String, Int>("test")
        val union2 = Union.ofFirst<String, Int>("test")
        val union3 = Union.ofSecond<String, Int>(42)

        assertEquals(union1, union2)
        assertNotEquals(union1, union3)
        assertEquals(union1.hashCode(), union2.hashCode())
        assertNotEquals(union1.hashCode(), union3.hashCode())
    }
}
