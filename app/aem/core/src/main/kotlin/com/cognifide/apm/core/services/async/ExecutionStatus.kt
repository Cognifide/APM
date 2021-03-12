/*-
 * ========================LICENSE_START=================================
 * AEM Permission Management
 * %%
 * Copyright (C) 2013 Wunderman Thompson Technology
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
package com.cognifide.apm.core.services.async

import com.cognifide.apm.api.services.ExecutionResult

sealed class ExecutionStatus(val status: String)
class RunningExecution : ExecutionStatus("running")
class UnknownExecution : ExecutionStatus("unknown")
class FinishedSuccessfulExecution(val path: String, val entries: List<ExecutionResult.Entry>) : ExecutionStatus("finished")
class FinishedFailedExecution(val path: String, val entries: List<ExecutionResult.Entry>, val error: ExecutionResult.Entry) : ExecutionStatus("finished")