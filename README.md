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
9. [License](#license)

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
    processUnion(Union.of("Hello")) // Output: Processing String: Hello
}
```

In these examples, `Union.of` is used directly within the function call, demonstrating how to create and use `Union` instances without storing them in variables first.

## License

This project is licensed under the terms of the [MIT License](LICENSE).
