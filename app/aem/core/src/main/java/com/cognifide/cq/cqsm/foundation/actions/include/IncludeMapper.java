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
package com.cognifide.cq.cqsm.foundation.actions.include;

import com.cognifide.apm.api.actions.Action;
import com.cognifide.apm.api.actions.annotations.Mapper;
import com.cognifide.apm.api.actions.annotations.Mapping;
import com.cognifide.apm.api.actions.annotations.Required;
import com.cognifide.cq.cqsm.foundation.actions.ActionGroup;
import java.util.Collections;
import java.util.List;

@Mapper(value = "include", group = ActionGroup.CORE)
public final class IncludeMapper {

  public static final String REFERENCE = "This action is an complementary one for EXCLUDE action, and can be used to"
      + " add specified users and groups to current group.";

  @Mapping(
      examples = "INCLUDE 'authors'",
      reference = REFERENCE
  )
  public Action mapAction(@Required(value = "id", description = "user's or group's id e.g.: 'author'") String id) {
    return mapAction(Collections.singletonList(id));
  }

  @Mapping(
      examples = "INCLUDE ['authors']",
      reference = REFERENCE
  )
  public Action mapAction(
      @Required(value = "ids", description = "users' or groups' ids e.g.: ['author']") List<String> ids) {
    return new Include(ids);
  }
}
