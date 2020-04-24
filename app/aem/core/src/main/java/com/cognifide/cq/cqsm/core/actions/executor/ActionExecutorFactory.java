package com.cognifide.cq.cqsm.core.actions.executor;

import com.cognifide.apm.api.actions.Context;
import com.cognifide.cq.cqsm.api.actions.ActionFactory;
import com.cognifide.apm.api.services.Mode;

public final class ActionExecutorFactory {

  public static ActionExecutor create(Mode mode, Context context, ActionFactory actionFactory) {
    switch (mode) {
      case AUTOMATIC_RUN:
      case RUN:
        return new RunActionExecutor(context, actionFactory);
      case DRY_RUN:
        return new DryRunActionExecutor(context, actionFactory);
      default:
        return new ValidationActionExecutor(context, actionFactory);
    }
  }
}