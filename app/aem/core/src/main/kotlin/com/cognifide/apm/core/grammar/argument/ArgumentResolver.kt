/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.cognifide.apm.core.grammar.argument

import com.cognifide.apm.core.grammar.*
import com.cognifide.apm.core.grammar.antlr.ApmLangParser.*
import com.cognifide.apm.core.grammar.common.getIdentifier
import com.cognifide.apm.core.grammar.executioncontext.VariableHolder
import com.google.common.primitives.Ints

class ArgumentResolver(private val variableHolder: VariableHolder) {

    private val singleArgumentResolver: SingleArgumentResolver

    init {
        this.singleArgumentResolver = SingleArgumentResolver()
    }

    fun resolve(context: ComplexArgumentsContext?): Arguments {
        return if (context != null) {
            val multiArgumentResolver = MultiArgumentResolver()
            multiArgumentResolver.visitComplexArguments(context)
            Arguments(multiArgumentResolver.required, multiArgumentResolver.named, multiArgumentResolver.flags)
        } else {
            Arguments()
        }
    }

    fun resolve(context: NamedArgumentsContext?): Arguments {
        return if (context != null) {
            val multiArgumentResolver = MultiArgumentResolver()
            multiArgumentResolver.visitNamedArguments(context)
            Arguments(multiArgumentResolver.required, multiArgumentResolver.named, multiArgumentResolver.flags)
        } else {
            Arguments()
        }
    }

    fun resolve(context: ArgumentContext?): ApmType {
        return if (context != null) {
            singleArgumentResolver.visitArgument(context)
        } else {
            ApmEmpty()
        }
    }

    private inner class MultiArgumentResolver : com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor<Unit>() {

        val required = mutableListOf<ApmType>()
        val named = mutableMapOf<String, ApmType>()
        val flags = mutableListOf<String>()

        override fun visitRequiredArgument(ctx: RequiredArgumentContext) {
            required.add(singleArgumentResolver.visitArgument(ctx.argument()))
        }

        override fun visitNamedArgument(ctx: NamedArgumentContext) {
            named[ctx.IDENTIFIER().toString()] = singleArgumentResolver.visitArgument(ctx.argument())
        }

        override fun visitFlag(ctx: FlagContext) {
            flags.add(getIdentifier(ctx.identifier()))
        }
    }

    private inner class SingleArgumentResolver : com.cognifide.apm.core.grammar.antlr.ApmLangBaseVisitor<ApmType>() {

        override fun defaultResult(): ApmType {
            return ApmEmpty()
        }

        override fun visitArray(ctx: ArrayContext): ApmType {
            val values = ctx.children
                    ?.map { child -> child.accept(this) }
                    ?.filterIsInstance<ApmString>()
                    ?.map { child -> child.string }
                    ?: listOf()
            return ApmList(values)
        }

        override fun visitExpression(ctx: ExpressionContext): ApmType {
            if (ctx.plus() != null) {
                val left = visit(ctx.expression(0))
                val right = visit(ctx.expression(1))
                return when {
                    left is ApmString && right is ApmString -> ApmString(left.string + right.string)
                    left is ApmString && right is ApmInteger -> ApmString(left.string + right.integer.toString())
                    left is ApmInteger && right is ApmString -> ApmString(left.integer.toString() + right.string)
                    left is ApmInteger && right is ApmInteger -> ApmInteger(left.integer + right.integer)
                    left is ApmList && right is ApmList -> ApmList(left.list + right.list)
                    else -> throw ArgumentResolverException("Operation not supported for given values $left and $right")
                }
            }
            return when {
                ctx.value() != null -> visit(ctx.value())
                ctx.array() != null -> visit(ctx.array())
                else -> super.visitExpression(ctx)
            }
        }

        override fun visitNumberValue(ctx: NumberValueContext): ApmType {
            val value = ctx.NUMBER_LITERAL().toString()
            val number = Ints.tryParse(value)
                    ?: throw ArgumentResolverException("Found invalid number value $value")
            return ApmInteger(number)
        }

        override fun visitStringValue(ctx: StringValueContext): ApmType {
            if (ctx.STRING_LITERAL() != null) {
                return ApmString(ctx.STRING_LITERAL().toPlainString())
            }
            throw ArgumentResolverException("Found invalid string value $ctx")
        }

        override fun visitVariable(ctx: VariableContext): ApmType {
            val name = ctx.IDENTIFIER().toString()
            return variableHolder[name]
                    ?: throw ArgumentResolverException("Variable \"$name\" not found")
        }
    }
}
