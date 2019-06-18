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
package com.cognifide.cq.cqsm.core.history;

import com.cognifide.cq.cqsm.api.executors.Mode;
import com.cognifide.cq.cqsm.api.history.HistoryEntry;
import com.cognifide.cq.cqsm.api.history.InstanceDetails;
import com.cognifide.cq.cqsm.api.logger.Progress;
import com.cognifide.cq.cqsm.api.scripts.Script;
import java.util.Calendar;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public interface History {

	/**
	 * Save detailed script execution on a remote host as entry
	 */
	HistoryEntry logRemote(Script script, Mode mode, Progress progressLogger, InstanceDetails instanceDetails,
		Calendar executionTime);

	/**
	 * Save detailed script execution as entry
	 */
	HistoryEntry log(Script script, Mode mode, Progress progressLogger);

	/**
	 * Replicate log entry from publish to author instance
	 */
	void replicate(HistoryEntry entry, String executor) throws RepositoryException;

	/**
	 * Get all logged entries
	 */
	List<HistoryEntry> findAll();

	List<Resource> findAllResources(ResourceResolver resourceResolver);

	List<Resource> findResources(ResourceResolver resourceResolver, Pagination pagination);

	/**
	 * Find entry by its unique file name
	 */
	HistoryEntry find(String path);
}