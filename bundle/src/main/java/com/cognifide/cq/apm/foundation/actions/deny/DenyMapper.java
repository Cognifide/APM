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
package com.cognifide.cq.apm.foundation.actions.deny;

import com.cognifide.cq.apm.api.actions.Action;
import com.cognifide.cq.apm.api.actions.BasicActionMapper;
import com.cognifide.cq.apm.api.actions.annotations.Mapping;

import java.util.Collections;
import java.util.List;

public class DenyMapper extends BasicActionMapper {

	public static final String REFERENCE = "This action is an complementary one for ALLOW action, and can be used to"
			+ " add deny permission for current authorizable on specified path.";

	@Mapping(
			value = {"DENY" + SPACE + PATH + SPACE + LIST},
			args = {"path", "permissions"},
			reference = REFERENCE
	)
	public Action mapAction(String path, List<String> permissions) {
		return mapAction(path, null, permissions, false);
	}

	@Mapping(
			value = {"DENY" + SPACE + PATH + SPACE + STRING},
			args = {"path", "permission"},
			reference = REFERENCE
	)
	public Action mapAction(String path, String permission) {
		return mapAction(path, null, Collections.singletonList(permission), false);
	}

	@Mapping(
			value = {"DENY" + SPACE + PATH + SPACE + GLOB + SPACE + LIST},
			args = {"path", "glob", "permissions"},
			reference = REFERENCE
	)
	public Action mapAction(String path, String glob, List<String> permissions) {
		return mapAction(path, glob, permissions, false);
	}

	@Mapping(
			value = {"DENY" + SPACE + PATH + SPACE + GLOB + SPACE + STRING},
			args = {"path", "glob", "permission"},
			reference = REFERENCE
	)
	public Action mapAction(String path, String glob, String permission) {
		return mapAction(path, glob, Collections.singletonList(permission), false);
	}

	@Mapping(
			value = {"DENY" + SPACE + PATH + SPACE + LIST + SPACE + ("IF" + DASH + "EXISTS")},
			args = {"path", "permissions"},
			reference = REFERENCE
	)
	public Action mapActionWithIfExists(String path, List<String> permissions) {
		return mapAction(path, null, permissions, true);
	}

	@Mapping(
			value = {"DENY" + SPACE + PATH + SPACE + GLOB + SPACE + LIST + SPACE + ("IF" + DASH + "EXISTS")},
			args = {"path", "glob", "permissions"},
			reference = REFERENCE
	)
	public Action mapActionWithIfExists(String path, String glob, List<String> permissions) {
		return mapAction(path, glob, permissions, true);
	}

	private Action mapAction(String path, String glob, List<String> permissions, Boolean ifExists) {
		return new Deny(path, glob, ifExists, permissions);
	}
}
