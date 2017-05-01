package de.invesdwin.context.r.runtime.contract;

import org.springframework.core.io.Resource;

public interface IScriptTask {

    Resource getResource();

    Iterable<IScriptResultExpression<?>> getResultExpressions();

}
