package xyz.malefic.types

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate

private const val UNION_QUALIFIED = "xyz.malefic.types.Union"

class UnionOverloadProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("xyz.malefic.types.UnionOverload")
        val invalidSymbols = symbols.filterNot { it is KSFunctionDeclaration && it.validate() }

        symbols.filterIsInstance<KSFunctionDeclaration>().forEach { function ->
            processFunction(function)
        }

        return invalidSymbols.toList()
    }

    private fun processFunction(function: KSFunctionDeclaration) {
        val packageName = function.packageName.asString()
        val functionName = function.simpleName.asString()
        val containingClass = function.parentDeclaration?.simpleName?.asString()
        val extensionReceiver = function.extensionReceiver?.resolve()
        val isInfix = function.modifiers.contains(Modifier.INFIX)

        // Identify parameters
        val parameters = function.parameters
        val unionParameters =
            parameters.filter {
                val type = it.type.resolve()
                type.declaration.qualifiedName?.asString() == UNION_QUALIFIED
            }

        if (unionParameters.isEmpty()) return // Skip if no Union parameters

        val fileBuilder = StringBuilder()
        fileBuilder.append("package $packageName\n\n")
        fileBuilder.append("import $UNION_QUALIFIED\n\n")

        // Generate overloaded functions
        val overloads = generateOverloads(functionName, parameters, unionParameters, containingClass, extensionReceiver, isInfix)
        fileBuilder.append(overloads)

        val fileName = "${functionName}Overloads"
        codeGenerator
            .createNewFile(
                Dependencies(false, function.containingFile!!),
                packageName,
                fileName,
            ).writer()
            .use { writer ->
                writer.write(fileBuilder.toString())
            }
    }

    private fun generateOverloads(
        functionName: String,
        parameters: List<KSValueParameter>,
        unionParameters: List<KSValueParameter>,
        containingClass: String?,
        extensionReceiver: KSType?,
        isInfix: Boolean,
    ): String {
        val overloads = StringBuilder()

        // Generate all combinations of Union parameter types
        val typeCombinations =
            unionParameters.map { unionParam ->
                val typeA =
                    unionParam.type
                        .resolve()
                        .arguments[0]
                        .type!!
                        .resolve()
                val typeB =
                    unionParam.type
                        .resolve()
                        .arguments[1]
                        .type!!
                        .resolve()
                listOf(typeA, typeB)
            }

        val combinations = cartesianProduct(typeCombinations)

        for (combination in combinations) {
            val parameterList =
                parameters.map { param ->
                    val replacement =
                        unionParameters.find { it == param }?.let { unionParam ->
                            val index = unionParameters.indexOf(unionParam)
                            combination[index]
                        }
                    replacement?.declaration?.qualifiedName?.asString()
                        ?: param.type
                            .resolve()
                            .declaration.qualifiedName!!
                            .asString()
                }

            val argumentList =
                parameters.map { param ->
                    val replacement =
                        unionParameters.find { it == param }?.let { unionParam ->
                            val index = unionParameters.indexOf(unionParam)
                            if (combination[index] == typeCombinations[index][0]) {
                                "Union.ofFirst(${param.name!!.asString()})"
                            } else {
                                "Union.ofSecond(${param.name!!.asString()})"
                            }
                        }
                    replacement ?: param.name!!.asString()
                }

            val parameterDefinitions =
                parameters
                    .mapIndexed { index, param ->
                        "${param.name!!.asString()}: ${parameterList[index]}"
                    }.joinToString(", ")

            if (isInfix && parameters.size == 1) {
                // Handle infix function with a single parameter
                overloads.append("infix fun ")
                extensionReceiver?.let { receiver ->
                    overloads.append("${receiver.declaration.qualifiedName!!.asString()}.")
                }
                overloads.append("${containingClass?.let { "$it." } ?: ""}$functionName($parameterDefinitions) = ")
                extensionReceiver?.let { overloads.append("this ") }
                overloads.append("$functionName ${argumentList[0]}\n\n")
            } else {
                // Handle regular or extension function
                overloads.append("fun ")
                extensionReceiver?.let { receiver ->
                    overloads.append("${receiver.declaration.qualifiedName!!.asString()}.")
                }
                overloads.append("${containingClass?.let { "$it." } ?: ""}$functionName($parameterDefinitions) = ")
                overloads.append("$functionName(${argumentList.joinToString(", ")})\n\n")
            }
        }

        return overloads.toString()
    }

    private fun <T> cartesianProduct(lists: List<List<T>>): List<List<T>> =
        lists.fold(listOf(listOf())) { acc, list ->
            acc.flatMap { prefix -> list.map { element -> prefix + element } }
        }
}

class UnionOverloadProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        UnionOverloadProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
}
