package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.argument.toPlainString
import com.cognifide.apm.antlr.parsedscript.ParsedScript
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import org.apache.sling.api.resource.ResourceResolver

class ImportFinder(val scriptFinder: ScriptFinder, val resolver: ResourceResolver) {

    fun findIncludes(parsedScript: ParsedScript): List<Script> {
        val finder = IncludesFinder()
        finder.visit(parsedScript.apm)
//        ParsedScript.create()
        return finder.scripts
    }

    private inner class IncludesFinder : ApmLangBaseVisitor<Unit>() {
        val scripts = mutableListOf<Script>()

        override fun visitImportScript(ctx: ApmLangParser.ImportScriptContext) {
            val path = ctx.path().STRING_LITERAL().toPlainString()
            val script = scriptFinder.find(path, resolver)
            this.scripts.add(script)
        }

        override fun visitRunScript(ctx: ApmLangParser.RunScriptContext) {
            val path = ctx.path().STRING_LITERAL().toPlainString()
            val script = scriptFinder.find(path, resolver)
            this.scripts.add(script)
        }
    }
}