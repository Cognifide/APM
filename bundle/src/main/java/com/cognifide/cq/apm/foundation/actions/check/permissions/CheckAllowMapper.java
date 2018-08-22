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
package com.cognifide.cq.apm.foundation.actions.check.permissions;

import com.cognifide.cq.apm.api.actions.Action;
import com.cognifide.cq.apm.api.actions.BasicActionMapper;
import com.cognifide.cq.apm.api.actions.annotations.Mapping;

import java.util.List;

public final class CheckAllowMapper extends BasicActionMapper {

	public static final String REFERENCE = "Check that specific permissions are allowed for current authorizable"
			+ " on specified path.";

	@Mapping(
			value = {"CHECK" + DASH + "ALLOW" + SPACE + STRING + SPACE + PATH + SPACE + LIST},
			args = {"authorizableId", "path", "permissions"},
			reference = REFERENCE
	)
	public Action mapAction(final String id, final String path, List<String> permissions) {
		return mapAction(id, path, null, permissions);
	}

	@Mapping(
			value = {"CHECK" + DASH + "ALLOW" + SPACE + STRING + SPACE + PATH + SPACE + STRING + SPACE + LIST},
			args = {"authorizableId", "path", "glob", "permissions"},
			reference = REFERENCE
	)
	public Action mapAction(final String id, final String path, String glob, List<String> permissions) {
		return new CheckPermissions(id, path, glob, permissions, true);
	}
}
