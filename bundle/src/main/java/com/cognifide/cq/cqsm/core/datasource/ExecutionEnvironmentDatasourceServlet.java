/*
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Cognifide Limited
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
package com.cognifide.cq.cqsm.core.datasource;

import com.cognifide.cq.cqsm.api.scripts.ExecutionEnvironment;
import com.cognifide.cq.cqsm.core.Property;
import com.cognifide.cq.cqsm.core.datasource.SimpleDataSourceBuilder.Option;
import javax.servlet.Servlet;
import org.osgi.service.component.annotations.Component;

@Component(
    immediate = true,
    service = Servlet.class,
    property = {
        Property.RESOURCE_TYPE + "apm/datasource/executionEnvironments",
        Property.DESCRIPTION + "Provides execution environments",
        Property.VENDOR
    }
)
public class ExecutionEnvironmentDatasourceServlet extends AbstractDatasourceServlet<ExecutionEnvironment> {

  public ExecutionEnvironmentDatasourceServlet() {
    super(ExecutionEnvironment.values(), new Option("Any", ""));
  }
}
