/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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

package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.ApmLangParser.*
import com.cognifide.apm.antlr.argument.ArgumentResolverException
import com.cognifide.apm.antlr.argument.Arguments
import com.cognifide.apm.antlr.argument.toPlainString
import com.cognifide.apm.antlr.common.getIdentifier
import com.cognifide.apm.antlr.executioncontext.ExecutionContext
import com.cognifide.apm.antlr.executioncontext.ExecutionContextException
import com.cognifide.apm.antlr.parsedscript.InvalidSyntaxException
import com.cognifide.apm.antlr.parsedscript.InvalidSyntaxMessageFactory
import com.cognifide.apm.antlr.variable.ImportVariable
import com.cognifide.cq.cqsm.api.logger.Progress
import com.cognifide.cq.cqsm.api.logger.Status
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import org.apache.sling.api.resource.ResourceResolver

class ScriptRunner(
        private val scriptFinder: ScriptFinder,
        private val resourceResolver: ResourceResolver,
        private val actionInvoker: ActionInvoker) {

    @JvmOverloads
    fun execute(script: Script, progress: Progress, initialDefinitions: Map<String, String> = mapOf()): Progress {
        try {
            val executionContext = ExecutionContext.create(scriptFinder, resourceResolver, script, progress)
            initialDefinitions.forEach { (name, value) -> executionContext.setVariable(name, ApmString(value)) }
            val executor = Executor(executionContext)
            executor.visit(executionContext.root.apm)
        } catch (e: InvalidSyntaxException) {
            val errorMessages = InvalidSyntaxMessageFactory.detailedSyntaxError(e)
            progress.addEntry(Status.ERROR, errorMessages)
        } catch (e: ArgumentResolverException) {
            progress.addEntry(Status.ERROR, e.message)
        } catch (e: ExecutionContextException) {
            progress.addEntry(Status.ERROR, e.message)
        }
        return progress
    }

    private inner class Executor(private val executionContext: ExecutionContext) : ApmLangBaseVisitor<Unit>() {

        override fun visitDefineVariable(ctx: DefineVariableContext) {
            val variableName = ctx.IDENTIFIER().toString()
            val variableValue = executionContext.resolveArgument(ctx.argument())
            executionContext.setVariable(variableName, variableValue)
            info("define", "Defined variable: $variableName= $variableValue")
        }

        override fun visitForEach(ctx: ForEachContext) {
            val index = ctx.IDENTIFIER().toString()
            val values: List<ApmValue> = readValues(ctx)
            for ((iteration, value) in values.withIndex()) {
                try {
                    executionContext.createLocalContext()
                    info("foreach", "$iteration. Begin: $index= $value")
                    executionContext.setVariable(index, value)
                    visit(ctx.body())
                    info("foreach", "$iteration. End")
                } finally {
                    executionContext.removeLocalContext()
                }
            }
        }

        override fun visitRunScript(ctx: RunScriptContext) {
            val path = ctx.path().STRING_LITERAL().toPlainString()
            val arguments = executionContext.resolveArguments(ctx.namedArguments())
            val loadScript = executionContext.loadScript(path)
            executionContext.createScriptContext(loadScript)
            try {
                arguments.named.forEach { (key, value) -> executionContext.setVariable(key, value) }
                info("run", "Begin: path= ${loadScript.path}", arguments)
                visit(loadScript.apm)
                info("run", "End")
            } finally {
                executionContext.removeScriptContext()
            }
        }

        private fun readValues(ctx: ForEachContext): List<ApmValue> =
             when (val variableValue = executionContext.resolveArgument(ctx.argument())) {
                is ApmList -> variableValue.list.map { ApmString(it) }
                is ApmEmpty -> listOf()
                else -> listOf(variableValue as ApmValue)
            }


        override fun visitGenericCommand(ctx: GenericCommandContext) {
            val commandName = getIdentifier(ctx.commandName().identifier()).toUpperCase()
            val arguments = executionContext.resolveArguments(ctx.complexArguments())
            actionInvoker.runAction(executionContext.progress, commandName, arguments)
        }

        override fun visitImportScript(ctx: ImportScriptContext) {
            val result = ImportVariable(executionContext).importAllVariables(ctx)

            result.variables.forEach { (name, value) ->
                executionContext.setVariable(name, value)
            }

            executionContext.progress.addEntry(Status.SUCCESS, importScriptMessages(result))
        }

        private fun importScriptMessages(importResult: ImportVariable.Result): List<String> {
            val messages = mutableListOf(importScriptOpenMessage(importResult))
            messages.addAll(
                    importResult
                            .variables
                            .map { "Imported variable: ${it.key} = ${it.value}" })

            return messages.toList()

        }

        private fun importScriptOpenMessage(importResult: ImportVariable.Result): String {
            val msg = "Import from script ${importResult.path}"

            return when(importResult.ns){
                null -> msg
                else -> msg + " with namespace: ${importResult.ns}"
            } + ". Notice, only DEFINE action will be processed!"
        }

        private fun info(command: String, details: String = "", arguments: Arguments? = null) {
            if (arguments != null) {
                executionContext.progress.addEntry(Status.SUCCESS, details, command, "", arguments)
            } else {
                executionContext.progress.addEntry(Status.SUCCESS, details, command)
            }
        }
    }
}