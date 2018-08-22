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

import com.cognifide.cq.apm.api.executors.Mode;
import com.cognifide.cq.apm.api.logger.Progress;
import com.cognifide.cq.apm.api.scripts.ScriptManager;
import com.cognifide.cq.apm.core.Apm;
import com.cognifide.cq.apm.core.utils.ServletUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

@SlingServlet(paths = {"/bin/apm/validate"})
@Service
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "APM Validation Servlet"),
		@Property(name = Constants.SERVICE_VENDOR, value = Apm.VENDOR_NAME)})
public class ScriptValidationServlet extends SlingAllMethodsServlet {

	@Reference
	private ScriptManager scriptManager;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final String content = request.getParameter("content");
		if (StringUtils.isEmpty(content)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			ServletUtils.writeMessage(response, "error", "Script content is required");
			return;
		}

		try {
			final Progress progress = scriptManager.evaluate(content, Mode.VALIDATION, request.getResourceResolver());
			if (progress.isSuccess()) {
				ServletUtils.writeMessage(response, "success", "Script passes validation");
			} else {
				final String message = progress.getLastError().getLastMessageText();
				final Map<String, Object> context = new HashMap<>();

				if (message != null) {
					context.put("error", message);
				}

				ServletUtils.writeMessage(response, "error", "Script does not pass validation", context);
			}
		} catch (RepositoryException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			ServletUtils.writeMessage(response, "error", String.format(
					"Script' cannot be validated because of " + "repository error: %s", e.getMessage()));
		}
	}
}