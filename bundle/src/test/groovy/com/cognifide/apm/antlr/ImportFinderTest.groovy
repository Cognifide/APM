package com.cognifide.apm.antlr

import com.cognifide.apm.antlr.parsedscript.ParsedScript
import com.cognifide.cq.cqsm.api.scripts.Script
import com.cognifide.cq.cqsm.api.scripts.ScriptFinder
import org.apache.sling.api.resource.ResourceResolver
import spock.lang.Specification

class ImportFinderTest extends Specification {

    def "find import and run_script"() {
        given:
        def script = createScript()
        def scriptFinder = Mock(ScriptFinder.class)
        def resolver = Mock(ResourceResolver.class)

        def mocked = Mock(Script)
        def anotherMocked = Mock(Script)
        scriptFinder.find("/dummy/import", resolver) >> mocked
        scriptFinder.find("/dummy/run", resolver) >> anotherMocked

        def importFinder = new ImportFinder(scriptFinder, resolver)

        when:
        def result = importFinder.findIncludes(script)

        then:
        result == [mocked, anotherMocked]
    }

    private ParsedScript createScript() {
        def content = """ IMPORT "/dummy/import"
                          RUN "/dummy/run" """
        def script = Mock(Script)
        script.path >> "/dummy/path"
        script.data >> content

        new ParsedScript.Factory().create(script)
    }
}
