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
package com.cognifide.cq.apm.core.servlets;

import com.cognifide.cq.apm.core.Apm;
import com.cognifide.cq.apm.core.models.FileModel;
import com.cognifide.cq.apm.core.models.ImportInitModel;
import com.cognifide.cq.apm.core.utils.ServletUtils;

import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

@SlingServlet(paths = {"/bin/apm/list"}, methods = {"GET"})
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "APM List Servlet"),
		@Property(name = Constants.SERVICE_VENDOR, value = Apm.VENDOR_NAME)
})
public class ScriptListServlet extends SlingAllMethodsServlet {

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		List<FileModel> files = request.adaptTo(ImportInitModel.class).getFiles();
		ServletUtils.writeJson(response, files);
	}
}
