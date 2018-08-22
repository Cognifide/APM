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
package com.cognifide.cq.apm.core.jobs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.cognifide.cq.apm.api.logger.Progress;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

import java.util.concurrent.TimeUnit;

@Component(label = "Job results holder service", description = "Job results holder service", metatype = true)
@Service(JobResultsCache.class)
@Properties({@Property(name = Constants.SERVICE_VENDOR, value = "Cognifide Ltd")})
public class JobResultsCache {

	private static final long DEFAULT_EXPIRATION_TIME = 10;

	private Cache<String, Progress> cache;

	@Activate
	public void activate(final ComponentContext componentContext) {
		cache = CacheBuilder.newBuilder().expireAfterWrite(DEFAULT_EXPIRATION_TIME, TimeUnit.MINUTES).build();
	}

	public void put(String id, Progress progress) {
		cache.put(id, progress);
	}

	public Progress get(String id) {
		return cache.getIfPresent(id);
	}

}
