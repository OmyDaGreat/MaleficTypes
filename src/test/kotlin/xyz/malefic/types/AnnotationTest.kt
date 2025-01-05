package xyz.malefic.types

import kotlin.test.Test
import kotlin.test.assertEquals

// Original function definitions
@UnionOverload
fun processSingle(value: Union<String, Int>): String =
    when {
        value.isFirst() -> "First: ${value.getFirst()}"
        value.isSecond() -> "Second: ${value.getSecond()}"
        else -> "Invalid"
    }

@UnionOverload
fun processMultiple(
    name: String,
    value: Union<String, Int>,
    scale: Union<Float, Double>,
): String =
    "Name: $name, Value: ${if (value.isFirst()) value.getFirst() else value.getSecond()}, Scale: ${if (scale.isFirst()) {
        scale.getFirst()
    } else {
        scale
            .getSecond()
    }}"

// Generated overloads should be tested
class UnionOverloadTests {
    @Test
    fun `test single Union parameter overloads`() {
        // Generated overloads for `processSingle`
        val result1 = processSingle("Hello") // Should call Union.ofFirst<String, Int>("Hello")
        val result2 = processSingle(42) // Should call Union.ofSecond<String, Int>(42)

        assertEquals("First: Hello", result1)
        assertEquals("Second: 42", result2)
    }

    @Test
    fun `test multiple Union parameter overloads`() {
        // Generated overloads for `processMultiple`
        val result1 = processMultiple("Test", "StringValue", 1.5f) // Union.ofFirst for both
        val result2 = processMultiple("Test", 42, 2.0) // Union.ofSecond for both
        val result3 = processMultiple("Test", "StringValue", 2.0) // Mixed Union.ofFirst and Union.ofSecond
        val result4 = processMultiple("Test", 42, 1.5f) // Mixed Union.ofSecond and Union.ofFirst

        assertEquals("Name: Test, Value: StringValue, Scale: 1.5", result1)
        assertEquals("Name: Test, Value: 42, Scale: 2.0", result2)
        assertEquals("Name: Test, Value: StringValue, Scale: 2.0", result3)
        assertEquals("Name: Test, Value: 42, Scale: 1.5", result4)
    }
}
