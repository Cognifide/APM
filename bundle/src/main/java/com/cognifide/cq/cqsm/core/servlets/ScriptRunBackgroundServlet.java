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
package com.cognifide.cq.cqsm.core.servlets;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.cognifide.cq.cqsm.api.scriptrunnerjob.JobProgressOutput;
import com.cognifide.cq.cqsm.core.jobs.ScriptRunnerJobManager;
import com.cognifide.cq.cqsm.core.utils.ServletUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.Job;
import org.osgi.framework.Constants;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

@SlingServlet(paths = {"/bin/cqsm/run-background"}, methods = {"GET", "POST"})
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "CQSM Servlet for running scripts in background and checking theirs status"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd")})
public class ScriptRunBackgroundServlet extends SlingAllMethodsServlet {

	private static final String BACKGROUND_RESPONSE_TYPE = "background";

	private static final String ERROR_RESPONSE_TYPE = "error";

	private static final String FILE_REQUEST_PARAMETER = "file";

	private static final String MODE_REQUEST_PARAMETER = "mode";

	private static final String ID_REQUEST_PARAMETER = "id";

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Reference
	private ScriptRunnerJobManager scriptRunnerJobManager;

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		BackgroundJobParameters parameters = getParameters(request, response);
		if (parameters == null) {
			return;
		}

		Job job = scriptRunnerJobManager.scheduleJob(parameters);
		ServletUtils.writeMessage(response, BACKGROUND_RESPONSE_TYPE, BACKGROUND_RESPONSE_TYPE, createMapWithJobIdKey(job));
	}

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		final String id = request.getParameter(ID_REQUEST_PARAMETER);
		if (id == null) {
			return;
		}
		JobProgressOutput jobProgressOutput = scriptRunnerJobManager.checkJobStatus(id);
		ServletUtils.writeJson(response, gson.toJson(jobProgressOutput));
	}

	private BackgroundJobParameters getParameters(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) throws IOException {
		final String searchPath = request.getParameter(FILE_REQUEST_PARAMETER);
		final String modeName = request.getParameter(MODE_REQUEST_PARAMETER);
		final String userName = request.getResourceResolver().getUserID();

		if (StringUtils.isEmpty(searchPath)) {
			ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE,
					"Please set the script file name: -d \"file=[name]\"");
			return null;
		}

		if (StringUtils.isEmpty(modeName)) {
			ServletUtils.writeMessage(response, ERROR_RESPONSE_TYPE, "Running mode not specified.");
			return null;
		}

		return new BackgroundJobParameters(searchPath, modeName, userName);
	}

	private Map<String, Object> createMapWithJobIdKey(Job job) {
		return ImmutableMap.<String, Object>builder().put(ID_REQUEST_PARAMETER, job.getId()).build();
	}

}
