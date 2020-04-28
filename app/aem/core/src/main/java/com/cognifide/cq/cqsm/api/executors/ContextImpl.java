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
package com.cognifide.cq.cqsm.api.executors;

import com.cognifide.apm.api.actions.ActionResult;
import com.cognifide.apm.api.actions.AuthorizableManager;
import com.cognifide.apm.api.actions.Context;
import com.cognifide.apm.api.actions.SessionSavingPolicy;
import com.cognifide.apm.api.exceptions.ActionExecutionException;
import com.cognifide.cq.cqsm.api.actions.ActionResultImpl;
import com.cognifide.cq.cqsm.core.sessions.SessionSavingPolicyImpl;
import com.cognifide.cq.cqsm.core.utils.AuthorizableManagerImpl;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.security.AccessControlManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;

public final class ContextImpl implements Context {

  @Getter
  private final AccessControlManager accessControlManager;

  @Getter
  private final AuthorizableManager authorizableManager;

  @Getter
  private final SessionSavingPolicy savingPolicy;

  @Getter
  private final JackrabbitSession session;

  @Setter
  private Authorizable currentAuthorizable;

  public ContextImpl(final JackrabbitSession session) throws RepositoryException {
    this.session = session;
    this.accessControlManager = session.getAccessControlManager();
    this.authorizableManager = new AuthorizableManagerImpl(session.getUserManager());
    this.savingPolicy = new SessionSavingPolicyImpl();
  }

  @Override
  public ValueFactory getValueFactory() throws RepositoryException {
    return session.getValueFactory();
  }

  @Override
  public Authorizable getCurrentAuthorizable() throws ActionExecutionException {
    if (currentAuthorizable == null) {
      throw new ActionExecutionException("Current authorizable not set.");
    }
    return currentAuthorizable;
  }

  @Override
  public Authorizable getCurrentAuthorizableIfExists() {
    return currentAuthorizable;
  }

  @Override
  public Group getCurrentGroup() throws ActionExecutionException {
    if (getCurrentAuthorizable() instanceof User) {
      throw new ActionExecutionException("Current authorizable is not a group");
    }
    return (Group) currentAuthorizable;
  }

  @Override
  public User getCurrentUser() throws ActionExecutionException {
    if (getCurrentAuthorizable() instanceof Group) {
      throw new ActionExecutionException("Current authorizable is not a user");
    }
    return (User) currentAuthorizable;
  }

  @Override
  public void clearCurrentAuthorizable() {
    this.currentAuthorizable = null;
  }

  @Override
  public ActionResult createActionResult() {
    return new ActionResultImpl();
  }
}