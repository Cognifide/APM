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

package com.cognifide.apm.core.grammar.parsedscript


import com.cognifide.apm.api.scripts.Script
import org.apache.commons.io.IOUtils
import spock.lang.Specification
import spock.lang.Unroll

class ParsedScriptTest extends Specification {

    @Unroll
    def "run script #file"(String file, List<String> output) {
        given:
        def script = Mock(Script)
        script.data >> IOUtils.toString((InputStream) getClass().getResourceAsStream(file))
        script.path >> file

        when:
        new ParsedScript.Factory().create(script)

        then:
        def e = thrown(InvalidSyntaxException)
        def error = new InvalidSyntaxMessageFactory().detailedSyntaxError(e)
        error == output

        where:
        file            | output
        "/invalid1.apm" | ["Invalid line [20:7]: DEFINE \$ nana", "Invalid sequence: \$"]
        "/invalid2.apm" | ["Invalid line [20:7]: DEFINE / nana"]
    }
}
