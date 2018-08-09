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
package com.cognifide.cq.cqsm.foundation.actions.check.include;

import com.cognifide.cq.cqsm.api.actions.Action;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapper;
import com.cognifide.cq.cqsm.api.actions.annotations.Mapping;
import java.util.Collections;
import java.util.List;

@Mapper("check_includes")
public final class CheckIncludesMapper {

  public static final String REFERENCE = "Verify that provided group contains all listed authorizables.";

  @Mapping(
      args = {"authorizableId", "groupId"},
      reference = REFERENCE
  )
  public Action mapAction(final String id, final String group) {
    return mapAction(id, Collections.singletonList(group));
  }

  @Mapping(
      args = {"authorizableId", "groupIds"},
      reference = REFERENCE
  )
  public Action mapAction(final String id, final List<String> groups) {
    return new CheckIncludes(id, groups);
  }
}
