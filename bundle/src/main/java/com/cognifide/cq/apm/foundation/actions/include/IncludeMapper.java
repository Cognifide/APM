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
package com.cognifide.cq.apm.foundation.actions.include;

import com.cognifide.cq.apm.api.actions.Action;
import com.cognifide.cq.apm.api.actions.BasicActionMapper;
import com.cognifide.cq.apm.api.actions.annotations.Mapping;
import com.cognifide.cq.apm.api.exceptions.ActionCreationException;

import java.util.Collections;
import java.util.List;

public final class IncludeMapper extends BasicActionMapper {

	public static final String REFERENCE = "This action is an complementary one for EXCLUDE action, and can be used to"
			+ " add specified users and groups to current group.";

	@Mapping(
			value = {"INCLUDE" + SPACE + STRING},
			args = {"authorizableId"},
			reference = REFERENCE
	)
	public Action mapAction(final String id) throws ActionCreationException {
		return mapAction(Collections.singletonList(id));
	}

	@Mapping(
			value = {"INCLUDE" + SPACE + LIST},
			args = {"authorizableIds"},
			reference = REFERENCE
	)
	public Action mapAction(final List<String> ids) throws ActionCreationException {
		return new Include(ids);
	}
}
