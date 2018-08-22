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
package com.cognifide.cq.apm.foundation.actions.clearfromgroups;

import com.cognifide.cq.apm.api.actions.Action;
import com.cognifide.cq.apm.api.actions.BasicActionMapper;
import com.cognifide.cq.apm.api.actions.annotations.Mapping;

public final class ClearFromGroupsMapper extends BasicActionMapper {

	@Mapping(
			value = {"CLEAR" + DASH + "FROM" + DASH + "GROUPS"},
			reference = "This action removes all memberships of a given group."
	)
	public Action mapAction() {
		return new ClearFromGroups(ClearFromGroupOperationTypes.ALL_PARENTS);
	}

	@Mapping(
			value = {"CLEAR" + DASH + "FROM" + DASH + "GROUPS" + DASH + "ALL-CHILDREN"},
			reference = "This action removes given group membership from child groups."
	)

	public Action mapActionWithAllChildren() {
		return new ClearFromGroups(ClearFromGroupOperationTypes.ALL_CHILDREN);
	}

	@Mapping(
			value = {"CLEAR" + DASH + "FROM" + DASH + "GROUPS" + DASH + "ALL-PARENTS"},
			reference = "This action removes all memberships of a given group."
	)

	public Action mapActionWithAllParents() {
		return new ClearFromGroups(ClearFromGroupOperationTypes.ALL_PARENTS);
	}

	@Mapping(
			value = {"CLEAR" + DASH + "GROUPS"},
			reference = "This action removes all groups from a given group."
	)
	public Action mapActionForClearGroups() {
		return new ClearFromGroups(ClearFromGroupOperationTypes.ALL_CHILDREN);
	}
}
