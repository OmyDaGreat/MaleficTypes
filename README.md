# MaleficTypes

## Table of Contents
1. [Introduction](#introduction)
2. [Union Class Overview](#union-class-overview)
3. [Creating Union Instances](#creating-union-instances)
   - [Using `ofFirst`](#using-offirst)
   - [Using `ofSecond`](#using-ofsecond)
   - [Using `of`](#using-of)
4. [Checking the Held Value Type](#checking-the-held-value-type)
5. [Retrieving Values](#retrieving-values)
6. [Equality and Hashing](#equality-and-hashing)
7. [String Representation](#string-representation)
8. [Examples](#examples)
   - [Basic Usage](#basic-usage)
   - [Using Union in Functions](#using-union-in-functions)
   - [Union Overloads](#using-the-annotation-and-plugin-for-union-overloads)
9. [The Annotation And Plugin](#the-annotation-and-plugin)
   - [Add the Plugin](#1-add-the-plugin-and-dependencies)
   - [Annotate Your Functions](#2-annotate-your-functions)
   - [Generated Overloads](#3-generated-overloads)
   - [Testing the Overloads](#4-testing-the-overloads)
   - [Benefits of Using the Plugin](#benefits-of-using-the-plugin)
10. [License](#license)

## Introduction

The `MaleficTypes` library provides a `Union` class that allows you to create a union type similar to those in JavaScript. A union type can hold a value of either type `A` or type `B`, but not both simultaneously. This class provides utility functions to manage and interact with the values it holds.

## Union Class Overview

The `Union` class is a generic class defined as `Union<A, B>`, where `A` and `B` are the types of values it can hold. It ensures that only one of the types is non-null at any time.

```kotlin
class Union<A, B> internal constructor(
    private val first: A? = null,
    private val second: B? = null,
) {
    init {
        require((first == null) xor (second == null)) { "Either must hold exactly one value at a time." }
    }
    // Additional methods...
}
```

## Creating Union Instances

### Using `ofFirst`

To create a `Union` instance holding a value of type `A`, use the `ofFirst` function:

```kotlin
val unionA: Union<Int, String> = Union.ofFirst(42)
```

### Using `ofSecond`

To create a `Union` instance holding a value of type `B`, use the `ofSecond` function:

```kotlin
val unionB: Union<Int, String> = Union.ofSecond("Hello")
```

### Using `of`

To create a `Union` instance from a value of either type `A` or `B`, use the `of` function. This function uses Kotlin's reified type parameters to determine the type of the value at runtime:

```kotlin
val unionA: Union<Int, String> = Union.of(42)
val unionB: Union = Union.of<Int, String>("Hello")
```

## Checking the Held Value Type

You can check which type of value the `Union` holds using the `isFirst` and `isSecond` methods:

```kotlin
if (unionA.isFirst()) {
    println("Union holds a value of type A")
}

if (unionB.isSecond()) {
    println("Union holds a value of type B")
}
```

Additionally, you can use the `isType` method to check against a specific type:

```kotlin
if (unionA.isType(Int::class)) {
    println("Union holds an Int")
}
```

## Retrieving Values

To retrieve the value from the `Union`, use the `getFirst` or `getSecond` methods. These methods will throw an `IllegalStateException` if the value of the requested type is not present:

```kotlin
val valueA: Int = unionA.getFirst()
val valueB: String = unionB.getSecond()
```

## Equality and Hashing

The `Union` class overrides the `equals` and `hashCode` methods to provide meaningful equality checks and hash codes based on the held value:

```kotlin
val anotherUnionA: Union<Int, String> = Union.ofFirst(42)
println(unionA == anotherUnionA) // true

println(unionA.hashCode()) // Hash code based on the value 42
```

## String Representation

The `Union` class provides a `toString` method that indicates which type the `Union` holds and its value:

```kotlin
println(unionA.toString()) // Union(first=Integer: 42)
println(unionB.toString()) // Union(second=String: Hello)
```

## Examples

### Basic Usage

Here are some examples demonstrating the basic usage of the `Union` class:

```kotlin
fun main() {
    val union1: Union<Int, String> = Union.ofFirst(100)
    val union2: Union<Int, String> = Union.ofSecond("Kotlin")

    if (union1.isFirst()) {
        println("Union1 holds an Int: ${union1.getFirst()}")
    }

    if (union2.isSecond()) {
        println("Union2 holds a String: ${union2.getSecond()}")
    }

    println(union1) // Output: Union(first=Integer: 100)
    println(union2) // Output: Union(second=String: Kotlin)
}
```

### Using Union in Functions

You can use `Union` types as parameters in functions to handle multiple types flexibly. Here are examples using the `of` method directly in function calls:

```kotlin
fun processUnion(union: Union<Int, String>) {
    when {
        union.isFirst() -> println("Processing Int: ${union.getFirst()}")
        union.isSecond() -> println("Processing String: ${union.getSecond()}")
    }
}

fun main() {
    processUnion(Union.of(10))    // Output: Processing Int: 10
    processUnion("Hello".toUnion()) // Output: Processing String: Hello
}
```

In these examples, `Union.of` is used directly within the function call, demonstrating how to create and use `Union` instances without storing them in variables first. You can do the same with the `.toUnion()` function, converting any type to a union.

### Using the Annotation and Plugin for Union Overloads

The `MaleficTypes` library provides an additional tool to simplify the use of `Union` types in your functions: the `@UnionOverload` annotation. When used with the `xyz.malefic.types` plugin, this annotation automatically generates overloaded versions of your functions for all possible combinations of types in your `Union` parameters.

This feature is especially useful for library authors who want to expose functions with flexible parameter types without requiring users to interact directly with the `Union` API.

## The Annotation and Plugin

### 1. Add the Plugin and Dependencies

In your `build.gradle.kts`, apply the `com.google.devtools.ksp` and `xyz.malefic.types` plugins:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "..." //Choose version based on your Kotlin version but make sure to instantiate before the plugin
    id("xyz.malefic.types") version "2.0.1" //Automatically applies the xyz.malefic:types library and xyz.malefic:types-processor through ksp
}
```

### 2. Annotate Your Functions

Add the `@UnionOverload` annotation to your functions that use `Union` types as parameters. This will signal the processor to generate overloads for all type combinations.

#### Example 1: Single Parameter with `Union`

```kotlin
@UnionOverload
fun processSingle(value: Union<String, Int>): String =
    when {
        value.isFirst() -> "First: ${value.getFirst()}"
        value.isSecond() -> "Second: ${value.getSecond()}"
        else -> "Invalid"
    }
```

#### Example 2: Multiple Parameters with `Union`

```kotlin
@UnionOverload
fun processMultiple(
    name: String,
    value: Union<String, Int>,
    scale: Union<Float, Double>,
): String =
    "Name: $name, Value: ${if (value.isFirst()) value.getFirst() else value.getSecond()}, Scale: ${if (scale.isFirst()) {
        scale.getFirst()
    } else {
        scale.getSecond()
    }}"
```

### 3. Generated Overloads

The plugin will generate overloads for every combination of types in your `Union` parameters. For the `processMultiple` example, it would create:

```kotlin
fun processMultiple(name: String, value: String, scale: Float) = 
    processMultiple(name, Union.ofFirst(value), Union.ofFirst(scale))

fun processMultiple(name: String, value: Int, scale: Double) = 
    processMultiple(name, Union.ofSecond(value), Union.ofSecond(scale))

fun processMultiple(name: String, value: String, scale: Double) = 
    processMultiple(name, Union.ofFirst(value), Union.ofSecond(scale))

fun processMultiple(name: String, value: Int, scale: Float) = 
    processMultiple(name, Union.ofSecond(value), Union.ofFirst(scale))
```

This allows users to call the function with any of these combinations without explicitly creating `Union` instances or even having the library.

### 4. Testing the Overloads

To verify the correctness of generated overloads, you can write tests like this:

```kotlin
@Test
fun `test single Union parameter overloads`() {
    val result1 = processSingle("Hello") // Calls Union.ofFirst<String, Int>("Hello")
    val result2 = processSingle(42) // Calls Union.ofSecond<String, Int>(42)

    assertEquals("First: Hello", result1)
    assertEquals("Second: 42", result2)
}

@Test
fun `test multiple Union parameter overloads`() {
    val result1 = processMultiple("Test", "StringValue", 1.5f) // Union.ofFirst for both
    val result2 = processMultiple("Test", 42, 2.0) // Union.ofSecond for both
    val result3 = processMultiple("Test", "StringValue", 2.0) // Mixed Union.ofFirst and Union.ofSecond
    val result4 = processMultiple("Test", 42, 1.5f) // Mixed Union.ofSecond and Union.ofFirst

    assertEquals("Name: Test, Value: StringValue, Scale: 1.5", result1)
    assertEquals("Name: Test, Value: 42, Scale: 2.0", result2)
    assertEquals("Name: Test, Value: StringValue, Scale: 2.0", result3)
    assertEquals("Name: Test, Value: 42, Scale: 1.5", result4)
}
```

### Benefits of Using the Plugin

- **Simplified Function Calls**: Users can call your functions directly with standard types (`String`, `Int`, etc.) without interacting with `Union` APIs.
- **Cleaner Library APIs**: Reduces boilerplate code and enhances usability for consumers of your library.
- **Flexibility**: Supports functions with any number of parameters and mixed `Union` and non-`Union` types.

## License

This project is licensed under the terms of the [MIT License](LICENSE).
