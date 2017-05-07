package de.invesdwin.context.r.runtime.contract;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunner {

    Log LOG = new Log(IScriptTaskRunner.class);

    IScriptTaskResults run(AScriptTask scriptTask);

}
