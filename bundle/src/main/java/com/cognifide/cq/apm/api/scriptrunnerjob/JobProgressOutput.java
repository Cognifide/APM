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
package com.cognifide.cq.apm.api.scriptrunnerjob;

import com.cognifide.cq.apm.api.logger.ProgressEntry;
import com.cognifide.cq.apm.core.jobs.ScriptRunnerJobStatus;

import java.util.List;

import lombok.Getter;

public class JobProgressOutput {

	@Getter
	private String type;

	@Getter
	private List<ProgressEntry> entries;

	public JobProgressOutput(ScriptRunnerJobStatus status) {
		this.type = status.toString();
	}

	public JobProgressOutput(ScriptRunnerJobStatus status, List<ProgressEntry> entries) {
		this.entries = entries;
		this.type = status.toString();
	}
}
