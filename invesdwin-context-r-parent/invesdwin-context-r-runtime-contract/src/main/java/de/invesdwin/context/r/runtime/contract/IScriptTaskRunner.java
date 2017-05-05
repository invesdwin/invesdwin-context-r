package de.invesdwin.context.r.runtime.contract;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunner {

    Log LOG = new Log(IScriptTaskRunner.class);

    IScriptResults run(IScriptTask task);

}
