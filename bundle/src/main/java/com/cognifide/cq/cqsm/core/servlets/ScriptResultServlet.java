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

import com.cognifide.cq.cqsm.core.Property;

import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

@Component(
		immediate = true,
		service = Servlet.class,
		property = {
				Property.PATH + "/bin/cqsm/executionResultDownload",
				Property.METHOD + "POST",
				Property.DESCRIPTION + "Execution result Servlet",
				Property.VENDOR
		}
)
public class ScriptResultServlet extends SlingAllMethodsServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScriptResultServlet.class);

	private static final int BYTES_DOWNLOAD = 1024;

	public static final String EXECUTION_RESULT_SERVLET_PATH = "/bin/cqsm/executionResultDownload";

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		String fileName = request.getParameter("filename");
		String content = request.getParameter("content");

		if (fileName == null || fileName.length() == 0) {
			LOGGER.error("Parameter fileName is required");
			return;
		}

		response.setContentType("application/octet-stream"); // Your content type
		response.setHeader("Content-Disposition",
				"attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

		InputStream input = IOUtils.toInputStream(content);

		int read = 0;
		byte[] bytes = new byte[BYTES_DOWNLOAD];
		OutputStream os = response.getOutputStream();

		while ((read = input.read(bytes)) != -1) {
			os.write(bytes, 0, read);
		}
		input.close();
		os.flush();
		os.close();
	}

}
