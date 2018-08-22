/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.cognifide.cq.apm.foundation.actions.check.notexists;

import com.cognifide.cq.apm.api.actions.Action;
import com.cognifide.cq.apm.api.actions.BasicActionMapper;
import com.cognifide.cq.apm.api.actions.annotations.Mapping;

import java.util.Collections;
import java.util.List;

public final class CheckNotExistsMapper extends BasicActionMapper {

	public static final String REFERENCE = "Verify that specific authorizables do not exist.";

	@Mapping(
			value = {"CHECK" + DASH + "NOT" + DASH + "EXISTS" + SPACE + STRING},
			args = {"id"},
			reference = REFERENCE
	)
	public Action mapAction(final String id) {
		return mapAction(Collections.singletonList(id));
	}

	@Mapping(
			value = {"CHECK" + DASH + "NOT" + DASH + "EXISTS" + SPACE + LIST},
			args = {"ids"},
			reference = REFERENCE
	)
	public Action mapAction(final List<String> ids) {
		return new CheckNotExists(ids);
	}
}
