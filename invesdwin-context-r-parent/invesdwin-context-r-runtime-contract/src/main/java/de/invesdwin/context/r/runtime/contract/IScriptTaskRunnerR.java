package de.invesdwin.context.r.runtime.contract;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunnerR {

    Log LOG = new Log(IScriptTaskRunnerR.class);

    <T> T run(AScriptTaskR<T> scriptTask);

}
