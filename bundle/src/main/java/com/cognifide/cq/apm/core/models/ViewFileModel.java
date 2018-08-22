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
package com.cognifide.cq.apm.core.models;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.models.annotations.Model;

import lombok.Getter;

@Model(adaptables = SlingHttpServletRequest.class)
public class ViewFileModel {

	private static final String FILENAME_PARAMETER_NAME = "filename";

	private static final String FILEPATH_PARAMETER_NAME = "filepath";

	@Getter
	private final String fileName;

	@Getter
	private final String filePath;

	public ViewFileModel(SlingHttpServletRequest request) {
		this.fileName = PropertiesUtil
				.toString(request.getParameter(FILENAME_PARAMETER_NAME), StringUtils.EMPTY);
		this.filePath = PropertiesUtil
				.toString(request.getParameter(FILEPATH_PARAMETER_NAME), StringUtils.EMPTY);
	}
}